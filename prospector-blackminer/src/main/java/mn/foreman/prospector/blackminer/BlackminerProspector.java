package mn.foreman.prospector.blackminer;

import mn.foreman.cgminer.CgMiner;
import mn.foreman.cgminer.request.CgMinerCommand;
import mn.foreman.cgminer.request.CgMinerRequest;
import mn.foreman.model.error.MinerException;
import mn.foreman.prospector.model.Miner;
import mn.foreman.prospector.model.MinerImpl;
import mn.foreman.prospector.prospect.EmptySiteException;
import mn.foreman.prospector.prospect.Prospector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * An {@link BlackminerProspector} provides a {@link Prospector} implementation
 * that will query the provided IP and port, determining the Blackminer model
 * that's present if a response is returned.
 */
public class BlackminerProspector
        implements Prospector {

    /** The logger for this class. */
    private static final Logger LOG =
            LoggerFactory.getLogger(BlackminerProspector.class);

    @Override
    public Optional<Miner> scan(
            final String ipAddress,
            final int apiPort) {
        Miner miner = null;
        try {
            final Map<String, List<Map<String, String>>> responseValues =
                    new HashMap<>();
            final CgMiner cgMiner =
                    new CgMiner.Builder()
                            .setApiIp(ipAddress)
                            .setApiPort(Integer.toString(apiPort))
                            .setConnectTimeout(
                                    1,
                                    TimeUnit.SECONDS)
                            .addRequest(
                                    new CgMinerRequest.Builder()
                                            .setCommand(CgMinerCommand.VERSION)
                                            .build(),
                                    (builder, response) ->
                                            responseValues.putAll(
                                                    response.getValues()))
                            .build();

            // Attempt to query the miner for stats.  If there's a response,
            // then we found something
            cgMiner.getStats();

            final Optional<BlackminerType> type =
                    getModel(
                            responseValues);
            if (type.isPresent()) {
                miner =
                        new MinerImpl.Builder()
                                .setIpAddress(ipAddress)
                                .setApiPort(apiPort)
                                .setType(type.get())
                                .build();
            }
        } catch (final MinerException | EmptySiteException e) {
            LOG.debug("No Blackminer found on {}:{}", ipAddress, apiPort);
        }

        return Optional.ofNullable(miner);
    }

    /**
     * Gets the model from the provided values.
     *
     * @param values The values to parse.
     *
     * @return The model.
     *
     * @throws EmptySiteException on non-Blackminer response.
     */
    private static Optional<BlackminerType> getModel(
            final Map<String, List<Map<String, String>>> values)
            throws EmptySiteException {
        final Map<String, String> versions =
                values
                        .entrySet()
                        .stream()
                        .filter(entry -> entry.getKey().equals("VERSION"))
                        .map(Map.Entry::getValue)
                        .flatMap(List::stream)
                        .filter(map -> map.containsKey("Type"))
                        .findFirst()
                        .orElseThrow(EmptySiteException::new);
        return BlackminerType.forModel(versions.get("Type"));
    }
}