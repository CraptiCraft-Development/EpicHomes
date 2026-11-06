package me.loving11ish.epichomes.versionsystems;

import me.loving11ish.epichomes.utils.MessageUtils;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public enum ServerVersion {

    v1_8_R1(MCVersion.MC_1_8),
    v1_8_R2(MCVersion.MC_1_8_3),
    v1_8_R3(MCVersion.MC_1_8_8),

    v1_9_R1(MCVersion.MC_1_9),
    v1_9_R2(MCVersion.MC_1_9_4),

    v1_10_R1(MCVersion.MC_1_10),
    v1_11_R1(MCVersion.MC_1_11),
    v1_12_R1(MCVersion.MC_1_12),

    v1_13_R1(MCVersion.MC_1_13),
    v1_13_R2(MCVersion.MC_1_13_2),

    v1_14_R1(MCVersion.MC_1_14),
    v1_15_R1(MCVersion.MC_1_15),

    v1_16_R1(MCVersion.MC_1_16),
    v1_16_R2(MCVersion.MC_1_16_2),
    v1_16_R3(MCVersion.MC_1_16_5),

    v1_17_R1(MCVersion.MC_1_17),

    v1_18_R1(MCVersion.MC_1_18),
    v1_18_R2(MCVersion.MC_1_18_2),

    v1_19_R1(MCVersion.MC_1_19),
    v1_19_R2(MCVersion.MC_1_19_3),
    v1_19_R3(MCVersion.MC_1_19_4),

    v1_20_R1(MCVersion.MC_1_20),
    v1_20_R2(MCVersion.MC_1_20_2),
    v1_20_R3(MCVersion.MC_1_20_3),
    v1_20_R4(MCVersion.MC_1_20_5),
    v1_20_R5(MCVersion.MC_1_20_6),

    v1_21_R1(MCVersion.MC_1_21),
    v1_21_R2(MCVersion.MC_1_21_1),
    v1_21_R3(MCVersion.MC_1_21_2),
    v1_21_R4(MCVersion.MC_1_21_3),
    v1_21_R5(MCVersion.MC_1_21_4),
    v1_21_R6(MCVersion.MC_1_21_5),
    v1_21_R7(MCVersion.MC_1_21_6),
    v1_21_R8(MCVersion.MC_1_21_7),
    v1_21_R9(MCVersion.MC_1_21_8),
    v1_21_R10(MCVersion.MC_1_21_9),
    v1_21_R11(MCVersion.MC_1_21_10),
    v1_21_R12(MCVersion.MC_1_21_11),

    v26_1_R1(MCVersion.MC_26_1),
    v26_1_R2(MCVersion.MC_26_1_1),
    v26_2_R1(MCVersion.MC_26_2),
    v26_3_R1(MCVersion.MC_26_3),
    v26_3_R2(MCVersion.MC_26_3_1),
    v26_3_R3(MCVersion.MC_26_3_2),
    v26_3_R4(MCVersion.MC_26_3_3),
    v26_3_R5(MCVersion.MC_26_3_4),
    v27_1_R1(MCVersion.MC_27_1),

    Other(MCVersion.UNKNOWN, "Other");

    private static final Map<String, ServerVersion> BY_MINECRAFT_VERSION = new HashMap<>();
    private static final Map<MCVersion, ServerVersion> BY_MC_VERSION = new EnumMap<>(MCVersion.class);

    static {
        for (ServerVersion serverVersion : values()) {
            if (serverVersion == Other) {
                continue;
            }

            BY_MINECRAFT_VERSION.put(
                    serverVersion.minecraftVersion.toLowerCase(Locale.ROOT),
                    serverVersion
            );
            BY_MC_VERSION.put(serverVersion.mcVersion, serverVersion);
        }
    }

    private final MCVersion mcVersion;
    private final String minecraftVersion;
    private final int firstVersionNumber;
    private final int secondVersionNumber;
    private final int patchVersionNumber;

    ServerVersion(MCVersion mcVersion) {
        this(mcVersion, mcVersion.getName());
    }

    ServerVersion(MCVersion mcVersion, String minecraftVersion) {
        this.mcVersion = mcVersion;
        this.minecraftVersion = minecraftVersion;

        int[] parsed = parseVersionParts(minecraftVersion);
        this.firstVersionNumber = parsed[0];
        this.secondVersionNumber = parsed[1];
        this.patchVersionNumber = parsed[2];
    }

    public boolean serverVersionEqual(ServerVersion version) {
        return this == version;
    }

    public boolean serverVersionGreaterThanOrEqual(ServerVersion version) {
        return compareVersions(this, version) >= 0;
    }

    public boolean serverVersionGreaterThan(ServerVersion version) {
        return compareVersions(this, version) > 0;
    }

    public boolean serverVersionLessThan(ServerVersion version) {
        return compareVersions(this, version) < 0;
    }

    public boolean serverVersionGreaterThan(ServerVersion version1, ServerVersion version2) {
        return compareVersions(version1, version2) > 0;
    }

    public boolean serverVersionLessThan(ServerVersion version1, ServerVersion version2) {
        return compareVersions(version1, version2) < 0;
    }

    private static int compareVersions(ServerVersion version1, ServerVersion version2) {
        return Integer.compare(version1.mcVersion.getVersionID(), version2.mcVersion.getVersionID());
    }

    public String getServerVersionName() {
        return this.name();
    }

    public String getMinecraftVersion() {
        return minecraftVersion;
    }

    public int getOrdinalServerVersionNumber() {
        return this.ordinal();
    }

    /**
     * @return Backwards-compatible with old behaviour:
     * - 1.21.x returns 21
     * - 26.1.x returns 26
     */
    public int getServerMajorVersionNumber() {
        if (this == Other) {
            return -1;
        }
        return isLegacyVersionScheme() ? secondVersionNumber : firstVersionNumber;
    }

    public int getFirstVersionNumber() {
        return firstVersionNumber;
    }

    public int getSecondVersionNumber() {
        return secondVersionNumber;
    }

    public int getPatchVersionNumber() {
        return patchVersionNumber;
    }

    public boolean isLegacyVersionScheme() {
        return firstVersionNumber == 1;
    }

    public boolean isNewVersionScheme() {
        return firstVersionNumber >= 25;
    }

    public MCVersion getMCVersion() {
        return mcVersion;
    }

    public static ServerVersion fromMinecraftVersion(String versionString) {
        MessageUtils.sendDebugConsole("-----------------------------------------");
        MessageUtils.sendDebugConsole("ServerVersion lookup requested for input: " + versionString);

        if (versionString == null || versionString.trim().isEmpty()) {
            MessageUtils.sendDebugConsole("Input version is null/empty. Returning: " + Other);
            return Other;
        }

        String normalized = versionString.trim();
        MessageUtils.sendDebugConsole("Normalized version string: " + normalized);

        ServerVersion directMatch = BY_MINECRAFT_VERSION.get(normalized.toLowerCase(Locale.ROOT));
        if (directMatch != null) {
            MessageUtils.sendDebugConsole("Direct minecraftVersion map match found: " + directMatch);
            return directMatch;
        }

        MCVersion mcVersion = resolveMCVersion(normalized);
        MessageUtils.sendDebugConsole("Resolved MCVersion: " + mcVersion);

        if (mcVersion != MCVersion.UNKNOWN) {
            ServerVersion exactMapped = BY_MC_VERSION.get(mcVersion);
            if (exactMapped != null) {
                MessageUtils.sendDebugConsole("Exact MCVersion -> ServerVersion map match found: " + exactMapped);
                return exactMapped;
            }

            int[] mcParts = parseVersionParts(mcVersion.getName());
            if (mcParts[0] >= 0) {
                ServerVersion closest = findClosestPatchInSameLine(mcParts[0], mcParts[1], mcParts[2]);
                MessageUtils.sendDebugConsole("No exact map match; closest same-line patch from MCVersion parts is: " + closest);
                return closest;
            }
        }

        int[] parsed = parseVersionParts(normalized);
        if (parsed[0] < 0) {
            MessageUtils.sendDebugConsole("Could not parse normalized version string. Returning: " + Other);
            return Other;
        }

        ServerVersion closest = findClosestPatchInSameLine(parsed[0], parsed[1], parsed[2]);
        MessageUtils.sendDebugConsole("Fallback closest same-line patch from parsed version is: " + closest);
        return closest;
    }

    private static MCVersion resolveMCVersion(String versionString) {
        MCVersion resolved = MCVersion.getFromVersionName(versionString);
        if (resolved != MCVersion.UNKNOWN) {
            return resolved;
        }

        String candidate = versionString;
        while (candidate.contains(".")) {
            String previous = candidate;

            // First try removing ".<digits>" suffix
            candidate = candidate.replaceFirst("\\.\\d+$", "");

            // If no change, remove last segment generically to prevent infinite loop
            if (candidate.equals(previous)) {
                int lastDot = candidate.lastIndexOf('.');
                if (lastDot <= 0) {
                    break;
                }
                candidate = candidate.substring(0, lastDot);
            }

            resolved = MCVersion.getFromVersionName(candidate);
            if (resolved != MCVersion.UNKNOWN) {
                return resolved;
            }
        }

        return MCVersion.UNKNOWN;
    }

    private static ServerVersion findClosestPatchInSameLine(int first, int second, int patch) {
        ServerVersion best = null;

        for (ServerVersion serverVersion : values()) {
            if (serverVersion == Other) {
                continue;
            }

            if (serverVersion.firstVersionNumber != first || serverVersion.secondVersionNumber != second) {
                continue;
            }

            if (serverVersion.patchVersionNumber > patch) {
                continue;
            }

            if (best == null || serverVersion.patchVersionNumber > best.patchVersionNumber) {
                best = serverVersion;
            }
        }

        return best != null ? best : Other;
    }

    private static int[] parseVersionParts(String version) {
        if (version == null || version.equalsIgnoreCase("Other")) {
            return new int[]{-1, -1, -1};
        }

        String[] split = version.split("\\.");
        if (split.length < 2 || split.length > 3) {
            return new int[]{-1, -1, -1};
        }

        try {
            int first = Integer.parseInt(split[0]);
            int second = Integer.parseInt(split[1]);
            int patch = split.length == 3 ? Integer.parseInt(split[2]) : 0;
            return new int[]{first, second, patch};
        } catch (NumberFormatException ignored) {
            return new int[]{-1, -1, -1};
        }
    }

}
