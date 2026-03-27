package me.loving11ish.epichomes.versionsystems;

public enum ServerVersion {

    v1_8_R1("1.8", 1, 8, 0),
    v1_8_R2("1.8.3", 1, 8, 3),
    v1_8_R3("1.8.8", 1, 8, 8),

    v1_9_R1("1.9", 1, 9, 0),
    v1_9_R2("1.9.4", 1, 9, 4),

    v1_10_R1("1.10", 1, 10, 0),
    v1_11_R1("1.11", 1, 11, 0),
    v1_12_R1("1.12", 1, 12, 0),

    v1_13_R1("1.13", 1, 13, 0),
    v1_13_R2("1.13.2", 1, 13, 2),

    v1_14_R1("1.14", 1, 14, 0),
    v1_15_R1("1.15", 1, 15, 0),

    v1_16_R1("1.16", 1, 16, 0),
    v1_16_R2("1.16.2", 1, 16, 2),
    v1_16_R3("1.16.5", 1, 16, 5),

    v1_17_R1("1.17", 1, 17, 0),

    v1_18_R1("1.18", 1, 18, 0),
    v1_18_R2("1.18.2", 1, 18, 2),

    v1_19_R1("1.19", 1, 19, 0),
    v1_19_R2("1.19.3", 1, 19, 3),
    v1_19_R3("1.19.4", 1, 19, 4),

    v1_20_R1("1.20", 1, 20, 0),
    v1_20_R2("1.20.2", 1, 20, 2),
    v1_20_R3("1.20.3", 1, 20, 3),
    v1_20_R4("1.20.5", 1, 20, 5),
    v1_20_R5("1.20.6", 1, 20, 6),

    v1_21_R1("1.21", 1, 21, 0),
    v1_21_R2("1.21.1", 1, 21, 1),
    v1_21_R3("1.21.2", 1, 21, 2),
    v1_21_R4("1.21.3", 1, 21, 3),
    v1_21_R5("1.21.4", 1, 21, 4),
    v1_21_R6("1.21.5", 1, 21, 5),
    v1_21_R7("1.21.6", 1, 21, 6),
    v1_21_R8("1.21.7", 1, 21, 7),
    v1_21_R9("1.21.8", 1, 21, 8),
    v1_21_R10("1.21.9", 1, 21, 9),
    v1_21_R11("1.21.10", 1, 21, 10),
    v1_21_R12("1.21.11", 1, 21, 11),

    v26_1_R1("26.1", 26, 1, 0),
    v26_1_R2("26.1.1", 26, 1, 1),

    Other("Other", -1, -1, -1);

    private final String minecraftVersion;
    private final int firstVersionNumber;
    private final int secondVersionNumber;
    private final int patchVersionNumber;

    ServerVersion(String minecraftVersion, int firstVersionNumber, int secondVersionNumber, int patchVersionNumber) {
        this.minecraftVersion = minecraftVersion;
        this.firstVersionNumber = firstVersionNumber;
        this.secondVersionNumber = secondVersionNumber;
        this.patchVersionNumber = patchVersionNumber;
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
        if (version1.firstVersionNumber != version2.firstVersionNumber) {
            return Integer.compare(version1.firstVersionNumber, version2.firstVersionNumber);
        }

        if (version1.secondVersionNumber != version2.secondVersionNumber) {
            return Integer.compare(version1.secondVersionNumber, version2.secondVersionNumber);
        }

        return Integer.compare(version1.patchVersionNumber, version2.patchVersionNumber);
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
     * Backwards-compatible with your old system.
     * For old versions like 1.21.4, this returns 21.
     * For new versions like 26.1, this returns 26.
     * @return The major version number of the server, which is the first version number in the version scheme.
     */
    public int getServerMajorVersionNumber() {
        return firstVersionNumber;
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

    public static ServerVersion fromMinecraftVersion(String versionString) {
        if (versionString == null || versionString.trim().isEmpty()) {
            return Other;
        }

        String normalizedVersion = versionString.trim();

        for (ServerVersion serverVersion : values()) {
            if (serverVersion == Other) {
                continue;
            }

            if (serverVersion.getMinecraftVersion().equalsIgnoreCase(normalizedVersion)) {
                return serverVersion;
            }
        }

        return Other;
    }
}
//public enum ServerVersion {
//
//    v1_8_R1,
//    v1_8_R2,
//    v1_8_R3,
//    v1_9_R1,
//    v1_9_R2,
//    v1_10_R1,
//    v1_11_R1,
//    v1_12_R1,
//    v1_13_R1,
//    v1_13_R2,
//    v1_14_R1,
//    v1_15_R1,
//    v1_16_R1,
//    v1_16_R2,
//    v1_16_R3,
//    v1_17_R1,
//    v1_18_R1,
//    v1_18_R2,
//    v1_19_R1,
//    v1_19_R2,
//    v1_19_R3,
//    v1_20_R1,
//    v1_20_R2,
//    v1_20_R3,
//    v1_20_R4,
//    v1_20_R5,
//    v1_21_R1,
//    v1_21_R2,
//    v1_21_R3,
//    v1_21_R4,
//    v1_21_R5,
//    v1_21_R6,
//    v1_21_R7,
//    v1_21_R8,
//    v1_21_R9,
//    v1_21_R10,
//    v1_21_R11,
//    v1_21_R12,
//    Other;
//
//    public boolean serverVersionEqual(ServerVersion version) {
//        return this.equals(version);
//    }
//
//    public boolean serverVersionGreaterThanOrEqual(ServerVersion version) {
//        return this.ordinal() >= version.ordinal();
//    }
//
//    public boolean serverVersionGreaterThan(ServerVersion version1, ServerVersion version2) {
//        return version1.ordinal() > version2.ordinal();
//    }
//
//    public boolean serverVersionLessThan(ServerVersion version1, ServerVersion version2) {
//        return version1.ordinal() < version2.ordinal();
//    }
//
//    public String getServerVersionName() {
//        return this.name();
//    }
//
//    public int getOrdinalServerVersionNumber() {
//        return this.ordinal();
//    }
//
//    public int getServerMajorVersionNumber() {
//        return Integer.parseInt(this.name().split("_")[1]);
//    }
//}
