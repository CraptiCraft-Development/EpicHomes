package me.loving11ish.epichomes;

import com.rylinaux.plugman.api.PlugManAPI;
import com.tcoded.folialib.FoliaLib;
import com.tcoded.folialib.wrapper.WrappedTask;
import io.papermc.lib.PaperLib;
import me.loving11ish.epichomes.commands.*;
import me.loving11ish.epichomes.externalhooks.PlaceholderAPIHook;
import me.loving11ish.epichomes.files.UsermapFileManager;
import me.loving11ish.epichomes.files.MessagesFileManager;
import me.loving11ish.epichomes.listeners.*;
import me.loving11ish.epichomes.menusystem.PlayerMenuUtility;
import me.loving11ish.epichomes.updatesystem.JoinEvent;
import me.loving11ish.epichomes.updatesystem.UpdateChecker;
import me.loving11ish.epichomes.utils.AutoSaveTaskUtils;
import me.loving11ish.epichomes.utils.ColorUtils;
import me.loving11ish.epichomes.utils.UsermapStorageUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public final class EpicHomes extends JavaPlugin {

    private final PluginDescriptionFile pluginInfo = getDescription();
    private final String pluginVersion = pluginInfo.getVersion();
    Logger logger = this.getLogger();

    private static EpicHomes plugin;
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

        //Server version compatibility check
        if (!(Bukkit.getVersion().contains("1.13")||Bukkit.getVersion().contains("1.14")||Bukkit.getVersion().contains("1.15")
                ||Bukkit.getVersion().contains("1.16")||Bukkit.getVersion().contains("1.17")||Bukkit.getVersion().contains("1.18")
                ||Bukkit.getVersion().contains("1.19")||Bukkit.getVersion().contains("1.20"))){
            logger.warning(ColorUtils.translateColorCodes("&4-------------------------------------------"));
            logger.warning(ColorUtils.translateColorCodes("&6EpicHomes: &4Your server version is: " + Bukkit.getVersion()));
            logger.warning(ColorUtils.translateColorCodes("&6EpicHomes: &4This plugin is only supported on the Minecraft versions listed below:"));
            logger.warning(ColorUtils.translateColorCodes("&6EpicHomes: &41.13.x"));
            logger.warning(ColorUtils.translateColorCodes("&6EpicHomes: &41.14.x"));
            logger.warning(ColorUtils.translateColorCodes("&6EpicHomes: &41.15.x"));
            logger.warning(ColorUtils.translateColorCodes("&6EpicHomes: &41.16.x"));
            logger.warning(ColorUtils.translateColorCodes("&6EpicHomes: &41.17.x"));
            logger.warning(ColorUtils.translateColorCodes("&6EpicHomes: &41.18.x"));
            logger.warning(ColorUtils.translateColorCodes("&6EpicHomes: &41.19.x"));
            logger.warning(ColorUtils.translateColorCodes("&6EpicHomes: &41.20.x"));
            logger.warning(ColorUtils.translateColorCodes("&6EpicHomes: &4Is now disabling!"));
            logger.warning(ColorUtils.translateColorCodes("&4-------------------------------------------"));
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }else {
            logger.info(ColorUtils.translateColorCodes("&a-------------------------------------------"));
            logger.info(ColorUtils.translateColorCodes("&6EpicHomes: &aA supported Minecraft version has been detected"));
            logger.info(ColorUtils.translateColorCodes("&6EpicHomes: &4Your server version is: " + Bukkit.getVersion()));
            logger.info(ColorUtils.translateColorCodes("&6EpicHomes: &6Continuing plugin startup"));
            logger.info(ColorUtils.translateColorCodes("&a-------------------------------------------"));
        }

        //Suggest PaperMC if not using
        FoliaLib foliaLib = new FoliaLib(this);
        if (foliaLib.isUnsupported()|| foliaLib.isSpigot()){
            PaperLib.suggestPaper(this);
        }

        //Check if PlugManX is enabled
        if (isPlugManXEnabled()){
            if (!PlugManAPI.iDoNotWantToBeUnOrReloaded("EpicHomes")){
                logger.severe(ColorUtils.translateColorCodes("&c-------------------------------------------"));
                logger.severe(ColorUtils.translateColorCodes("&c-------------------------------------------"));
                logger.severe(ColorUtils.translateColorCodes("&4WARNING WARNING WARNING WARNING!"));
                logger.severe(ColorUtils.translateColorCodes("&c-------------------------------------------"));
                logger.severe(ColorUtils.translateColorCodes("&6EpicHomes: &4You appear to be using an unsupported version of &d&lPlugManX"));
                logger.severe(ColorUtils.translateColorCodes("&6EpicHomes: &4Please &4&lDO NOT USE PLUGMANX TO LOAD/UNLOAD/RELOAD THIS PLUGIN!"));
                logger.severe(ColorUtils.translateColorCodes("&6EpicHomes: &4Please &4&lFULLY RESTART YOUR SERVER!"));
                logger.severe(ColorUtils.translateColorCodes("&c-------------------------------------------"));
                logger.severe(ColorUtils.translateColorCodes("&6EpicHomes: &4This plugin &4&lHAS NOT &4been validated to use this version of PlugManX!"));
                logger.severe(ColorUtils.translateColorCodes("&6EpicHomes: &4&lNo official support will be given to you if you use this!"));
                logger.severe(ColorUtils.translateColorCodes("&6EpicHomes: &4&lUnless Loving11ish has explicitly agreed to help!"));
                logger.severe(ColorUtils.translateColorCodes("&6EpicHomes: &4Please add EpicHomes to the ignored-plugins list in PlugManX's config.yml"));
                logger.severe(ColorUtils.translateColorCodes("&c-------------------------------------------"));
                logger.severe(ColorUtils.translateColorCodes("&6EpicHomes: &6Continuing plugin startup"));
                logger.severe(ColorUtils.translateColorCodes("&c-------------------------------------------"));
                logger.severe(ColorUtils.translateColorCodes("&c-------------------------------------------"));
            }else {
                logger.info(ColorUtils.translateColorCodes("&a-------------------------------------------"));
                logger.info(ColorUtils.translateColorCodes("&6EpicHomes: &aSuccessfully hooked into PlugManX"));
                logger.info(ColorUtils.translateColorCodes("&6EpicHomes: &aSuccessfully added EpicHomes to ignoredPlugins list."));
                logger.info(ColorUtils.translateColorCodes("&6EpicHomes: &6Continuing plugin startup"));
                logger.info(ColorUtils.translateColorCodes("&a-------------------------------------------"));
            }
        }else {
            logger.info(ColorUtils.translateColorCodes("-------------------------------------------"));
            logger.info(ColorUtils.translateColorCodes("&6EpicHomes: &cPlugManX not found!"));
            logger.info(ColorUtils.translateColorCodes("&6EpicHomes: &cDisabling PlugManX hook loader"));
            logger.info(ColorUtils.translateColorCodes("&6EpicHomes: &6Continuing plugin startup"));
            logger.info(ColorUtils.translateColorCodes("-------------------------------------------"));
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
                    logger.info(ColorUtils.translateColorCodes("&6EpicHomes: &3Successfully loaded usermap to memory."));
                } catch (IOException e) {
                    logger.severe(ColorUtils.translateColorCodes("&6EpicHomes: &4Failed to load data from usermap.yml!"));
                    logger.severe(ColorUtils.translateColorCodes("&6EpicHomes: &4See below for errors!"));
                    logger.severe(ColorUtils.translateColorCodes("&6EpicHomes: &4Disabling Plugin!"));
                    e.printStackTrace();
                    Bukkit.getPluginManager().disablePlugin(this);
                    return;
                }
            }
        }

        //Register commands
        HomeCommand homeCommand = new HomeCommand();
        getCommand("home").setExecutor(homeCommand);
        getCommand("home").setTabCompleter(homeCommand);
        getCommand("importhomes").setExecutor(new HomeImportCommand());
        getCommand("sethome").setExecutor(new SetHomeCommand());
        getCommand("delhome").setExecutor(new DeleteHomeCommand());
        pluginCommands.add("/home");
        pluginCommands.add("/sethome");
        pluginCommands.add("/delhome");
        pluginCommands.add("/h");
        pluginCommands.add("/eh");
        pluginCommands.add("/delhome");
        pluginCommands.add("/homes");
        pluginCommands.add("/epichomes");

        //Register events
        getServer().getPluginManager().registerEvents(new PlayerConnectionEvent(), this);
        getServer().getPluginManager().registerEvents(new PlayerDisconnectionEvent(), this);
        getServer().getPluginManager().registerEvents(new PlayerMovementEvent(), this);
        getServer().getPluginManager().registerEvents(new MenuEvent(), this);
        getServer().getPluginManager().registerEvents(new JoinEvent(), this);
        getServer().getPluginManager().registerEvents(new PlayerCommandSendEvent(pluginCommands), this);

        //Register PlaceHolderAPI hooks
        if (Bukkit.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")||isPlaceholderAPIEnabled()){
            new PlaceholderAPIHook().register();
            logger.info(ColorUtils.translateColorCodes("-------------------------------------------"));
            logger.info(ColorUtils.translateColorCodes("&6EpicHomes: &3PlaceholderAPI found!"));
            logger.info(ColorUtils.translateColorCodes("&6EpicHomes: &3External placeholders enabled!"));
            logger.info(ColorUtils.translateColorCodes("&6EpicHomes: &6Continuing plugin startup"));
            logger.info(ColorUtils.translateColorCodes("-------------------------------------------"));
        }else {
            logger.warning(ColorUtils.translateColorCodes("-------------------------------------------"));
            logger.warning(ColorUtils.translateColorCodes("&6EpicHomes: &cPlaceholderAPI not found!"));
            logger.warning(ColorUtils.translateColorCodes("&6EpicHomes: &cExternal placeholders disabled!"));
            logger.info(ColorUtils.translateColorCodes("&6EpicHomes: &6Continuing plugin startup"));
            logger.warning(ColorUtils.translateColorCodes("-------------------------------------------"));
        }

        //Plugin startup message
        logger.info(ColorUtils.translateColorCodes("-------------------------------------------"));
        logger.info(ColorUtils.translateColorCodes("&6EpicHomes: &3Plugin by: &b&lLoving11ish"));
        logger.info(ColorUtils.translateColorCodes("&6EpicHomes: &3has been loaded successfully"));
        logger.info(ColorUtils.translateColorCodes("&6EpicHomes: &3Plugin Version: &d&l" + pluginVersion));
        if (getConfig().getBoolean("general.developer-debug-mode.enabled")){
            logger.warning(ColorUtils.translateColorCodes("&6EpicHomes-Debug: &aDeveloper debug mode enabled!"));
            logger.warning(ColorUtils.translateColorCodes("&6EpicHomes-Debug: &aThis WILL fill the console"));
            logger.warning(ColorUtils.translateColorCodes("&6EpicHomes-Debug: &awith additional EpicHomes information!"));
            logger.warning(ColorUtils.translateColorCodes("&6EpicHomes-Debug: &aThis setting is not intended for "));
            logger.warning(ColorUtils.translateColorCodes("&6EpicHomes-Debug: &acontinous use!"));
        }
        logger.info(ColorUtils.translateColorCodes("-------------------------------------------"));

        //Check for available updates
        new UpdateChecker(109590).getVersion(version -> {
            String prefix = messagesFileManager.getMessagesConfig().getString("global-prefix");
            String PREFIX_PLACEHOLDER = "%PREFIX%";
            if (this.getDescription().getVersion().equalsIgnoreCase(version)) {
                logger.info(ColorUtils.translateColorCodes(messagesFileManager.getMessagesConfig().getString("no-update-available.1").replace(PREFIX_PLACEHOLDER, prefix)));
                logger.info(ColorUtils.translateColorCodes(messagesFileManager.getMessagesConfig().getString("no-update-available.2").replace(PREFIX_PLACEHOLDER, prefix)));
                logger.info(ColorUtils.translateColorCodes(messagesFileManager.getMessagesConfig().getString("no-update-available.3").replace(PREFIX_PLACEHOLDER, prefix)));
            }else {
                logger.warning(ColorUtils.translateColorCodes(messagesFileManager.getMessagesConfig().getString("update-available.1").replace(PREFIX_PLACEHOLDER, prefix)));
                logger.warning(ColorUtils.translateColorCodes(messagesFileManager.getMessagesConfig().getString("update-available.2").replace(PREFIX_PLACEHOLDER, prefix)));
                logger.warning(ColorUtils.translateColorCodes(messagesFileManager.getMessagesConfig().getString("update-available.3").replace(PREFIX_PLACEHOLDER, prefix)));
            }
        });

        //Start auto save task
        if (getConfig().getBoolean("general.run-auto-save-task.enabled")){
            foliaLib.getImpl().runLaterAsync(new Runnable() {
                @Override
                public void run() {
                    AutoSaveTaskUtils.runAutoSaveTaskOne();
                    logger.info(ColorUtils.translateColorCodes(messagesFileManager.getMessagesConfig().getString("auto-save-started")));
                }
            }, 5L, TimeUnit.SECONDS);
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

        //Unregister event listeners
        HandlerList.unregisterAll(this);

        //Safely stop the background tasks if running
        logger.info(ColorUtils.translateColorCodes("-------------------------------------------"));
        logger.info(ColorUtils.translateColorCodes("&6EpicHomes: &3Plugin by: &b&lLoving11ish"));
        try {
            if (!teleportQueue.isEmpty()){
                for (Map.Entry<UUID, WrappedTask> wrappedTaskEntry: teleportQueue.entrySet()){
                    WrappedTask wrappedTask = wrappedTaskEntry.getValue();
                    wrappedTask.cancel();
                    if (getConfig().getBoolean("general.developer-debug-mode.enabled")){
                        logger.info(ColorUtils.translateColorCodes("&6EpicHomes-Debug: &aWrapped task: " + wrappedTask.toString()));
                        logger.info(ColorUtils.translateColorCodes("&6EpicHomes-Debug: &aTimed task canceled successfully"));
                    }
                    teleportQueue.remove(wrappedTaskEntry.getKey());
                }
            }
            FoliaLib foliaLib = new FoliaLib(this);
            if (foliaLib.isUnsupported()){
                Bukkit.getScheduler().cancelTasks(this);
                if (getConfig().getBoolean("general.developer-debug-mode.enabled")){
                    logger.info(ColorUtils.translateColorCodes("&6EpicHomes-Debug: &aBukkit scheduler tasks canceled successfully"));
                }
            }
            logger.info(ColorUtils.translateColorCodes("&6EpicHomes: &3All pending teleport tasks stopped successfully."));
        }catch (Exception e){
            logger.info(ColorUtils.translateColorCodes("&6EpicHomes: &3All pending teleport tasks stopped successfully."));
        }

        //Saver usermap to usermap.yml
        if (usermapStorageUtil != null){
            if (!usermapStorageUtil.getRawUsermapList().isEmpty()){
                try {
                    usermapStorageUtil.saveUsermap();
                    logger.info(ColorUtils.translateColorCodes("&6EpicHomes: &3All users data saved to usermap.yml successfully!"));
                } catch (IOException e) {
                    logger.severe(ColorUtils.translateColorCodes("&6EpicHomes: &4Failed to save usermap data to usermap.yml!"));
                    logger.severe(ColorUtils.translateColorCodes("&6EpicHomes: &4See below error for reason!"));
                    e.printStackTrace();
                }
            }else {
                logger.info(ColorUtils.translateColorCodes("&6EpicHomes: &3Usermap storage was empty, skipping saving!"));
            }
        }

        //Final plugin shutdown message
        logger.info(ColorUtils.translateColorCodes("&6EpicHomes: &3Plugin Version: &d&l" + pluginVersion));
        logger.info(ColorUtils.translateColorCodes("&6EpicHomes: &3Has been shutdown successfully"));
        logger.info(ColorUtils.translateColorCodes("&6EpicHomes: &3Goodbye!"));
        logger.info(ColorUtils.translateColorCodes("-------------------------------------------"));

        //Clear any plugin remains
        usermapStorageUtil = null;
        usermapFileManager = null;
        messagesFileManager = null;
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

    public boolean isPlugManXEnabled() {
        try {
            Class.forName("com.rylinaux.plugman.PlugMan");
            if (getConfig().getBoolean("general.developer-debug-mode.enabled")){
                logger.info(ColorUtils.translateColorCodes("&6EpicHomes-Debug: &aFound PlugManX main class at:"));
                logger.info(ColorUtils.translateColorCodes("&6EpicHomes-Debug: &dcom.rylinaux.plugman.PlugMan"));
            }
            return true;
        }catch (ClassNotFoundException e){
            if (getConfig().getBoolean("general.developer-debug-mode.enabled")){
                logger.info(ColorUtils.translateColorCodes("&6EpicHomes-Debug: &aCould not find PlugManX main class at:"));
                logger.info(ColorUtils.translateColorCodes("&6EpicHomes-Debug: &dcom.rylinaux.plugman.PlugMan"));
            }
            return false;
        }
    }

    public boolean isPlaceholderAPIEnabled() {
        try {
            Class.forName("me.clip.placeholderapi.PlaceholderAPIPlugin");
            if (getConfig().getBoolean("general.developer-debug-mode.enabled")){
                logger.info(ColorUtils.translateColorCodes("&6EpicHomes-Debug: &aFound PlaceholderAPI main class at:"));
                logger.info(ColorUtils.translateColorCodes("&6EpicHomes-Debug: &dme.clip.placeholderapi.PlaceholderAPIPlugin"));
            }
            return true;
        }catch (ClassNotFoundException e){
            if (getConfig().getBoolean("general.developer-debug-mode.enabled")){
                logger.info(ColorUtils.translateColorCodes("&6EpicHomes-Debug: &aCould not find PlaceholderAPI main class at:"));
                logger.info(ColorUtils.translateColorCodes("&6EpicHomes-Debug: &dme.clip.placeholderapi.PlaceholderAPIPlugin"));
            }
            return false;
        }
    }

    public static EpicHomes getPlugin() {
        return plugin;
    }
}
