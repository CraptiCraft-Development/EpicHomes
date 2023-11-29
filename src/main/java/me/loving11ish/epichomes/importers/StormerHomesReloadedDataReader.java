package me.loving11ish.epichomes.importers;

import me.loving11ish.epichomes.EpicHomes;
import me.loving11ish.epichomes.models.User;
import me.loving11ish.epichomes.utils.ColorUtils;
import me.loving11ish.epichomes.utils.UsermapStorageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class StormerHomesReloadedDataReader {

    ConsoleCommandSender console = Bukkit.getConsoleSender();

    FileConfiguration config = EpicHomes.getPlugin().getConfig();
    FileConfiguration usermapConfig = EpicHomes.getPlugin().usermapFileManager.getUsermapConfig();

    public boolean homesImportSuccessful = false;

    private UsermapStorageUtil usermapStorageUtil = EpicHomes.getPlugin().usermapStorageUtil;

    private YamlConfiguration fileReader;

    private String world;
    private String homeName;
    private String playerName;
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;

    public StormerHomesReloadedDataReader() {
        try {
            fileReader = new YamlConfiguration();
            File stormerData = Bukkit.getServer().getPluginManager().getPlugin("StormerHomesReloaded").getDataFolder();
            File stormerConfig = new File(stormerData, "config.yml");
            fileReader.load(stormerConfig);
            if (config.getBoolean("general.developer-debug-mode.enabled", false)||!usermapConfig.getBoolean("import-completed")){
                console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes-Debug: &aFound StormerHomesReloaded config.yml and loaded successfully."));
            }
            if (loadHomes()){
                this.homesImportSuccessful = true;
            }
        }catch (IOException | InvalidConfigurationException | NullPointerException e) {
            console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes: &4&lCannot find StormerHomesReloaded data file!"));
        }
        this.world = null;
        this.homeName = null;
        this.playerName = null;
        this.x = 0;
        this.y = 0;
        this.z = 0;
        this.yaw = 0f;
        this.pitch = 0f;
    }

    private boolean loadHomes() {
        try {
            if (fileReader.getConfigurationSection("homes2") != null){
                if (config.getBoolean("general.developer-debug-mode.enabled", false)||!usermapConfig.getBoolean("import-completed")){
                    console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes-Debug: &aFound homes2 section in StormerHomesReloaded config.yml"));
                }
                fileReader.getConfigurationSection("homes2").getKeys(false).forEach(key -> {
                    HashMap<String, Location> homes = new HashMap<>();
                    fileReader.getConfigurationSection("homes2." + key).getKeys(false).forEach(home -> {
                        homeName = home;
                        if (config.getBoolean("general.developer-debug-mode.enabled", false)||!usermapConfig.getBoolean("import-completed")){
                            console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes-Debug: &aFound home name entry of: " + home + " &afor UUID: " + key));
                        }
                        world = fileReader.getString("homes2." + key + "." + home + "." + "world");
                        if (config.getBoolean("general.developer-debug-mode.enabled", false)||!usermapConfig.getBoolean("import-completed")){
                            console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes-Debug: &aFound world entry for home: " + home + ". &aWorld entry: " + world));
                        }
                        playerName = fileReader.getString("homes2." + key + "." + home + "." + "playername");
                        if (config.getBoolean("general.developer-debug-mode.enabled", false)||!usermapConfig.getBoolean("import-completed")){
                            console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes-Debug: &aFound playername entry for home: " + home + ". &aPlayerName entry: " + playerName));
                        }
                        x = fileReader.getDouble("homes2." + key + "." + home + "." + "x");
                        if (config.getBoolean("general.developer-debug-mode.enabled", false)||!usermapConfig.getBoolean("import-completed")){
                            console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes-Debug: &aFound x entry for home: " + home + ". &aX entry: " + x));
                        }
                        y = fileReader.getDouble("homes2." + key + "." + home + "." + "y");
                        if (config.getBoolean("general.developer-debug-mode.enabled", false)||!usermapConfig.getBoolean("import-completed")){
                            console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes-Debug: &aFound y entry for home: " + home + ". &aY entry: " + y));
                        }
                        z = fileReader.getDouble("homes2." + key + "." + home + "." + "z");
                        if (config.getBoolean("general.developer-debug-mode.enabled", false)||!usermapConfig.getBoolean("import-completed")){
                            console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes-Debug: &aFound z entry for home: " + home + ". &aZ entry: " + z));
                        }
                        yaw = (float) fileReader.getDouble("homes2." + key + "." + home + "." + "yaw");
                        if (config.getBoolean("general.developer-debug-mode.enabled", false)||!usermapConfig.getBoolean("import-completed")){
                            console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes-Debug: &aFound yaw entry for home: " + home + ". &aYaw entry: " + yaw));
                        }
                        pitch = (float) fileReader.getDouble("homes2." + key + "." + home + "." + "pitch");
                        if (config.getBoolean("general.developer-debug-mode.enabled", false)||!usermapConfig.getBoolean("import-completed")){
                            console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes-Debug: &aFound pitch entry for home: " + home + ". &aPitch entry: " + pitch));
                        }
                        World homeWorld = Bukkit.getWorld(world);
                        Location location = new Location(homeWorld, x, y, z, yaw, pitch);
                        homes.put(homeName, location);
                        if (config.getBoolean("general.developer-debug-mode.enabled", false)||!usermapConfig.getBoolean("import-completed")){
                            console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes-Debug: &aHome: " + home + " &ahas been successfully created for player: " + playerName));
                        }
                    });

                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(key));
                    if (usermapStorageUtil.isUserExisting(offlinePlayer)){
                        User user = usermapStorageUtil.getUserByOfflinePlayer(offlinePlayer);
                        HashMap<String, Location> usersCurrentHomes = user.getHomesList();
                        for (Map.Entry<String, Location> homesList : homes.entrySet()){
                            if (!usersCurrentHomes.containsKey(homesList.getKey())){
                                usersCurrentHomes.put(homesList.getKey(), homesList.getValue());
                                if (config.getBoolean("general.developer-debug-mode.enabled", false)||!usermapConfig.getBoolean("import-completed")){
                                    console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes-Debug: &aPlayer " + user.getLastKnownName() + " already exists in the usermap"));
                                    console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes-Debug: &aSuccessfully added new home: " + homeName + " to player."));
                                }
                            }else {
                                if (config.getBoolean("general.developer-debug-mode.enabled", false) || !usermapConfig.getBoolean("import-completed")) {
                                    console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes-Debug: &aPlayer " + user.getLastKnownName() + " already exists in the usermap"));
                                    console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes-Debug: &aThe current home iteration already exists! Skipping..."));
                                }
                            }
                        }
                        user.setHomesList(usersCurrentHomes);
                        usermapStorageUtil.getUsermapStorage().replace(UUID.fromString(key), user);
                    }else {
                      User user = new User(key, playerName);
                      user.setHomesList(homes);
                      usermapStorageUtil.getUsermapStorage().put(UUID.fromString(key), user);
                        if (config.getBoolean("general.developer-debug-mode.enabled", false)||!usermapConfig.getBoolean("import-completed")){
                            console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes-Debug: &aPlayer " + user.getLastKnownName() + " has never joined this server before!"));
                            console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes-Debug: &aThey have been added to the usermap and had their homes stored!"));
                        }
                    }
                });
                return true;
            }
        }catch (NullPointerException e) {
            console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes &cCould not load homes from StormerHomesReloaded plugin!"));
            console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes &cSee below for error message!"));
            e.printStackTrace();
        }
        return false;
    }
}
