package mn.foreman.prospector.antminer;

import mn.foreman.prospector.model.MinerType;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * An {@link AntminerType} provides a {@link MinerType} implementation that
 * contains all of the known Antminer types.
 */
public enum AntminerType
        implements MinerType {

    /** The Antminer A3. */
    ANTMINER_A3("Antminer A3"),

    /** The Antminer B3. */
    ANTMINER_B3("Antminer B3"),

    /** The Antminer B7. */
    ANTMINER_B7("Antminer B7"),

    /** The Antminer D3. */
    ANTMINER_D3("Antminer D3"),

    /** The Antminer DR3. */
    ANTMINER_DR3("Antminer DR3"),

    /** The Antminer E3. */
    ANTMINER_E3("Antminer E3"),

    /** The Antminer L3. */
    ANTMINER_L3("Antminer L3"),

    /** The Antminer S7. */
    ANTMINER_S7("Antminer S7"),

    /** The Antminer S9. */
    ANTMINER_S9("Antminer S9"),

    /** The Antminer S15. */
    ANTMINER_S15("Antminer S15"),

    /** The Antminer S17. */
    ANTMINER_S17("Antminer S17"),

    /** The Antminer T9. */
    ANTMINER_T9("Antminer T9"),

    /** The Antminer T15. */
    ANTMINER_T15("Antminer T15"),

    /** The Antminer T17. */
    ANTMINER_T17("Antminer T17"),

    /** The Antminer X3. */
    ANTMINER_X3("Antminer X3"),

    /** The Antminer Z9. */
    ANTMINER_Z9("Antminer Z9"),

    /** The Antminer Z11. */
    ANTMINER_Z11("Antminer Z11"),

    /** The BraiinsOS Antminer S9. */
    BRAIINS_S9("braiins-am1-s9", "Antminer S9");

    /** All of the types, by string, mapped to their type. */
    private static final Map<String, AntminerType> TYPE_MAP =
            new ConcurrentHashMap<>();

    static {
        for (final AntminerType asicType : values()) {
            TYPE_MAP.put(asicType.identifier, asicType);
        }
    }

    /** The miner identifier. */
    private final String identifier;

    /** The miner name. */
    private final String name;

    /**
     * Constructor.
     *
     * @param identifier The identifier.
     * @param name       The miner name.
     */
    AntminerType(
            final String identifier,
            final String name) {
        this.identifier = identifier;
        this.name = name;
    }

    /**
     * Constructor.
     *
     * @param name The miner name.
     */
    AntminerType(final String name) {
        this(name, name);
    }

    /**
     * Converts the provided model to an {@link AntminerType}.
     *
     * @param model The model.
     *
     * @return The corresponding {@link AntminerType}.
     */
    public static Optional<AntminerType> forModel(final String model) {
        if (model != null && !model.isEmpty()) {
            return TYPE_MAP.entrySet()
                    .stream()
                    .filter(entry -> model.startsWith(entry.getKey()))
                    .map(Map.Entry::getValue)
                    .findFirst();
        }
        return Optional.empty();
    }

    @Override
    public String getCategory() {
        return "ASIC";
    }

    /**
     * Returns the identifier.
     *
     * @return The identifier.
     */
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String getName() {
        return this.name;
    }
}