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
import java.util.concurrent.CompletableFuture;

public class EssentialsXHomesDataReader {

    public boolean homesImportSuccessful = false;

    private final UsermapStorageUtil usermapStorageUtil = EpicHomes.getPlugin().getUsermapStorageUtil();

    private YamlConfiguration fileReader;

    private String playerName;
    private String uuid;

    public EssentialsXHomesDataReader() {
        this.playerName = null;
        this.uuid = null;

        // Run the import process asynchronously
        CompletableFuture.runAsync(() -> {
            try {
                fileReader = new YamlConfiguration();
                Plugin plugin = Bukkit.getPluginManager().getPlugin("Essentials");

                File[] files = new File(plugin.getDataFolder().getParentFile() + File.separator + "Essentials" + File.separator + "userdata").listFiles();

                if (files != null) {
                    for (File file : files) {
                        fileReader.load(file);
                        playerName = fileReader.getString("last-account-name");
                        uuid = file.getName().replace(".yml", "");
                        MessageUtils.sendDebugConsole("User UUID: " + uuid);
                        getHomes();
                    }
                }
                homesImportSuccessful = true;
            } catch (IOException | InvalidConfigurationException | NullPointerException e) {
                MessageUtils.sendConsole("error", "Cannot find EssentialsX player data file!");
            }
        });
    }

    private void getHomes() {
        try {
            if (fileReader.getConfigurationSection("homes") != null) {
                Set<String> names = fileReader.getConfigurationSection("homes").getKeys(false);

                for (String name : names) {
                    MessageUtils.sendDebugConsole("&aFound home name entry of: " + name);

                    Set<String> data = fileReader.getConfigurationSection("homes." + name).getKeys(false);
                    String world = null;
                    double x = 0, y = 0, z = 0;
                    float yaw = 0f, pitch = 0f;

                    for (String info : data) {
                        if (info == null) {
                            MessageUtils.sendDebugConsole("&cNo data found for home: " + name + "! Skipping...");
                            continue;
                        }

                        switch (info) {
                            case "world":
                                world = fileReader.get("homes." + name + "." + info).toString();
                                MessageUtils.sendDebugConsole("&aWorld UUID: " + fileReader.getString("homes." + name + "." + info));
                                MessageUtils.sendDebugConsole("&aFound world entry for home: " + name + " world entry: " + world);
                                break;
                            case "x":
                                x = Double.parseDouble(fileReader.get("homes." + name + "." + info).toString());
                                MessageUtils.sendDebugConsole("&aFound x entry for home: " + name + " x entry: " + x);
                                break;
                            case "y":
                                y = Double.parseDouble(fileReader.get("homes." + name + "." + info).toString());
                                MessageUtils.sendDebugConsole("&aFound y entry for home: " + name + " y entry: " + y);
                                break;
                            case "z":
                                z = Double.parseDouble(fileReader.get("homes." + name + "." + info).toString());
                                MessageUtils.sendDebugConsole("&aFound z entry for home: " + name + " z entry: " + z);
                                break;
                            case "yaw":
                                yaw = Float.parseFloat(fileReader.get("homes." + name + "." + info).toString());
                                MessageUtils.sendDebugConsole("&aFound yaw entry for home: " + name + " yaw entry: " + yaw);
                                break;
                            case "pitch":
                                pitch = Float.parseFloat(fileReader.get("homes." + name + "." + info).toString());
                                MessageUtils.sendDebugConsole("&aFound pitch entry for home: " + name + " pitch entry: " + pitch);
                                break;
                        }
                    }

                    String finalWorld = world;
                    double finalX = x, finalY = y, finalZ = z;
                    float finalYaw = yaw, finalPitch = pitch;
                    String finalUuid = uuid;
                    String finalPlayerName = playerName;

                    EpicHomes.getFoliaLib().getScheduler().runNextTick((task) -> {
                        UUID userUUID = UUID.fromString(finalUuid);
                        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(userUUID);

                        if (usermapStorageUtil.isUserExisting(offlinePlayer)) {
                            User user = usermapStorageUtil.getUserByOfflinePlayer(offlinePlayer);
                            List<String> userHomeNames = usermapStorageUtil.getHomeNamesListByUser(user);

                            if (!userHomeNames.contains(name)) {
                                if (finalWorld == null || Bukkit.getWorld(UUID.fromString(finalWorld)) == null) {
                                    MessageUtils.sendDebugConsole("&cWorld UUID: " + finalWorld);
                                    MessageUtils.sendDebugConsole("&cWorld does not exist for " + name + "! Skipping...");
                                } else {
                                    Location location = new Location(Bukkit.getWorld(UUID.fromString(finalWorld)), finalX, finalY, finalZ, finalYaw, finalPitch);
                                    usermapStorageUtil.addHomeToUser(user, name, location);
                                    MessageUtils.sendDebugConsole("&aPlayer " + user.getLastKnownName() + " already exists in the usermap");
                                    MessageUtils.sendDebugConsole("&aSuccessfully added new home &e" + name + " &ato player: &d" + user.getLastKnownName());
                                }
                            } else {
                                MessageUtils.sendDebugConsole("&aPlayer " + user.getLastKnownName() + " already exists in the usermap");
                                MessageUtils.sendDebugConsole("&aThe home " + name + " already exists! Skipping...");
                            }

                        } else {
                            User user = new User(finalUuid, finalPlayerName);
                            usermapStorageUtil.getUsermapStorage().put(UUID.fromString(finalUuid), user);

                            if (finalWorld == null || Bukkit.getWorld(UUID.fromString(finalWorld)) == null) {
                                MessageUtils.sendDebugConsole("&cWorld UUID: " + finalWorld);
                                MessageUtils.sendDebugConsole("&cWorld does not exist for " + name + "! Skipping...");
                            } else {
                                Location location = new Location(Bukkit.getWorld(UUID.fromString(finalWorld)), finalX, finalY, finalZ, finalYaw, finalPitch);
                                usermapStorageUtil.addHomeToUser(user, name, location);
                                MessageUtils.sendDebugConsole("&aPlayer &d" + user.getLastKnownName() + " &ahas never joined this server before!");
                                MessageUtils.sendDebugConsole("&aThey have been added to the usermap.");
                                MessageUtils.sendDebugConsole("&aSuccessfully added new home &e" + name + " &ato player: &d" + user.getLastKnownName());
                            }
                        }
                    });
                }
            }
        } catch (NullPointerException e) {
            MessageUtils.sendConsole("error", "Could not load homes from EssentialsX plugin!");
            MessageUtils.sendConsole("error", "See below for error message!");
            e.printStackTrace();
        }
    }
}
