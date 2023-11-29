package me.loving11ish.epichomes;

import com.rylinaux.plugman.api.PlugManAPI;
import com.tcoded.folialib.FoliaLib;
import com.tcoded.folialib.wrapper.task.WrappedTask;
import io.papermc.lib.PaperLib;
import me.loving11ish.epichomes.commands.*;
import me.loving11ish.epichomes.externalhooks.PlaceholderAPI;
import me.loving11ish.epichomes.externalhooks.PlaceholderAPIHook;
import me.loving11ish.epichomes.externalhooks.PlugManXAPI;
import me.loving11ish.epichomes.files.UsermapFileManager;
import me.loving11ish.epichomes.files.MessagesFileManager;
import me.loving11ish.epichomes.listeners.*;
import me.loving11ish.epichomes.menusystem.PlayerMenuUtility;
import me.loving11ish.epichomes.updatesystem.JoinEvent;
import me.loving11ish.epichomes.updatesystem.UpdateChecker;
import me.loving11ish.epichomes.utils.AutoSaveTaskUtils;
import me.loving11ish.epichomes.utils.ColorUtils;
import me.loving11ish.epichomes.utils.UsermapStorageUtil;
import me.loving11ish.epichomes.utils.VersionCheckerUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public final class EpicHomes extends JavaPlugin {

    ConsoleCommandSender console = Bukkit.getConsoleSender();

    private final PluginDescriptionFile pluginInfo = getDescription();
    private final String pluginVersion = pluginInfo.getVersion();

    private static EpicHomes plugin;
    private static FoliaLib foliaLib;
    private static VersionCheckerUtils versionCheckerUtils;
    private static boolean GUIEnabled = false;
    private static boolean onlineMode = false;

    private boolean updateAvailable;

    public MessagesFileManager messagesFileManager;
    public UsermapFileManager usermapFileManager;
    public UsermapStorageUtil usermapStorageUtil;

    public HashMap<UUID, WrappedTask> teleportQueue = new HashMap<>();
    private static final HashMap<Player, PlayerMenuUtility> playerMenuUtilityMap = new HashMap<>();
    private final List<String> pluginCommands = new ArrayList<>();

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        foliaLib = new FoliaLib(this);
        versionCheckerUtils = new VersionCheckerUtils();
        versionCheckerUtils.setVersion();

        //Server version compatibility check
        if (versionCheckerUtils.getVersion() < 13||versionCheckerUtils.getVersion() > 20){
            console.sendMessage(ColorUtils.translateColorCodes("&4-------------------------------------------"));
            console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes: &4Your server version is: &d" + Bukkit.getVersion()));
            console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes: &4This plugin is only supported on the Minecraft versions listed below:"));
            console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes: &41.13.x"));
            console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes: &41.14.x"));
            console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes: &41.15.x"));
            console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes: &41.16.x"));
            console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes: &41.17.x"));
            console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes: &41.18.x"));
            console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes: &41.19.x"));
            console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes: &41.20.x"));
            console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes: &4Is now disabling!"));
            console.sendMessage(ColorUtils.translateColorCodes("&4-------------------------------------------"));
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }else {
            console.sendMessage(ColorUtils.translateColorCodes("&a-------------------------------------------"));
            console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes: &aA supported Minecraft version has been detected"));
            console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes: &aYour server version is: &d" + Bukkit.getVersion()));
            console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes: &6Continuing plugin startup"));
            console.sendMessage(ColorUtils.translateColorCodes("&a-------------------------------------------"));
        }

        //Suggest PaperMC if not using
        if (foliaLib.isUnsupported()|| foliaLib.isSpigot()){
            PaperLib.suggestPaper(this);
        }

        //Check if PlugManX is enabled
        if (Bukkit.getPluginManager().isPluginEnabled("PlugManX")||PlugManXAPI.isPlugManXEnabled()){
            if (!PlugManAPI.iDoNotWantToBeUnOrReloaded("EpicHomes")){
                console.sendMessage(ColorUtils.translateColorCodes("&c-------------------------------------------"));
                console.sendMessage(ColorUtils.translateColorCodes("&c-------------------------------------------"));
                console.sendMessage(ColorUtils.translateColorCodes("&4WARNING WARNING WARNING WARNING!"));
                console.sendMessage(ColorUtils.translateColorCodes("&c-------------------------------------------"));
                console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes: &4You appear to be using an unsupported version of &d&lPlugManX"));
                console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes: &4Please &4&lDO NOT USE PLUGMANX TO LOAD/UNLOAD/RELOAD THIS PLUGIN!"));
                console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes: &4Please &4&lFULLY RESTART YOUR SERVER!"));
                console.sendMessage(ColorUtils.translateColorCodes("&c-------------------------------------------"));
                console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes: &4This plugin &4&lHAS NOT &4been validated to use this version of PlugManX!"));
                console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes: &4&lNo official support will be given to you if you use this!"));
                console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes: &4&lUnless Loving11ish has explicitly agreed to help!"));
                console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes: &4Please add EpicHomes to the ignored-plugins list in PlugManX's config.yml"));
                console.sendMessage(ColorUtils.translateColorCodes("&c-------------------------------------------"));
                console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes: &6Continuing plugin startup"));
                console.sendMessage(ColorUtils.translateColorCodes("&c-------------------------------------------"));
                console.sendMessage(ColorUtils.translateColorCodes("&c-------------------------------------------"));
            }else {
                console.sendMessage(ColorUtils.translateColorCodes("&a-------------------------------------------"));
                console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes: &aSuccessfully hooked into PlugManX"));
                console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes: &aSuccessfully added EpicHomes to ignoredPlugins list."));
                console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes: &6Continuing plugin startup"));
                console.sendMessage(ColorUtils.translateColorCodes("&a-------------------------------------------"));
            }
        }else {
            console.sendMessage(ColorUtils.translateColorCodes("-------------------------------------------"));
            console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes: &cPlugManX not found!"));
            console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes: &cDisabling PlugManX hook loader"));
            console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes: &6Continuing plugin startup"));
            console.sendMessage(ColorUtils.translateColorCodes("-------------------------------------------"));
        }

        //Load the plugin configs
        getConfig().options().copyDefaults();
        saveDefaultConfig();

        //Load messages.yml
        this.messagesFileManager = new MessagesFileManager();
        messagesFileManager.MessagesFileManager(this);

        //Load usermap.yml
        this.usermapFileManager = new UsermapFileManager();
        usermapFileManager.UsermapFileManager(this);

        //Load internal plugin objects
        usermapStorageUtil = new UsermapStorageUtil();

        //Load usermap contents to memory
        if (usermapFileManager != null){
            if (usermapFileManager.getUsermapConfig().contains("users.data")){
                try {
                    usermapStorageUtil.loadUsermap();
                    console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes: &3Successfully loaded usermap to memory."));
                } catch (IOException e) {
                    console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes: &4Failed to load data from usermap.yml!"));
                    console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes: &4See below for errors!"));
                    console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes: &4Disabling Plugin!"));
                    e.printStackTrace();
                    Bukkit.getPluginManager().disablePlugin(this);
                    return;
                }
            }
        }

        //Check for PersistentDataContainers for GUI system
        if (versionCheckerUtils.getVersion() >= 14) {
            GUIEnabled = getConfig().getBoolean("gui-system.use-global-gui.enabled", true);
            if (GUIEnabled){
                console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes: &3Global GUI system enabled!"));
            }else {
                console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes: &cGlobal GUI system disabled!"));
            }
        }else {
            console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes: &cYour current server version does not support PersistentDataContainers!"));
            console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes: &c&lGlobal GUI system disabled!"));
        }

        //Register commands
        HomeCommand homeCommand = new HomeCommand();
        getCommand("home").setExecutor(homeCommand);
        getCommand("home").setTabCompleter(homeCommand);
        getCommand("sethome").setExecutor(new SetHomeCommand());
        getCommand("delhome").setExecutor(new DeleteHomeCommand());
        getCommand("importhomes").setExecutor(new HomeImportCommand());

        //Register events
        pluginCommands.add("/home");
        pluginCommands.add("/sethome");
        pluginCommands.add("/delhome");
        pluginCommands.add("/h");
        pluginCommands.add("/eh");
        pluginCommands.add("/delhome");
        pluginCommands.add("/homes");
        pluginCommands.add("/epichomes");
        getServer().getPluginManager().registerEvents(new MenuEvent(), this);
        getServer().getPluginManager().registerEvents(new JoinEvent(), this);
        getServer().getPluginManager().registerEvents(new PlayerMovementEvent(), this);
        getServer().getPluginManager().registerEvents(new PlayerConnectionEvent(), this);
        getServer().getPluginManager().registerEvents(new PlayerPreConnectionEvent(), this);
        getServer().getPluginManager().registerEvents(new PlayerDisconnectionEvent(), this);
        getServer().getPluginManager().registerEvents(new PlayerCommandSendEvent(pluginCommands), this);

        //Update banned names lists
        HomeCommand.updateBannedNamesList();
        SetHomeCommand.updateBannedNamesList();

        //Register PlaceHolderAPI hooks
        if (Bukkit.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")||PlaceholderAPI.isPlaceholderAPIEnabled()){
            new PlaceholderAPIHook().register();
            console.sendMessage(ColorUtils.translateColorCodes("-------------------------------------------"));
            console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes: &3PlaceholderAPI found!"));
            console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes: &3External placeholders enabled!"));
            console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes: &6Continuing plugin startup"));
            console.sendMessage(ColorUtils.translateColorCodes("-------------------------------------------"));
        }else {
            console.sendMessage(ColorUtils.translateColorCodes("-------------------------------------------"));
            console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes: &cPlaceholderAPI not found!"));
            console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes: &cExternal placeholders disabled!"));
            console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes: &6Continuing plugin startup"));
            console.sendMessage(ColorUtils.translateColorCodes("-------------------------------------------"));
        }

        //Plugin startup message
        console.sendMessage(ColorUtils.translateColorCodes("-------------------------------------------"));
        console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes: &3Plugin by: &b&lLoving11ish"));
        console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes: &3has been loaded successfully"));
        console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes: &3Plugin Version: &d&l" + pluginVersion));
        if (getConfig().getBoolean("general.developer-debug-mode.enabled", false)){
            console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes-Debug: &aDeveloper debug mode enabled!"));
            console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes-Debug: &aThis WILL fill the console"));
            console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes-Debug: &awith additional EpicHomes information!"));
            console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes-Debug: &aThis setting is not intended for "));
            console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes-Debug: &acontinous use!"));
        }
        console.sendMessage(ColorUtils.translateColorCodes("-------------------------------------------"));

        //Check for available updates
        new UpdateChecker(109590).getVersion(version -> {
            String prefix = messagesFileManager.getMessagesConfig().getString("global-prefix", "&f[&6Epic&bHomes&f]&r");
            String PREFIX_PLACEHOLDER = "%PREFIX%";
            if (this.getDescription().getVersion().equalsIgnoreCase(version)) {
                console.sendMessage(ColorUtils.translateColorCodes(messagesFileManager.getMessagesConfig().getString("no-update-available.1").replace(PREFIX_PLACEHOLDER, prefix)));
                console.sendMessage(ColorUtils.translateColorCodes(messagesFileManager.getMessagesConfig().getString("no-update-available.2").replace(PREFIX_PLACEHOLDER, prefix)));
                console.sendMessage(ColorUtils.translateColorCodes(messagesFileManager.getMessagesConfig().getString("no-update-available.3").replace(PREFIX_PLACEHOLDER, prefix)));
                this.updateAvailable = false;
            }else {
                console.sendMessage(ColorUtils.translateColorCodes(messagesFileManager.getMessagesConfig().getString("update-available.1").replace(PREFIX_PLACEHOLDER, prefix)));
                console.sendMessage(ColorUtils.translateColorCodes(messagesFileManager.getMessagesConfig().getString("update-available.2").replace(PREFIX_PLACEHOLDER, prefix)));
                console.sendMessage(ColorUtils.translateColorCodes(messagesFileManager.getMessagesConfig().getString("update-available.3").replace(PREFIX_PLACEHOLDER, prefix)));
                this.updateAvailable = true;
            }
        });

        //Start auto save task
        foliaLib.getImpl().runLaterAsync(() -> {
            AutoSaveTaskUtils.runAutoSaveTask();
            String PREFIX_PLACEHOLDER = "%PREFIX%";
            String prefix = messagesFileManager.getMessagesConfig().getString("global-prefix", "&f[&6Epic&bHomes&f]&r");
            console.sendMessage(ColorUtils.translateColorCodes(messagesFileManager.getMessagesConfig().getString("auto-save-started").replace(PREFIX_PLACEHOLDER, prefix)));
        }, 5L, TimeUnit.SECONDS);
    }

    @Override
    public void onDisable() {
        //Plugin shutdown logic

        //Unregister event listeners
        HandlerList.unregisterAll(this);

        //Safely stop the background tasks if running
        console.sendMessage(ColorUtils.translateColorCodes("-------------------------------------------"));
        console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes: &3Plugin by: &b&lLoving11ish"));
        try {
            if (!teleportQueue.isEmpty()){
                for (Map.Entry<UUID, WrappedTask> wrappedTaskEntry: teleportQueue.entrySet()){
                    WrappedTask wrappedTask = wrappedTaskEntry.getValue();
                    wrappedTask.cancel();
                    if (getConfig().getBoolean("general.developer-debug-mode.enabled")){
                        console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes-Debug: &aWrapped task: " + wrappedTask.toString()));
                        console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes-Debug: &aTimed task canceled successfully"));
                    }
                    teleportQueue.remove(wrappedTaskEntry.getKey());
                }
            }
            if (!AutoSaveTaskUtils.getAutoSaveTask().isCancelled()){
                if (getConfig().getBoolean("general.developer-debug-mode.enabled")){
                    console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes-Debug: &aWrapped task: " + AutoSaveTaskUtils.getAutoSaveTask().toString()));
                    console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes-Debug: &aAuto save timed task canceled successfully"));
                }
                AutoSaveTaskUtils.getAutoSaveTask().cancel();
            }
            if (foliaLib.isUnsupported()){
                Bukkit.getScheduler().cancelTasks(this);
                if (getConfig().getBoolean("general.developer-debug-mode.enabled")){
                    console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes-Debug: &aBukkit scheduler tasks canceled successfully"));
                }
            }
            console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes: &3All pending teleport & background tasks stopped successfully."));
        }catch (Exception e){
            console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes: &3All pending teleport & background tasks stopped successfully."));
        }

        //Saver usermap to usermap.yml
        if (usermapStorageUtil != null){
            if (!usermapStorageUtil.getRawUsermapList().isEmpty()){
                try {
                    usermapStorageUtil.saveUsermap();
                    console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes: &3All users data saved to usermap.yml successfully!"));
                } catch (IOException e) {
                    console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes: &4Failed to save usermap data to usermap.yml!"));
                    console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes: &4See below error for reason!"));
                    e.printStackTrace();
                }
            }else {
                console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes: &3Usermap storage was empty, skipping saving!"));
            }
        }

        //Final plugin shutdown message
        console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes: &3Plugin Version: &d&l" + pluginVersion));
        console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes: &3Has been shutdown successfully"));
        console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes: &3Goodbye!"));
        console.sendMessage(ColorUtils.translateColorCodes("-------------------------------------------"));

        //Clear any plugin remains
        usermapStorageUtil = null;
        usermapFileManager = null;
        messagesFileManager = null;
        versionCheckerUtils = null;
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

    public static EpicHomes getPlugin() {
        return plugin;
    }

    public static FoliaLib getFoliaLib() {
        return foliaLib;
    }

    public static VersionCheckerUtils getVersionCheckerUtils() {
        return versionCheckerUtils;
    }

    public static boolean isGUIEnabled() {
        return GUIEnabled;
    }

    public static void setGUIEnabled(boolean GUIEnabled) {
        EpicHomes.GUIEnabled = GUIEnabled;
    }

    public static boolean isOnlineMode() {
        return onlineMode;
    }

    public static void setOnlineMode(boolean onlineMode) {
        EpicHomes.onlineMode = onlineMode;
    }

    public boolean isUpdateAvailable() {
        return updateAvailable;
    }
}
