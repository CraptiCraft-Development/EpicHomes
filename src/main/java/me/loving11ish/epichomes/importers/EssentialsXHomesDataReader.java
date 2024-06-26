package me.loving11ish.epichomes.importers;

import me.loving11ish.epichomes.EpicHomes;
import me.loving11ish.epichomes.models.User;
import me.loving11ish.epichomes.utils.MessageUtils;
import me.loving11ish.epichomes.utils.UsermapStorageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class EssentialsXHomesDataReader {

    public boolean homesImportSuccessful = false;

    private final UsermapStorageUtil usermapStorageUtil = EpicHomes.getPlugin().getUsermapStorageUtil();

    private YamlConfiguration fileReader;

    private String world;
    private String homeName;
    private String playerName;
    private String uuid;
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;

    public EssentialsXHomesDataReader() {
        this.world = null;
        this.homeName = null;
        this.playerName = null;
        this.uuid = null;
        this.x = 0;
        this.y = 0;
        this.z = 0;
        this.yaw = 0f;
        this.pitch = 0f;
        try {
            fileReader = new YamlConfiguration();
            Plugin plugin = Bukkit.getPluginManager().getPlugin("Essentials");
            for (File file : new File(plugin.getDataFolder().getParentFile() + File.separator + "Essentials" + File.separator + "userdata").listFiles()) {
                fileReader.load(file);
                playerName = fileReader.getString("last-account-name");
                uuid = file.getName().replace(".yml", "");
                MessageUtils.sendDebugConsole("User UUID: " + uuid);
                getHomes();
            }
            this.homesImportSuccessful = true;
        } catch (IOException | InvalidConfigurationException | NullPointerException e) {
            MessageUtils.sendConsole("error", "Cannot find EssentialsX player data file!");
        }
    }

    private void getHomes() {
        try {
            if (fileReader.getConfigurationSection("homes") != null) {
                Set<String> names = fileReader.getConfigurationSection("homes").getKeys(false);

                for (String name : names) {
                    homeName = name;
                    MessageUtils.sendDebugConsole("&aFound home name entry of: " + name);

                    Set<String> data = fileReader.getConfigurationSection("homes." + name).getKeys(false);
                    for (String info : data) {
                        if (info.matches("world-name")) {
                            world = fileReader.get("homes." + name + "." + info).toString();
                            MessageUtils.sendDebugConsole("&aFound world entry for home: " + name + " world entry: " + world);

                        } else if (info.matches("x")) {
                            x = Double.parseDouble(fileReader.get("homes." + name + "." + info).toString());
                            MessageUtils.sendDebugConsole("&aFound x entry for home: " + name + " x entry: " + x);

                        } else if (info.matches("y")) {
                            y = Double.parseDouble(fileReader.get("homes." + name + "." + info).toString());
                            MessageUtils.sendDebugConsole("&aFound y entry for home: " + name + " y entry: " + y);

                        } else if (info.matches("z")) {
                            z = Double.parseDouble(fileReader.get("homes." + name + "." + info).toString());
                            MessageUtils.sendDebugConsole("&aFound z entry for home: " + name + " z entry: " + z);

                        } else if (info.matches("yaw")) {
                            yaw = Float.parseFloat(fileReader.get("homes." + name + "." + info).toString());
                            MessageUtils.sendDebugConsole("&aFound yaw entry for home: " + name + " yaw entry: " + yaw);

                        } else if (info.matches("pitch")) {
                            pitch = Float.parseFloat(fileReader.get("homes." + name + "." + info).toString());
                            MessageUtils.sendDebugConsole("&aFound pitch entry for home: " + name + " pitch entry: " + pitch);

                        }
                    }
                    UUID userUUID = UUID.fromString(uuid);
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(userUUID);

                    if (usermapStorageUtil.isUserExisting(offlinePlayer)) {
                        User user = usermapStorageUtil.getUserByOfflinePlayer(offlinePlayer);
                        List<String> userHomeNames = usermapStorageUtil.getHomeNamesListByUser(user);

                        if (!userHomeNames.contains(homeName)) {
                            Location location = new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
                            usermapStorageUtil.addHomeToUser(user, homeName, location);
                            MessageUtils.sendDebugConsole("&aPlayer " + user.getLastKnownName() + " already exists in the usermap");
                            MessageUtils.sendDebugConsole("&aSuccessfully added new home to player: " + user.getLastKnownName());
                        }

                        else {
                            MessageUtils.sendDebugConsole("&aPlayer " + user.getLastKnownName() + " already exists in the usermap");
                            MessageUtils.sendDebugConsole("&aThe home " + homeName + "&aalready exists! Skipping...");
                        }
                    }

                    else {
                        User user = new User(uuid, playerName);
                        usermapStorageUtil.getUsermapStorage().put(UUID.fromString(uuid), user);
                        Location location = new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);

                        usermapStorageUtil.addHomeToUser(user, homeName, location);
                        MessageUtils.sendDebugConsole("&aPlayer " + user.getLastKnownName() + " has never joined this server before!");
                        MessageUtils.sendDebugConsole("&aThey have been added to the usermap and had their homes stored!");
                    }
                }
            }
        } catch (NullPointerException e) {
            MessageUtils.sendConsole("error", "Could not load homes from EssentialsX plugin!");
            MessageUtils.sendConsole("error", "See below for error message!");
            e.printStackTrace();
        }
    }
}
