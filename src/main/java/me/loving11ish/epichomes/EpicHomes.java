package me.loving11ish.epichomes;

import com.tcoded.folialib.FoliaLib;
import com.tcoded.folialib.wrapper.WrappedTask;
import me.loving11ish.epichomes.commands.HomeCommand;
import me.loving11ish.epichomes.commands.HomeCommandTabCompleter;
import me.loving11ish.epichomes.commands.HomeImportCommand;
import me.loving11ish.epichomes.files.UsermapFileManager;
import me.loving11ish.epichomes.files.MessagesFileManager;
import me.loving11ish.epichomes.listeners.MenuEvent;
import me.loving11ish.epichomes.listeners.PlayerConnectionEvent;
import me.loving11ish.epichomes.listeners.PlayerDisconnectionEvent;
import me.loving11ish.epichomes.listeners.PlayerMovementEvent;
import me.loving11ish.epichomes.menusystem.PlayerMenuUtility;
import me.loving11ish.epichomes.updatesystem.JoinEvent;
import me.loving11ish.epichomes.updatesystem.UpdateChecker;
import me.loving11ish.epichomes.utils.ColorUtils;
import me.loving11ish.epichomes.utils.UsermapStorageUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.*;
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

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;

        //Server version compatibility check
        if (!(Bukkit.getServer().getVersion().contains("1.13")||Bukkit.getServer().getVersion().contains("1.14")||
                Bukkit.getServer().getVersion().contains("1.15")||Bukkit.getServer().getVersion().contains("1.16")||
                Bukkit.getServer().getVersion().contains("1.17")||Bukkit.getServer().getVersion().contains("1.18")||
                Bukkit.getServer().getVersion().contains("1.19"))){
            logger.warning(ColorUtils.translateColorCodes("&4-------------------------------------------"));
            logger.warning(ColorUtils.translateColorCodes("&6EpicHomes: &4This plugin is only supported on the Minecraft versions listed below:"));
            logger.warning(ColorUtils.translateColorCodes("&6EpicHomes: &41.13.x"));
            logger.warning(ColorUtils.translateColorCodes("&6EpicHomes: &41.14.x"));
            logger.warning(ColorUtils.translateColorCodes("&6EpicHomes: &41.15.x"));
            logger.warning(ColorUtils.translateColorCodes("&6EpicHomes: &41.16.x"));
            logger.warning(ColorUtils.translateColorCodes("&6EpicHomes: &41.17.x"));
            logger.warning(ColorUtils.translateColorCodes("&6EpicHomes: &41.18.x"));
            logger.warning(ColorUtils.translateColorCodes("&6EpicHomes: &41.19.x"));
            logger.warning(ColorUtils.translateColorCodes("&6EpicHomes: &4Is now disabling!"));
            logger.warning(ColorUtils.translateColorCodes("&4-------------------------------------------"));
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }else {
            logger.info(ColorUtils.translateColorCodes("&a-------------------------------------------"));
            logger.info(ColorUtils.translateColorCodes("&6EpicHomes: &aA supported Minecraft version has been detected"));
            logger.info(ColorUtils.translateColorCodes("&6EpicHomes: &6Continuing plugin startup"));
            logger.info(ColorUtils.translateColorCodes("&a-------------------------------------------"));
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

        //Register commands
        getCommand("home").setExecutor(new HomeCommand());
        getCommand("importhomes").setExecutor(new HomeImportCommand());

        //Register tabCompleter
        getCommand("home").setTabCompleter(new HomeCommandTabCompleter());

        //Register events
        getServer().getPluginManager().registerEvents(new PlayerConnectionEvent(), this);
        getServer().getPluginManager().registerEvents(new PlayerDisconnectionEvent(), this);
        getServer().getPluginManager().registerEvents(new PlayerMovementEvent(), this);
        getServer().getPluginManager().registerEvents(new MenuEvent(), this);
        getServer().getPluginManager().registerEvents(new JoinEvent(), this);

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
//        new UpdateChecker(97163).getVersion(version -> {
//            if (this.getDescription().getVersion().equalsIgnoreCase(version)) {
//                logger.info(ColorUtils.translateColorCodes(messagesFileManager.getMessagesConfig().getString("no-update-available.1")));
//                logger.info(ColorUtils.translateColorCodes(messagesFileManager.getMessagesConfig().getString("no-update-available.2")));
//                logger.info(ColorUtils.translateColorCodes(messagesFileManager.getMessagesConfig().getString("no-update-available.3")));
//            }else {
//                logger.warning(ColorUtils.translateColorCodes(messagesFileManager.getMessagesConfig().getString("update-available.1")));
//                logger.warning(ColorUtils.translateColorCodes(messagesFileManager.getMessagesConfig().getString("update-available.2")));
//                logger.warning(ColorUtils.translateColorCodes(messagesFileManager.getMessagesConfig().getString("update-available.3")));
//            }
//        });
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

        //Safely stop the background tasks if running
        logger.info(ColorUtils.translateColorCodes("-------------------------------------------"));
        logger.info(ColorUtils.translateColorCodes("&6EpicHomes: &3Plugin by: &b&lLoving11ish"));
        try {
            if (!teleportQueue.isEmpty()){
                for (Map.Entry<UUID, WrappedTask> wrappedTaskEntry: teleportQueue.entrySet()){
                    WrappedTask wrappedTask = wrappedTaskEntry.getValue();
                    wrappedTask.cancel();
                    teleportQueue.remove(wrappedTaskEntry.getKey());
                }
            }
            FoliaLib foliaLib = new FoliaLib(this);
            if (foliaLib.isUnsupported()){
                Bukkit.getScheduler().cancelTasks(this);
            }
            logger.info(ColorUtils.translateColorCodes("&6EpicHomes: &3All pending teleport tasks stopped successfully."));
        }catch (Exception e){
            logger.info(ColorUtils.translateColorCodes("&6EpicHomes: &3All pending teleport tasks stopped successfully."));
        }

        //Saver usermap to usermap.yml
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

    public static EpicHomes getPlugin() {
        return plugin;
    }
}
