package mn.foreman.prospector.util;

import mn.foreman.prospector.model.Miner;
import mn.foreman.prospector.prospect.Prospector;
import mn.foreman.util.FakeMinerServer;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * An {@link AbstractProspectorTest} provides n testing framework for validating
 * {@link Prospector Prospectors} against canned miner responses.
 */
public abstract class AbstractProspectorTest {

    /** The {@link Miner} that should be found. */
    private final Miner expectedMiner;

    /** The port to query that will find a {@link Miner}. */
    private final int foundPort;

    /** The IP to query. */
    private final String ipAddress;

    /** The server factory. */
    private final MinerServerFactory minerServerFactory;

    /** A port to query that will fail to find a {@link Miner}. */
    private final int notFoundPort;

    /** The {@link Prospector} under test. */
    private final Prospector prospector;

    /**
     * Constructor.
     *
     * @param ipAddress          The IP address.
     * @param foundPort          The port to query that will find a {@link
     *                           Miner}.
     * @param notFoundPort       The port to query that won't find a {@link
     *                           Miner}.
     * @param prospector         The {@link Prospector} under test.
     * @param minerServerFactory The factory.
     * @param expectedMiner      The {@link Miner} that should be found.
     */
    public AbstractProspectorTest(
            final String ipAddress,
            final int foundPort,
            final int notFoundPort,
            final Prospector prospector,
            final MinerServerFactory minerServerFactory,
            final Miner expectedMiner) {
        this.ipAddress = ipAddress;
        this.foundPort = foundPort;
        this.notFoundPort = notFoundPort;
        this.prospector = prospector;
        this.minerServerFactory = minerServerFactory;
        this.expectedMiner = expectedMiner;
    }

    /** Tests finding a {@link Miner}. */
    @Test
    public void testFound() throws Exception {
        runTest(
                this.foundPort,
                this.expectedMiner);
    }

    /** Tests not finding a {@link Miner}. */
    @Test
    public void testNotFound() throws Exception {
        runTest(
                this.notFoundPort,
                null);
    }

    /**
     * Tests finding a {@link Miner}.
     *
     * @param port          The API port to query.
     * @param expectedMiner The expected {@link Miner}.
     */
    private void runTest(
            final int port,
            final Miner expectedMiner) throws Exception {
        try (final FakeMinerServer fakeMinerServer =
                     this.minerServerFactory.create()) {
            fakeMinerServer.start();
            assertEquals(
                    expectedMiner,
                    this.prospector.scan(
                            this.ipAddress,
                            port).orElse(null));
            if (expectedMiner != null) {
                assertTrue(
                        fakeMinerServer.waitTillDone(
                                10,
                                TimeUnit.SECONDS));
            }
        }
    }
}