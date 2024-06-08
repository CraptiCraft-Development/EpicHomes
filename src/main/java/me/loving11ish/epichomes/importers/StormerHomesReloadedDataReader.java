package me.loving11ish.epichomes.importers;

import me.loving11ish.epichomes.EpicHomes;
import me.loving11ish.epichomes.models.User;
import me.loving11ish.epichomes.utils.MessageUtils;
import me.loving11ish.epichomes.utils.UsermapStorageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StormerHomesReloadedDataReader {

    public boolean homesImportSuccessful = false;

    private final UsermapStorageUtil usermapStorageUtil = EpicHomes.getPlugin().getUsermapStorageUtil();

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
            MessageUtils.sendDebugConsole("&aFound StormerHomesReloaded config.yml and loaded successfully.");

            if (loadHomes()) {
                this.homesImportSuccessful = true;
            }
        } catch (IOException | InvalidConfigurationException | NullPointerException e) {
            MessageUtils.sendConsole("error", "&4&lCannot find StormerHomesReloaded data file!");
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
            if (fileReader.getConfigurationSection("homes2") != null) {
                MessageUtils.sendDebugConsole("&aFound homes2 section in StormerHomesReloaded config.yml");

                fileReader.getConfigurationSection("homes2").getKeys(false).forEach(key -> {
                    HashMap<String, Location> homes = new HashMap<>();
                    fileReader.getConfigurationSection("homes2." + key).getKeys(false).forEach(home -> {
                        homeName = home;
                        MessageUtils.sendDebugConsole("&aFound home name entry of: " + home + " for UUID: " + key);

                        world = fileReader.getString("homes2." + key + "." + home + "." + "world");
                        MessageUtils.sendDebugConsole("&aFound world entry for home: " + home + ". World entry: " + world);

                        playerName = fileReader.getString("homes2." + key + "." + home + "." + "playername");
                        MessageUtils.sendDebugConsole("&aFound playername entry for home: " + home + ". PlayerName entry: " + playerName);

                        x = fileReader.getDouble("homes2." + key + "." + home + "." + "x");
                        MessageUtils.sendDebugConsole("&aFound x entry for home: " + home + ". X entry: " + x);

                        y = fileReader.getDouble("homes2." + key + "." + home + "." + "y");
                        MessageUtils.sendDebugConsole("&aFound y entry for home: " + home + ". Y entry: " + y);

                        z = fileReader.getDouble("homes2." + key + "." + home + "." + "z");
                        MessageUtils.sendDebugConsole("&aFound z entry for home: " + home + ". Z entry: " + z);

                        yaw = (float) fileReader.getDouble("homes2." + key + "." + home + "." + "yaw");
                        MessageUtils.sendDebugConsole("&aFound yaw entry for home: " + home + ". Yaw entry: " + yaw);

                        pitch = (float) fileReader.getDouble("homes2." + key + "." + home + "." + "pitch");
                        MessageUtils.sendDebugConsole("&aFound pitch entry for home: " + home + ". Pitch entry: " + pitch);

                        World homeWorld = Bukkit.getWorld(world);
                        Location location = new Location(homeWorld, x, y, z, yaw, pitch);
                        homes.put(homeName, location);
                        MessageUtils.sendDebugConsole("&aHome: " + home + "&ahas been successfully created for player: " + playerName);
                    });

                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(key));

                    if (usermapStorageUtil.isUserExisting(offlinePlayer)) {
                        User user = usermapStorageUtil.getUserByOfflinePlayer(offlinePlayer);
                        HashMap<String, Location> usersCurrentHomes = user.getHomesList();

                        for (Map.Entry<String, Location> homesList : homes.entrySet()) {
                            if (!usersCurrentHomes.containsKey(homesList.getKey())) {

                                usersCurrentHomes.put(homesList.getKey(), homesList.getValue());
                                MessageUtils.sendDebugConsole("&aPlayer " + user.getLastKnownName() + " already exists in the usermap");
                                MessageUtils.sendDebugConsole("&aSuccessfully added new home: " + homeName + " to player.");
                            }

                            else {
                                MessageUtils.sendDebugConsole("&aPlayer " + user.getLastKnownName() + " already exists in the usermap");
                                MessageUtils.sendDebugConsole("&aThe current home iteration already exists! Skipping...");
                            }
                        }
                        user.setHomesList(usersCurrentHomes);
                        usermapStorageUtil.getUsermapStorage().replace(UUID.fromString(key), user);
                    }

                    else {
                        User user = new User(key, playerName);
                        user.setHomesList(homes);
                        usermapStorageUtil.getUsermapStorage().put(UUID.fromString(key), user);
                        MessageUtils.sendDebugConsole("&aPlayer " + user.getLastKnownName() + " has never joined this server before!");
                        MessageUtils.sendDebugConsole("&aThey have been added to the usermap and had their homes stored!");
                    }
                });
                return true;
            }
        } catch (NullPointerException e) {
            MessageUtils.sendConsole("error", "&cCould not load homes from StormerHomesReloaded plugin!");
            MessageUtils.sendConsole("error", "&cSee below for error message!");
            e.printStackTrace();
        }
        return false;
    }
}
