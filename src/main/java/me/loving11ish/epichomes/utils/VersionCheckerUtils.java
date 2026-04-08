package me.loving11ish.epichomes.utils;

import me.loving11ish.epichomes.EpicHomes;
import me.loving11ish.epichomes.versionsystems.ServerVersion;
import org.bukkit.Bukkit;

public class VersionCheckerUtils {

    private String serverPackage;
    private int version;
    private int firstVersionNumber;
    private int secondVersionNumber;
    private int patchVersionNumber;
    private boolean versionCheckedSuccessfully = false;

    public VersionCheckerUtils() {
        try {
            serverPackage = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        } catch (ArrayIndexOutOfBoundsException e) {
            serverPackage = null;
        }
    }

    public void setVersion() {
        try {
            ServerVersion detectedVersion = EpicHomes.getPlugin().getServerVersion();

            version = detectedVersion.getServerMajorVersionNumber();
            firstVersionNumber = detectedVersion.getFirstVersionNumber();
            secondVersionNumber = detectedVersion.getSecondVersionNumber();
            patchVersionNumber = detectedVersion.getPatchVersionNumber();

            versionCheckedSuccessfully = detectedVersion != ServerVersion.Other;
        } catch (Exception e) {
            versionCheckedSuccessfully = false;

            MessageUtils.sendConsole("&c-------------------------------------------");
            MessageUtils.sendConsole("&4Unable to process server version!");
            MessageUtils.sendConsole("&4Some features may break unexpectedly!");
            MessageUtils.sendConsole("&4Report any issues to the developer!");
            MessageUtils.sendDebugConsole("error", "&4-------------------------------------------");
            MessageUtils.sendDebugConsole("error", "Cause: " + e.getCause());
            MessageUtils.sendDebugConsole("error", "Message: " + e.getMessage());
            MessageUtils.sendDebugConsole("error", "&4-------------------------------------------");
            MessageUtils.sendConsole("&c-------------------------------------------");
        }
    }

    public void getServerVersion() {
        try {
            String bukkitVersion = Bukkit.getServer().getBukkitVersion();
            String normalizedVersion = bukkitVersion.split("-")[0].trim();

            MessageUtils.sendDebugConsole("-----------------------------------------");
            MessageUtils.sendDebugConsole("Detected Bukkit version: " + bukkitVersion);
            MessageUtils.sendDebugConsole("Normalized version: " + normalizedVersion);

            ServerVersion detectedVersion = ServerVersion.fromMinecraftVersion(normalizedVersion);


             // Fallback for older CraftBukkit-style package versions such as v1_16_R3
             // if Bukkit version string matching fails for some reason.
            if (detectedVersion == ServerVersion.Other) {
                try {
                    String packageName = Bukkit.getServer().getClass().getPackage().getName();
                    String packageVersion = packageName.replace("org.bukkit.craftbukkit.", "");
                    detectedVersion = ServerVersion.valueOf(packageVersion);

                    MessageUtils.sendDebugConsole("Fell back to package version detection: " + packageVersion);
                } catch (Exception ignored) {
                    // Keep as Other
                }
            }

            EpicHomes.getPlugin().setServerVersion(detectedVersion);

        } catch (Exception e) {
            EpicHomes.getPlugin().setServerVersion(ServerVersion.Other);
            MessageUtils.sendDebugConsole("Failed to detect server version, defaulting to: " + EpicHomes.getPlugin().getServerVersion());
            MessageUtils.sendDebugConsole("Version detection error message: " + e.getMessage());
        }

        MessageUtils.sendDebugConsole("Set server version: " + EpicHomes.getPlugin().getServerVersion());
    }

    public String getServerPackage() {
        return serverPackage != null ? serverPackage : "Unknown";
    }

    /**
     * Backwards-compatible with your old code.
     * Old scheme examples:
     * `1.21.4 - returns 21`
     * New scheme examples:
     * `26.1 - returns 26`
     * @return The major version number, which is the first number in the Minecraft version (e.g. 1.21.4 returns 1, 26.1 returns 26)
     */
    public int getVersion() {
        return version;
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

    public boolean isVersionCheckedSuccessfully() {
        return versionCheckedSuccessfully;
    }

    public boolean isLegacyVersionScheme() {
        return firstVersionNumber == 1;
    }

    public boolean isNewVersionScheme() {
        return firstVersionNumber >= 25;
    }

    /**
     * Useful helper for checks like:
     * - all 26.x versions should count as newer than 1.21
     * @return True if the server is running Minecraft 1.21 or newer, false otherwise
     */
    public boolean isAtLeastMinecraft1_21() {
        ServerVersion serverVersion = EpicHomes.getPlugin().getServerVersion();

        if (serverVersion == null || serverVersion == ServerVersion.Other) {
            return false;
        }

        if (serverVersion.isNewVersionScheme()) {
            return true;
        }

        return serverVersion.serverVersionGreaterThanOrEqual(ServerVersion.v1_21_R1);
    }

    /**
     * Generic helper if you want to compare against a specific enum version.
     * @param requiredVersion The ServerVersion enum constant you want to compare against (e.g. ServerVersion.v1_21_R1 or ServerVersion.v1_20_R4)
     * @return True if the server version is greater than or equal to the required version, false otherwise
     */
    public boolean isAtLeast(ServerVersion requiredVersion) {
        ServerVersion serverVersion = EpicHomes.getPlugin().getServerVersion();

        if (serverVersion == null || serverVersion == ServerVersion.Other || requiredVersion == null) {
            return false;
        }

        return serverVersion.serverVersionGreaterThanOrEqual(requiredVersion);
    }
}
//import me.loving11ish.epichomes.EpicHomes;
//import me.loving11ish.epichomes.versionsystems.ServerVersion;
//import org.bukkit.Bukkit;
//
//import java.util.regex.PatternSyntaxException;
//
//public class VersionCheckerUtils {
//
//    private String serverPackage;
//    private int version;
//    private boolean versionCheckedSuccessfully = false;
//
//    public VersionCheckerUtils() {
//        try {
//            serverPackage = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
//        } catch (ArrayIndexOutOfBoundsException e) {
//            serverPackage = null;
//        }
//    }
//
//    public void setVersion() {
//        try {
//            version = EpicHomes.getPlugin().getServerVersion().getServerMajorVersionNumber();
//            versionCheckedSuccessfully = true;
//        } catch (NumberFormatException | PatternSyntaxException e) {
//            versionCheckedSuccessfully = false;
//            MessageUtils.sendConsole("&c-------------------------------------------");
//            MessageUtils.sendConsole("&4Unable to process server version!");
//            MessageUtils.sendConsole("&4Some features may break unexpectedly!");
//            MessageUtils.sendConsole("&4Report any issues to the developer!");
//            MessageUtils.sendDebugConsole("error", "&4-------------------------------------------");
//            MessageUtils.sendDebugConsole("error", "Cause: " + e.getCause());
//            MessageUtils.sendDebugConsole("error", "Message: " +e.getMessage());
//            MessageUtils.sendDebugConsole("error", "&4-------------------------------------------");
//            MessageUtils.sendConsole("&c-------------------------------------------");
//        }
//    }
//
//    public void getServerVersion() {
//        try {
//            String bukkitVersion = Bukkit.getServer().getBukkitVersion();
//            String normalizedVersion = bukkitVersion.split("-")[0]; // Extract base version
//
//            MessageUtils.sendDebugConsole("-----------------------------------------");
//            MessageUtils.sendDebugConsole("Detected Bukkit version: " + bukkitVersion);
//            MessageUtils.sendDebugConsole("Normalized version: " + normalizedVersion);
//
//            switch (normalizedVersion) {
//                case "1.20.5":
//                    EpicHomes.getPlugin().setServerVersion(ServerVersion.v1_20_R4);
//                    break;
//                case "1.20.6":
//                    EpicHomes.getPlugin().setServerVersion(ServerVersion.v1_20_R5);
//                    break;
//                case "1.21":
//                    EpicHomes.getPlugin().setServerVersion(ServerVersion.v1_21_R1);
//                    break;
//                case "1.21.1":
//                    EpicHomes.getPlugin().setServerVersion(ServerVersion.v1_21_R2);
//                    break;
//                case "1.21.2":
//                    EpicHomes.getPlugin().setServerVersion(ServerVersion.v1_21_R3);
//                    break;
//                case "1.21.3":
//                    EpicHomes.getPlugin().setServerVersion(ServerVersion.v1_21_R4);
//                    break;
//                case "1.21.4":
//                    EpicHomes.getPlugin().setServerVersion(ServerVersion.v1_21_R5);
//                    break;
//                case "1.21.5":
//                    EpicHomes.getPlugin().setServerVersion(ServerVersion.v1_21_R6);
//                    break;
//                case "1.21.6":
//                    EpicHomes.getPlugin().setServerVersion(ServerVersion.v1_21_R7);
//                    break;
//                case "1.21.7":
//                    EpicHomes.getPlugin().setServerVersion(ServerVersion.v1_21_R8);
//                    break;
//                case "1.21.8":
//                    EpicHomes.getPlugin().setServerVersion(ServerVersion.v1_21_R9);
//                    break;
//                case "1.21.9":
//                    EpicHomes.getPlugin().setServerVersion(ServerVersion.v1_21_R10);
//                    break;
//                case "1.21.10":
//                    EpicHomes.getPlugin().setServerVersion(ServerVersion.v1_21_R11);
//                    break;
//                case "1.21.11":
//                    EpicHomes.getPlugin().setServerVersion(ServerVersion.v1_21_R12);
//                    break;
//
//
//                default:
//                    String packageName = Bukkit.getServer().getClass().getPackage().getName();
//                    EpicHomes.getPlugin().setServerVersion(ServerVersion.valueOf(packageName.replace("org.bukkit.craftbukkit.", "")));
//                    break;
//            }
//
//        } catch (Exception e) {
//            EpicHomes.getPlugin().setServerVersion(ServerVersion.Other);
//            MessageUtils.sendDebugConsole("Failed to detect server version, defaulting to: " + EpicHomes.getPlugin().getServerVersion());
//        }
//        MessageUtils.sendDebugConsole("Set server version: " + EpicHomes.getPlugin().getServerVersion());
//    }
//
//    public String getServerPackage() {
//        return serverPackage != null ? serverPackage : "Unknown";
//    }
//
//    public int getVersion() {
//        return version;
//    }
//
//    public boolean isVersionCheckedSuccessfully() {
//        return versionCheckedSuccessfully;
//    }
//}
