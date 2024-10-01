package me.loving11ish.epichomes;

import com.tcoded.folialib.FoliaLib;
import com.tcoded.folialib.wrapper.task.WrappedTask;
import io.papermc.lib.PaperLib;
import me.loving11ish.epichomes.commands.*;
import me.loving11ish.epichomes.externalhooks.PlaceholderAPI;
import me.loving11ish.epichomes.externalhooks.PlaceholderAPIHook;
import me.loving11ish.epichomes.files.MessagesFileManager;
import me.loving11ish.epichomes.files.UsermapFileManager;
import me.loving11ish.epichomes.listeners.*;
import me.loving11ish.epichomes.managers.TeleportationManager;
import me.loving11ish.epichomes.managers.filemanagers.ConfigManager;
import me.loving11ish.epichomes.managers.filemanagers.MessagesManager;
import me.loving11ish.epichomes.menusystem.PlayerMenuUtility;
import me.loving11ish.epichomes.updatesystem.JoinEvent;
import me.loving11ish.epichomes.updatesystem.UpdateChecker;
import me.loving11ish.epichomes.utils.AutoSaveTaskUtils;
import me.loving11ish.epichomes.utils.MessageUtils;
import me.loving11ish.epichomes.utils.UsermapStorageUtil;
import me.loving11ish.epichomes.utils.VersionCheckerUtils;
import me.loving11ish.epichomes.versionsystems.ServerVersion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public final class EpicHomes extends JavaPlugin {

    private final PluginDescriptionFile pluginInfo = getDescription();
    private final String pluginVersion = pluginInfo.getVersion();

    private static EpicHomes plugin;
    private static FoliaLib foliaLib;
    private static ServerVersion serverVersion;
    private static VersionCheckerUtils versionCheckerUtils;
    private static PlaceholderAPIHook placeholderAPIHook = null;

    private boolean isPluginEnabled = true;
    private boolean isOnlineMode = true;
    private boolean isGUIEnabled = true;
    private boolean isUpdateAvailable = false;

    private MessagesFileManager messagesFileManager;
    private UsermapFileManager usermapFileManager;
    private UsermapStorageUtil usermapStorageUtil;

    private ConfigManager configManager;
    private MessagesManager messagesManager;

    private TeleportationManager teleportationManager;

    private final List<String> pluginCommands = new ArrayList<>();
    private static final HashMap<Player, PlayerMenuUtility> playerMenuUtilityMap = new HashMap<>();

    @Override
    public void onLoad() {
        // Plugin startup logic
        plugin = this;
        foliaLib = new FoliaLib(this);

        // Load the plugin configs
        getConfig().options().copyDefaults();
        saveDefaultConfig();

        // Load ConfigManager
        setConfigManager(new ConfigManager(getConfig()));
        getConfigManager().loadConfigValues();

        // Load messages.yml
        setMessagesFileManager(new MessagesFileManager(this));

        // Load MessagesManager
        setMessagesManager(new MessagesManager(getMessagesFileManager().getMessagesConfig()));
        getMessagesManager().loadMessagesValues();
        MessageUtils.setDebug(getConfigManager().isDebugMode());

        // Load usermap.yml
        setUsermapFileManager(new UsermapFileManager(this));

        // Check server version and set it
        setVersion();
        versionCheckerUtils = new VersionCheckerUtils();
        versionCheckerUtils.setVersion();

        // Server version compatibility check
        if (versionCheckerUtils.getVersion() < 16 || versionCheckerUtils.getVersion() > 21
                || !versionCheckerUtils.isVersionCheckedSuccessfully()
                && !serverVersion.serverVersionEqual(ServerVersion.Other)) {
            MessageUtils.sendConsole("&4-------------------------------------------");
            MessageUtils.sendConsole("&4Your server version is: &d" + Bukkit.getVersion());
            MessageUtils.sendConsole("&4This plugin is only supported on the Minecraft versions listed below:");
            MessageUtils.sendConsole("&41.16.x");
            MessageUtils.sendConsole("&41.17.x");
            MessageUtils.sendConsole("&41.18.x");
            MessageUtils.sendConsole("&41.19.x");
            MessageUtils.sendConsole("&41.20.x");
            MessageUtils.sendConsole("&41.21.x");
            MessageUtils.sendConsole("&4Is now disabling!");
            MessageUtils.sendConsole("&4-------------------------------------------");
            setPluginEnabled(false);
            return;
        } else {
            MessageUtils.sendConsole("&a-------------------------------------------");
            MessageUtils.sendConsole("&aA supported Minecraft version has been detected");
            MessageUtils.sendConsole("&aYour server version is: &d" + Bukkit.getVersion());
            MessageUtils.sendConsole("&6Continuing plugin startup");
            MessageUtils.sendConsole("&a-------------------------------------------");
        }

        if (getFoliaLib().isUnsupported()) {
            MessageUtils.sendConsole("&4-------------------------------------------");
            MessageUtils.sendConsole("&4Your server appears to running a version other than Spigot based!");
            MessageUtils.sendConsole("&4This plugin uses features that your server most likely doesn't have!");
            MessageUtils.sendConsole("&4Is now disabling!");
            MessageUtils.sendConsole("&4-------------------------------------------");
            setPluginEnabled(false);
            return;
        }

        if (getFoliaLib().isSpigot()) {
            PaperLib.suggestPaper(this);
        }

        // Set GUI enabled status
        setGUIEnabled(getConfigManager().isUseGUI());

        // Signal end of onLoad method
        MessageUtils.sendDebugConsole("End of onLoad method");
    }

    @Override
    public void onEnable() {
        // Plugin startup logic

        // Check plugin was not disabled during onLoad
        if (!isPluginEnabled()) {
            MessageUtils.sendConsole("&4-------------------------------------------");
            MessageUtils.sendConsole("&4Plugin has been disabled during onLoad!");
            MessageUtils.sendConsole("&4See above for details!");
            MessageUtils.sendConsole("&4Disabling plugin!");
            MessageUtils.sendConsole("&4-------------------------------------------");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        // Load internal plugin objects
        setUsermapStorageUtil(new UsermapStorageUtil());

        // Load usermap contents to memory
        if (getUsermapFileManager() != null) {
            if (getUsermapFileManager().getUsermapConfig().contains("users.data")) {
                try {
                    getUsermapStorageUtil().loadUsermap();
                    MessageUtils.sendConsole("-------------------------------------------");
                    MessageUtils.sendConsole("&3Successfully loaded usermap to memory.");
                } catch (IOException e) {
                    MessageUtils.sendConsole("-------------------------------------------");
                    MessageUtils.sendConsole("error", "&4Failed to load data from usermap.yml!");
                    MessageUtils.sendConsole("error", "&4See below for errors!");
                    MessageUtils.sendConsole("error", "&4Disabling Plugin!");
                    e.printStackTrace();
                    MessageUtils.sendConsole("-------------------------------------------");
                    Bukkit.getPluginManager().disablePlugin(this);
                    return;
                }
            }
        }

        // Load teleportation manager
        setTeleportationManager(new TeleportationManager());

        // Register commands
        HomeCommand homeCommand = new HomeCommand();
        HomeAdminCommand homeAdminCommand = new HomeAdminCommand();
        getCommand("home").setExecutor(homeCommand);
        getCommand("home").setTabCompleter(homeCommand);
        getCommand("homeadmin").setExecutor(homeAdminCommand);
        getCommand("homeadmin").setTabCompleter(homeAdminCommand);
        getCommand("sethome").setExecutor(new SetHomeCommand());
        getCommand("delhome").setExecutor(new DeleteHomeCommand());
        getCommand("importhomes").setExecutor(new HomeImportCommand());

        // Register events
        pluginCommands.add("/home");
        pluginCommands.add("/sethome");
        pluginCommands.add("/delhome");
        pluginCommands.add("/h");
        pluginCommands.add("/eh");
        pluginCommands.add("/delhome");
        pluginCommands.add("/homes");
        pluginCommands.add("/epichomes");
        pluginCommands.add("/homeadmin");
        pluginCommands.add("/ha");
        getServer().getPluginManager().registerEvents(new MenuEvent(), this);
        getServer().getPluginManager().registerEvents(new JoinEvent(), this);
        getServer().getPluginManager().registerEvents(new PlayerMovementEvent(), this);
        getServer().getPluginManager().registerEvents(new PlayerConnectionEvent(), this);
        getServer().getPluginManager().registerEvents(new PlayerPreConnectionEvent(), this);
        getServer().getPluginManager().registerEvents(new PlayerDisconnectionEvent(), this);
        getServer().getPluginManager().registerEvents(new PlayerCommandSendEvent(pluginCommands), this);

        // Update banned names lists
        HomeCommand.updateBannedNamesList();
        SetHomeCommand.updateBannedNamesList();

        // Register PlaceHolderAPI hooks
        if (Bukkit.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI") || PlaceholderAPI.isPlaceholderAPIEnabled()) {
            placeholderAPIHook = new PlaceholderAPIHook();
            placeholderAPIHook.register();
            MessageUtils.sendConsole("-------------------------------------------");
            MessageUtils.sendConsole("&3PlaceholderAPI found!");
            MessageUtils.sendConsole("&3External placeholders enabled!");
        } else {
            MessageUtils.sendConsole("-------------------------------------------");
            MessageUtils.sendConsole("&cPlaceholderAPI not found!");
            MessageUtils.sendConsole("&cExternal placeholders disabled!");
        }
        MessageUtils.sendConsole("&6Continuing plugin startup");
        MessageUtils.sendConsole("-------------------------------------------");

        // Plugin startup message
        MessageUtils.sendConsole("-------------------------------------------");
        MessageUtils.sendConsole("&3Plugin by: &b&lLoving11ish");
        MessageUtils.sendConsole("&3has been loaded successfully");
        MessageUtils.sendConsole("&3Plugin Version: &d&l" + pluginVersion);
        MessageUtils.sendDebugConsole( "&aDeveloper debug mode enabled!");
        MessageUtils.sendDebugConsole( "&aThis WILL fill the console");
        MessageUtils.sendDebugConsole( "&awith additional EpicHomes information!");
        MessageUtils.sendDebugConsole( "&aThis setting is not intended for ");
        MessageUtils.sendDebugConsole( "&acontinous use!");
        MessageUtils.sendConsole("-------------------------------------------");

        // Check for available updates
        new UpdateChecker(109590).getVersion(version -> {
            if (this.getDescription().getVersion().equalsIgnoreCase(version)) {
                MessageUtils.sendConsole(getMessagesManager().getUpdateCheckerNoUpdateOne());
                MessageUtils.sendConsole(getMessagesManager().getUpdateCheckerNoUpdateTwo());
                MessageUtils.sendConsole(getMessagesManager().getUpdateCheckerNoUpdateThree());
                setUpdateAvailable(false);
            } else {
                MessageUtils.sendConsole(getMessagesManager().getUpdateCheckerUpdateAvailableOne());
                MessageUtils.sendConsole(getMessagesManager().getUpdateCheckerUpdateAvailableTwo());
                MessageUtils.sendConsole(getMessagesManager().getUpdateCheckerUpdateAvailableThree());
                setUpdateAvailable(true);
            }
        });

        // Set plugin enabled to true
        setPluginEnabled(true);

        // Start auto save task
        getFoliaLib().getScheduler().runLaterAsync(() -> {
            AutoSaveTaskUtils.runAutoSaveTask();
            MessageUtils.sendConsole(getMessagesManager().getAutoSaveStart());
        }, 5L, TimeUnit.SECONDS);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        setPluginEnabled(false);

        // Unregister event listeners
        HandlerList.unregisterAll(this);

        // Unregister PlaceHolderAPI hooks
        if (placeholderAPIHook != null) {
            placeholderAPIHook.unregister();
            MessageUtils.sendDebugConsole("&aExternal placeholders unregistered!");
        }

        // Safely stop the background tasks if running
        MessageUtils.sendConsole("-------------------------------------------");
        MessageUtils.sendConsole("&3Plugin by: &b&lLoving11ish");
        try {
            if (!getTeleportationManager().getTeleportQueue().isEmpty()) {
                for (Map.Entry<UUID, WrappedTask> wrappedTaskEntry : getTeleportationManager().getTeleportQueue().entrySet()) {
                    WrappedTask wrappedTask = wrappedTaskEntry.getValue();
                    wrappedTask.cancel();
                    MessageUtils.sendDebugConsole( "&aWrapped task: " + wrappedTask.toString());
                    MessageUtils.sendDebugConsole( "&aTimed task canceled successfully");
                    getTeleportationManager().getTeleportQueue().remove(wrappedTaskEntry.getKey());
                }
            }
            if (!AutoSaveTaskUtils.getAutoSaveTask().isCancelled()) {
                MessageUtils.sendDebugConsole( "&aWrapped task: " + AutoSaveTaskUtils.getAutoSaveTask().toString());
                MessageUtils.sendDebugConsole( "&aAuto save timed task canceled successfully");
                AutoSaveTaskUtils.getAutoSaveTask().cancel();
            }
            getFoliaLib().getScheduler().cancelAllTasks();
            if (getFoliaLib().isUnsupported()) {
                Bukkit.getScheduler().cancelTasks(this);
                MessageUtils.sendDebugConsole( "&aBukkit scheduler tasks canceled successfully");
            }
            MessageUtils.sendConsole("&3All pending teleport & background tasks stopped successfully.");
        } catch (Exception e) {
            MessageUtils.sendConsole("&3All pending teleport & background tasks stopped successfully.");
        }

        // Saver usermap to usermap.yml
        if (getUsermapStorageUtil() != null) {
            if (!getUsermapStorageUtil().getRawUsermapList().isEmpty()) {
                try {
                    getUsermapStorageUtil().saveUsermap();
                    MessageUtils.sendConsole("&3All users data saved to usermap.yml successfully!");
                } catch (IOException e) {
                    MessageUtils.sendConsole("error", "&4Failed to save usermap data to usermap.yml!");
                    MessageUtils.sendConsole("error", "&4See below error for reason!");
                    e.printStackTrace();
                }
            } else {
                MessageUtils.sendConsole("&3Usermap storage was empty, skipping saving!");
            }
        }

        // Final plugin shutdown message
        MessageUtils.sendConsole("&3Plugin Version: &d&l" + pluginVersion);
        MessageUtils.sendConsole("&3Has been shutdown successfully");
        MessageUtils.sendConsole("&3Goodbye!");
        MessageUtils.sendConsole("-------------------------------------------");

        // Clear any plugin remains
        getTeleportationManager().getTeleportQueue().clear();
        pluginCommands.clear();
        playerMenuUtilityMap.clear();

        placeholderAPIHook = null;
        usermapStorageUtil = null;
        messagesManager = null;
        configManager = null;
        usermapFileManager = null;
        messagesFileManager = null;
        teleportationManager = null;
        versionCheckerUtils = null;
        serverVersion = null;
        foliaLib = null;
        plugin = null;
    }

    public static PlayerMenuUtility getPlayerMenuUtility(Player player) {
        PlayerMenuUtility playerMenuUtility;
        if (!(playerMenuUtilityMap.containsKey(player))) {
            playerMenuUtility = new PlayerMenuUtility(player);
            playerMenuUtilityMap.put(player, playerMenuUtility);
            return playerMenuUtility;
        } else {
            return playerMenuUtilityMap.get(player);
        }
    }

    private void setVersion() {
        try {
            String packageName = Bukkit.getServer().getClass().getPackage().getName();
            String bukkitVersion = Bukkit.getServer().getBukkitVersion();
            if (bukkitVersion.contains("1.20.5")) {
                serverVersion = ServerVersion.v1_20_R5;
            } else if (bukkitVersion.contains("1.20.6")) {
                serverVersion = ServerVersion.v1_20_R5;
            } else if (bukkitVersion.contains("1.21")) {
                serverVersion = ServerVersion.v1_21_R1;
            } else if (bukkitVersion.contains("1.21.1")) {
                serverVersion = ServerVersion.v1_21_R2;
            }else {
                serverVersion = ServerVersion.valueOf(packageName.replace("org.bukkit.craftbukkit.", ""));
            }
        } catch (Exception e) {
            serverVersion = ServerVersion.Other;
            MessageUtils.sendDebugConsole("Failed to detect server version, defaulting to: " + serverVersion);
        }
        MessageUtils.sendDebugConsole("Set server version: " + serverVersion);
    }

    public static EpicHomes getPlugin() {
        return plugin;
    }

    public static FoliaLib getFoliaLib() {
        return foliaLib;
    }

    public static ServerVersion getServerVersion() {
        return serverVersion;
    }

    public static VersionCheckerUtils getVersionCheckerUtils() {
        return versionCheckerUtils;
    }

    public boolean isPluginEnabled() {
        return isPluginEnabled;
    }

    public void setPluginEnabled(boolean pluginEnabled) {
        isPluginEnabled = pluginEnabled;
    }

    public boolean isOnlineMode() {
        return isOnlineMode;
    }

    public void setOnlineMode(boolean onlineMode) {
        isOnlineMode = onlineMode;
    }

    public boolean isGUIEnabled() {
        return isGUIEnabled;
    }

    public void setGUIEnabled(boolean GUIEnabled) {
        isGUIEnabled = GUIEnabled;
    }

    public boolean isUpdateAvailable() {
        return isUpdateAvailable;
    }

    public void setUpdateAvailable(boolean updateAvailable) {
        isUpdateAvailable = updateAvailable;
    }

    public MessagesFileManager getMessagesFileManager() {
        return messagesFileManager;
    }

    public void setMessagesFileManager(MessagesFileManager messagesFileManager) {
        this.messagesFileManager = messagesFileManager;
    }

    public UsermapFileManager getUsermapFileManager() {
        return usermapFileManager;
    }

    public void setUsermapFileManager(UsermapFileManager usermapFileManager) {
        this.usermapFileManager = usermapFileManager;
    }

    public UsermapStorageUtil getUsermapStorageUtil() {
        return usermapStorageUtil;
    }

    public void setUsermapStorageUtil(UsermapStorageUtil usermapStorageUtil) {
        this.usermapStorageUtil = usermapStorageUtil;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public void setConfigManager(ConfigManager configManager) {
        this.configManager = configManager;
    }

    public MessagesManager getMessagesManager() {
        return messagesManager;
    }

    public void setMessagesManager(MessagesManager messagesManager) {
        this.messagesManager = messagesManager;
    }

    public TeleportationManager getTeleportationManager() {
        return teleportationManager;
    }

    public void setTeleportationManager(TeleportationManager teleportationManager) {
        this.teleportationManager = teleportationManager;
    }
}
