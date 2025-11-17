package me.loving11ish.epichomes.utils;

import me.loving11ish.epichomes.EpicHomes;
import me.loving11ish.epichomes.versionsystems.ServerVersion;
import org.bukkit.Bukkit;

import java.util.regex.PatternSyntaxException;

public class VersionCheckerUtils {

    private String serverPackage;
    private int version;
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
            version = EpicHomes.getPlugin().getServerVersion().getServerMajorVersionNumber();
            versionCheckedSuccessfully = true;
        } catch (NumberFormatException | PatternSyntaxException e) {
            versionCheckedSuccessfully = false;
            MessageUtils.sendConsole("&c-------------------------------------------");
            MessageUtils.sendConsole("&4Unable to process server version!");
            MessageUtils.sendConsole("&4Some features may break unexpectedly!");
            MessageUtils.sendConsole("&4Report any issues to the developer!");
            MessageUtils.sendDebugConsole("error", "&4-------------------------------------------");
            MessageUtils.sendDebugConsole("error", "Cause: " + e.getCause());
            MessageUtils.sendDebugConsole("error", "Message: " +e.getMessage());
            MessageUtils.sendDebugConsole("error", "&4-------------------------------------------");
            MessageUtils.sendConsole("&c-------------------------------------------");
        }
    }

    public void getServerVersion() {
        try {
            String bukkitVersion = Bukkit.getServer().getBukkitVersion();
            String normalizedVersion = bukkitVersion.split("-")[0]; // Extract base version

            MessageUtils.sendDebugConsole("-----------------------------------------");
            MessageUtils.sendDebugConsole("Detected Bukkit version: " + bukkitVersion);
            MessageUtils.sendDebugConsole("Normalized version: " + normalizedVersion);

            switch (normalizedVersion) {
                case "1.20.5":
                    EpicHomes.getPlugin().setServerVersion(ServerVersion.v1_20_R4);
                    break;
                case "1.20.6":
                    EpicHomes.getPlugin().setServerVersion(ServerVersion.v1_20_R5);
                    break;
                case "1.21":
                    EpicHomes.getPlugin().setServerVersion(ServerVersion.v1_21_R1);
                    break;
                case "1.21.1":
                    EpicHomes.getPlugin().setServerVersion(ServerVersion.v1_21_R2);
                    break;
                case "1.21.2":
                    EpicHomes.getPlugin().setServerVersion(ServerVersion.v1_21_R3);
                    break;
                case "1.21.3":
                    EpicHomes.getPlugin().setServerVersion(ServerVersion.v1_21_R4);
                    break;
                case "1.21.4":
                    EpicHomes.getPlugin().setServerVersion(ServerVersion.v1_21_R5);
                    break;
                case "1.21.5":
                    EpicHomes.getPlugin().setServerVersion(ServerVersion.v1_21_R6);
                    break;
                case "1.21.6":
                    EpicHomes.getPlugin().setServerVersion(ServerVersion.v1_21_R7);
                    break;
                case "1.21.7":
                    EpicHomes.getPlugin().setServerVersion(ServerVersion.v1_21_R8);
                    break;
                case "1.21.8":
                    EpicHomes.getPlugin().setServerVersion(ServerVersion.v1_21_R9);
                    break;
                case "1.21.9":
                    EpicHomes.getPlugin().setServerVersion(ServerVersion.v1_21_R10);
                    break;
                case "1.21.10":
                    EpicHomes.getPlugin().setServerVersion(ServerVersion.v1_21_R11);
                    break;


                default:
                    String packageName = Bukkit.getServer().getClass().getPackage().getName();
                    EpicHomes.getPlugin().setServerVersion(ServerVersion.valueOf(packageName.replace("org.bukkit.craftbukkit.", "")));
                    break;
            }

        } catch (Exception e) {
            EpicHomes.getPlugin().setServerVersion(ServerVersion.Other);
            MessageUtils.sendDebugConsole("Failed to detect server version, defaulting to: " + EpicHomes.getPlugin().getServerVersion());
        }
        MessageUtils.sendDebugConsole("Set server version: " + EpicHomes.getPlugin().getServerVersion());
    }

    public String getServerPackage() {
        return serverPackage != null ? serverPackage : "Unknown";
    }

    public int getVersion() {
        return version;
    }

    public boolean isVersionCheckedSuccessfully() {
        return versionCheckedSuccessfully;
    }
}
