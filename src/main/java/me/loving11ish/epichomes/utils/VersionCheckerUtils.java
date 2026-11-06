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
            serverPackage = "Unknown";
        }
    }

    public void setVersion() {
        try {
            ServerVersion detectedVersion = EpicHomes.getPlugin().getServerVersion();

            if (detectedVersion == null || detectedVersion == ServerVersion.Other) {
                version = -1;
                firstVersionNumber = -1;
                secondVersionNumber = -1;
                patchVersionNumber = -1;
                versionCheckedSuccessfully = false;

                MessageUtils.sendConsole("&c-------------------------------------------");
                MessageUtils.sendConsole("&4Unable to identify a supported server version!");
                MessageUtils.sendConsole("&4Detected server package: &d" + getServerPackage());
                MessageUtils.sendConsole("&4Some features may break unexpectedly!");
                MessageUtils.sendConsole("&4-------------------------------------------");
                MessageUtils.sendDebugConsole("error", "ServerVersion unresolved: " + detectedVersion);

                return;
            }

            version = detectedVersion.getServerMajorVersionNumber();
            firstVersionNumber = detectedVersion.getFirstVersionNumber();
            secondVersionNumber = detectedVersion.getSecondVersionNumber();
            patchVersionNumber = detectedVersion.getPatchVersionNumber();

            versionCheckedSuccessfully = true;
        } catch (Exception e) {
            version = -1;
            firstVersionNumber = -1;
            secondVersionNumber = -1;
            patchVersionNumber = -1;
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
        ServerVersion detectedVersion = ServerVersion.Other;
        String bukkitVersion = "Unknown";

        try {
            bukkitVersion = Bukkit.getServer().getBukkitVersion();
            String normalizedVersion = bukkitVersion.split("-")[0].trim();
            normalizedVersion = normalizedVersion.replaceFirst("^((\\d+)(\\.\\d+){1,2}).*$", "$1");

            MessageUtils.sendDebugConsole("-----------------------------------------");
            MessageUtils.sendDebugConsole("Detected Bukkit version: " + bukkitVersion);
            MessageUtils.sendDebugConsole("Normalized version: " + normalizedVersion);

            detectedVersion = ServerVersion.fromMinecraftVersion(normalizedVersion);

            if (detectedVersion == ServerVersion.Other) {
                String packageName = Bukkit.getServer().getClass().getPackage().getName();
                String packageVersion = packageName.replace("org.bukkit.craftbukkit.", "");

                try {
                    detectedVersion = ServerVersion.valueOf(packageVersion);
                    MessageUtils.sendDebugConsole("Fell back to package version detection: " + packageVersion);
                } catch (IllegalArgumentException ignored) {
                    // Keep as Other
                }
            }
        } catch (Exception e) {
            MessageUtils.sendDebugConsole("Failed to detect server version from Bukkit version: " + bukkitVersion);
            MessageUtils.sendDebugConsole("Version detection error message: " + e.getMessage());
        }

        if (detectedVersion == ServerVersion.Other) {
            MessageUtils.sendDebugConsole("Unable to map Bukkit version/package to a known ServerVersion.");
            MessageUtils.sendDebugConsole("Detected Bukkit version: " + bukkitVersion);
            MessageUtils.sendDebugConsole("Please report this to the plugin developer for further assistance.");
        }

        EpicHomes.getPlugin().setServerVersion(detectedVersion);
        MessageUtils.sendDebugConsole("Set server version: " + detectedVersion);
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
