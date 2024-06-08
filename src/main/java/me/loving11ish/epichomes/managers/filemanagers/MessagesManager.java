package me.loving11ish.epichomes.managers.filemanagers;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class MessagesManager {

    private final FileConfiguration messages;

    // Strings
    // General
    private String prefix;
    private String noPermission;
    private String playerOnly;
    private String playerNotFound;
    private String userNotFound;
    private String homeNotFound;
    private String commandCooldownTimeRemaining;
    private String userMapSaveFailed;
    private String pluginReloadBroadcastStart;
    private String autoSaveStart;
    private String autoSaveComplete;
    private String autoSaveFailed;
    private String pluginReloadStart;
    private String pluginReloadComplete;
    private String moveEventCancelFailed;
    // Home data import
    private String homeDataImportSuccess;
    private String homeDataImportFailed;
    private String homeDataImportFailedAlreadyImported;
    private String homeDataImportFailedSystemDisabled;
    // GUI
    private String guiFirstPage;
    private String guiLastPage;
    // Update checker
    private String updateCheckerFailed;
    private String updateCheckerUpdateAvailableOne;
    private String updateCheckerUpdateAvailableTwo;
    private String updateCheckerUpdateAvailableThree;
    private String updateCheckerNoUpdateOne;
    private String updateCheckerNoUpdateTwo;
    private String updateCheckerNoUpdateThree;
    // Timed teleport
    private String timedBeginTP;
    private String timedCompleteTP;
    private String timedCancelTP;
    private String timedTeleportCancelledPluginReload;
    // Non-timed teleport
    private String nonTimedCompleteTP;
    // Home set
    private String homeSetSuccess;
    private String homeSetFailAlreadyExists;
    private String homeSetFailedMaxHomes;
    private String homeSetFailedTieredMaxHomes;
    private String homeSetFailInvalidName;
    private String homeSetFailedIllegalName;
    // Home delete
    private String homeDeleteSuccess;
    private String homeAdminDeleteSuccess;
    private String homeDeleteFail;
    private String homeAdminDeleteFail;

    // Lists
    private List<String> playerHomeList;
    private List<String> adminHomeList;
    private List<String> homeCommandList;
    private List<String> setHomeCommandList;
    private List<String> deleteHomeCommandList;
    private List<String> homeAdminCommandList;

    public MessagesManager(FileConfiguration messages) {
        this.messages = messages;
    }

    public void loadMessagesValues() {
        // Strings
        // General
        prefix = messages.getString("global-prefix", "&f[&6Epic&bHomes&f]&r");
        noPermission = messages.getString("no-permission", "&cSorry, you don't have permission to do that.");
        playerOnly = messages.getString("incorrect-command-usage", "&cSorry, that is a player only command.");
        playerNotFound = messages.getString("homeadmin-unable-to-find-player", "&cUnable to find an offline player by the name of &e%TARGET%&c!");
        userNotFound = messages.getString("homeadmin-unable-to-find-user", "&cUnable to find a user for &e%TARGET%&c!");
        homeNotFound = messages.getString("home-name-does-not-exist", "&cSorry, you do not appear to have a home called &e%HOME%&c!");
        commandCooldownTimeRemaining = messages.getString("command-cool-down-time-left", "&cSorry, you still have &e%TIMELEFT% &cseconds until you can use that command again.");
        userMapSaveFailed = messages.getString("usermap-file-save-failure", "&cInvalid home world name entry found for player &e%PLAYER%&c! &aSkipping current entry!");
        pluginReloadBroadcastStart = messages.getString("plugin-reload-broadcast-start", "&aThe plugin is being reloaded, please do not use any Homes commands until completed!");
        autoSaveStart = messages.getString("auto-save-started", "&aAuto save task has started.");
        autoSaveComplete = messages.getString("auto-save-complete", "&aSaved usermap data to disk!");
        autoSaveFailed = messages.getString("auto-save-failed", "&4Failed to save usermap.yml to disk!");
        pluginReloadStart = messages.getString("plugin-reload-start", "&aStarting reload process...");
        pluginReloadComplete = messages.getString("plugin-reload-complete", "&aPlugin reload process complete!");
        moveEventCancelFailed = messages.getString("move-event-cancel-failed", "&4Unable to cancel teleport on move!\\n&4See below for reason!");
        // Home data import
        homeDataImportSuccess = messages.getString("home-data-import-successful", "&aAll available home and player data successfully imported from &e%PLUGIN%&a!");
        homeDataImportFailed = messages.getString("home-data-import-failed", "&4Failed to import home or player data from &e%PLUGIN%&a!\\n&4Please check console for errors!");
        homeDataImportFailedAlreadyImported = messages.getString("home-data-import-failed-already-run", "&cData has already been successfully imported!");
        homeDataImportFailedSystemDisabled = messages.getString("home-data-import-disabled", "&cThe importer system is disabled in the config.yml!\\n&cPlease contact a system admin if you believe this is in error!");
        // GUI
        guiFirstPage = messages.getString("GUI-first-page", "&7You are on the first page.");
        guiLastPage = messages.getString("GUI-last-page", "&7You are on the last page.");
        // Update checker
        updateCheckerFailed = messages.getString("update-check-failure", "&4Unable to check for updates! - &c");
        updateCheckerUpdateAvailableOne = messages.getString("update-available.1", "&4*-------------------------------------------*");
        updateCheckerUpdateAvailableTwo = messages.getString("update-available.2", "&cA new version is available!");
        updateCheckerUpdateAvailableThree = messages.getString("update-available.3", "&4*-------------------------------------------*");
        updateCheckerNoUpdateOne = messages.getString("no-update-available.1", "&a*-------------------------------------------*");
        updateCheckerNoUpdateTwo = messages.getString("no-update-available.2", "&aPlugin is up to date!");
        updateCheckerNoUpdateThree = messages.getString("no-update-available.3", "&a*-------------------------------------------*");
        // Timed teleport
        timedBeginTP = messages.getString("timed-teleporting-begin-tp", "&aTeleporting to home &e%HOME%&a...\\n&aPlease stay still!");
        timedCompleteTP = messages.getString("timed-teleporting-complete", "&aSuccessfully arrived at &e%HOME%&a.");
        timedCancelTP = messages.getString("timed-teleport-failed-player-moved", "&cYou moved before you were teleported to your home! The teleport was cancelled.");
        timedTeleportCancelledPluginReload = messages.getString("timed-teleport-failed-plugin-reloaded", "&cA server operator reloaded the plugin so your teleport was canceled.\\n&cPlease try again.");
        // Non-timed teleport
        nonTimedCompleteTP = messages.getString("non-timed-teleporting-complete", "&aSuccessfully arrived at &e%HOME%&a.");
        // Home set
        homeSetSuccess = messages.getString("home-set-successfully", "&aSuccessfully set home &e%HOME% &aat your current location.");
        homeSetFailAlreadyExists = messages.getString("home-set-failed-home-exists", "&e%HOME% &calready exists!");
        homeSetFailedMaxHomes = messages.getString("home-set-failed-max-homes-reached", "&cSorry, you have reached the max allowed homes.");
        homeSetFailedTieredMaxHomes = messages.getString("home-set-failed-max-tiered-reached", "&cSorry, you have reached the total homes your tier can set!\\n&cYou can set a total of &e%LIMIT% &chomes.");
        homeSetFailInvalidName = messages.getString("home-set-failed-invalid-name", "&cSorry, your home cannot contain colours or the following characters: &d.");
        homeSetFailedIllegalName = messages.getString("home-set-failed-name-not-allowed", "&cSorry, that home name is not allowed. Please choose another!");
        // Home delete
        homeDeleteSuccess = messages.getString("home-delete-successful", "&aSuccessfully deleted &e%HOME%&a.");
        homeAdminDeleteSuccess = messages.getString("homeadmin-delete-successful", "&aSuccessfully deleted &e%TARGET%'s %HOME%&a.");
        homeDeleteFail = messages.getString("home-delete-failed", "&cFailed to delete &e%HOME%&c!\\n&cCheck console for errors!");
        homeAdminDeleteFail = messages.getString("homeadmin-delete-failed", "&cFailed to delete &e%TARGET%'s %HOME%&c!\\n&cCheck console for errors!");

        // Lists
        playerHomeList = messages.getStringList("list");
        adminHomeList = messages.getStringList("homeadmin-list");
        homeCommandList = messages.getStringList("incorrect-home-command-usage");
        setHomeCommandList = messages.getStringList("incorrect-sethome-command-usage");
        deleteHomeCommandList = messages.getStringList("incorrect-delhome-command-usage");
        homeAdminCommandList = messages.getStringList("incorrect-homeadmin-command-usage");
    }

    // String getters
    // General
    public String getPrefix() {
        return prefix;
    }

    public String getNoPermission() {
        return noPermission;
    }

    public String getPlayerOnly() {
        return playerOnly;
    }

    public String getPlayerNotFound() {
        return playerNotFound;
    }

    public String getUserNotFound() {
        return userNotFound;
    }

    public String getHomeNotFound() {
        return homeNotFound;
    }

    public String getCommandCooldownTimeRemaining() {
        return commandCooldownTimeRemaining;
    }

    public String getUserMapSaveFailed() {
        return userMapSaveFailed;
    }

    public String getPluginReloadBroadcastStart() {
        return pluginReloadBroadcastStart;
    }

    public String getAutoSaveStart() {
        return autoSaveStart;
    }

    public String getAutoSaveComplete() {
        return autoSaveComplete;
    }

    public String getAutoSaveFailed() {
        return autoSaveFailed;
    }

    public String getPluginReloadStart() {
        return pluginReloadStart;
    }

    public String getPluginReloadComplete() {
        return pluginReloadComplete;
    }

    public String getMoveEventCancelFailed() {
        return moveEventCancelFailed;
    }

    public String getHomeDataImportSuccess() {
        return homeDataImportSuccess;
    }

    public String getHomeDataImportFailed() {
        return homeDataImportFailed;
    }

    public String getHomeDataImportFailedAlreadyImported() {
        return homeDataImportFailedAlreadyImported;
    }

    public String getHomeDataImportFailedSystemDisabled() {
        return homeDataImportFailedSystemDisabled;
    }

    public String getGuiFirstPage() {
        return guiFirstPage;
    }

    public String getGuiLastPage() {
        return guiLastPage;
    }

    public String getUpdateCheckerFailed() {
        return updateCheckerFailed;
    }

    public String getUpdateCheckerUpdateAvailableOne() {
        return updateCheckerUpdateAvailableOne;
    }

    public String getUpdateCheckerUpdateAvailableTwo() {
        return updateCheckerUpdateAvailableTwo;
    }

    public String getUpdateCheckerUpdateAvailableThree() {
        return updateCheckerUpdateAvailableThree;
    }

    public String getUpdateCheckerNoUpdateOne() {
        return updateCheckerNoUpdateOne;
    }

    public String getUpdateCheckerNoUpdateTwo() {
        return updateCheckerNoUpdateTwo;
    }

    public String getUpdateCheckerNoUpdateThree() {
        return updateCheckerNoUpdateThree;
    }

    public String getTimedBeginTP() {
        return timedBeginTP;
    }

    public String getTimedCompleteTP() {
        return timedCompleteTP;
    }

    public String getTimedCancelTP() {
        return timedCancelTP;
    }

    public String getTimedTeleportCancelledPluginReload() {
        return timedTeleportCancelledPluginReload;
    }

    public String getNonTimedCompleteTP() {
        return nonTimedCompleteTP;
    }

    public String getHomeSetSuccess() {
        return homeSetSuccess;
    }

    public String getHomeSetFailAlreadyExists() {
        return homeSetFailAlreadyExists;
    }

    public String getHomeSetFailedMaxHomes() {
        return homeSetFailedMaxHomes;
    }

    public String getHomeSetFailedTieredMaxHomes() {
        return homeSetFailedTieredMaxHomes;
    }

    public String getHomeSetFailInvalidName() {
        return homeSetFailInvalidName;
    }

    public String getHomeSetFailedIllegalName() {
        return homeSetFailedIllegalName;
    }

    public String getHomeDeleteSuccess() {
        return homeDeleteSuccess;
    }

    public String getHomeAdminDeleteSuccess() {
        return homeAdminDeleteSuccess;
    }

    public String getHomeDeleteFail() {
        return homeDeleteFail;
    }

    public String getHomeAdminDeleteFail() {
        return homeAdminDeleteFail;
    }

    // List getters
    public List<String> getPlayerHomeList() {
        return playerHomeList;
    }

    public List<String> getAdminHomeList() {
        return adminHomeList;
    }

    public List<String> getHomeCommandList() {
        return homeCommandList;
    }

    public List<String> getSetHomeCommandList() {
        return setHomeCommandList;
    }

    public List<String> getDeleteHomeCommandList() {
        return deleteHomeCommandList;
    }

    public List<String> getHomeAdminCommandList() {
        return homeAdminCommandList;
    }
}
