package me.loving11ish.epichomes.utils;

import me.loving11ish.epichomes.EpicHomes;
import me.loving11ish.epichomes.models.User;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.*;

public class UsermapStorageUtil {

    private final FileConfiguration usermapConfig = EpicHomes.getPlugin().getUsermapFileManager().getUsermapConfig();

    private final HashMap<UUID, User> usermapStorage = new HashMap<>();

    private static final String HOME_NAME_PLACEHOLDER = "%HOME%";

    public void saveUsermap() throws IOException {
        for (Map.Entry<UUID, User> entry : this.usermapStorage.entrySet()) {
            HashMap<String, Location> homeLocations = entry.getValue().getHomesList();
            this.usermapConfig.set("users.data." + entry.getKey() + ".userUUID", entry.getValue().getUserUUID());
            this.usermapConfig.set("users.data." + entry.getKey() + ".lastKnownName", entry.getValue().getLastKnownName());
            if (!entry.getValue().getHomesList().isEmpty()){
                for (Map.Entry<String, Location> homeEntry : homeLocations.entrySet()) {
                    if (homeEntry.getValue().getWorld() == null){
                        MessageUtils.sendConsole("warning", EpicHomes.getPlugin().getMessagesManager().getUserMapSaveFailed()
                                .replace("%PLAYER%", entry.getValue().getLastKnownName()));
                        continue;
                    }
                    this.usermapConfig.set("users.data." + entry.getKey() + ".homes." + homeEntry.getKey() + ".homeName", homeEntry.getKey());
                    this.usermapConfig.set("users.data." + entry.getKey() + ".homes." + homeEntry.getKey() + ".homeWorld", homeEntry.getValue().getWorld().getName());
                    this.usermapConfig.set("users.data." + entry.getKey() + ".homes." + homeEntry.getKey() + ".homeX", homeEntry.getValue().getX());
                    this.usermapConfig.set("users.data." + entry.getKey() + ".homes." + homeEntry.getKey() + ".homeY", homeEntry.getValue().getY());
                    this.usermapConfig.set("users.data." + entry.getKey() + ".homes." + homeEntry.getKey() + ".homeZ", homeEntry.getValue().getZ());
                    this.usermapConfig.set("users.data." + entry.getKey() + ".homes." + homeEntry.getKey() + ".homeYaw", homeEntry.getValue().getYaw());
                    this.usermapConfig.set("users.data." + entry.getKey() + ".homes." + homeEntry.getKey() + ".homePitch", homeEntry.getValue().getPitch());
                }
            }
        }
        EpicHomes.getPlugin().getUsermapFileManager().saveUsermapConfig();
    }

    public void loadUsermap() throws IOException {
        this.usermapStorage.clear();
        this.usermapConfig.getConfigurationSection("users.data").getKeys(false).forEach(key -> {
            HashMap<String, Location> homeLocations = new HashMap<>();
            UUID uuid = UUID.fromString(key);
            String userUUID = this.usermapConfig.getString("users.data." + key + ".userUUID");
            String lastKnownName = this.usermapConfig.getString("users.data." + key + ".lastKnownName");
            ConfigurationSection homesSection = this.usermapConfig.getConfigurationSection("users.data." + key + ".homes");
            if (homesSection != null){
                this.usermapConfig.getConfigurationSection("users.data." + key + ".homes").getKeys(false).forEach(homeKey -> {
                    String homeName = this.usermapConfig.getString("users.data." + key + ".homes." + homeKey + ".homeName");
                    String homeWorld = this.usermapConfig.getString("users.data." + key + ".homes." + homeKey + ".homeWorld");
                    double homeX = this.usermapConfig.getDouble("users.data." + key + ".homes." + homeKey + ".homeX");
                    double homeY = this.usermapConfig.getDouble("users.data." + key + ".homes." + homeKey + ".homeY");
                    double homeZ = this.usermapConfig.getDouble("users.data." + key + ".homes." + homeKey + ".homeZ");
                    float homeYaw = (float) this.usermapConfig.getDouble("users.data." + key + ".homes." + homeKey + ".homeYaw");
                    float homePitch = (float) this.usermapConfig.getDouble("users.data." + key + ".homes." + homeKey + ".homePitch");
                    World world = Bukkit.getWorld(homeWorld);
                    Location location = new Location(world, homeX, homeY, homeZ, homeYaw, homePitch);
                    homeLocations.put(homeName, location);
                });
            }
            User user = new User(userUUID, lastKnownName);
            if (!homeLocations.isEmpty()){
                user.setHomesList(homeLocations);
            }
            this.usermapStorage.put(uuid, user);
        });
    }

    public User addToUsermap(Player player) {
        UUID uuid = player.getUniqueId();
        String userUUID = uuid.toString();
        String lastKnownName = player.getName();
        if (!isUserExisting(player)){
            User user = new User(userUUID, lastKnownName);
            this.usermapStorage.put(uuid, user);
            return user;
        }
        return null;
    }

    public boolean isUserExisting(Player player) {
        UUID uuid = player.getUniqueId();
        return this.usermapStorage.containsKey(uuid);
    }

    public boolean isUserExisting(OfflinePlayer offlinePlayer) {
        UUID uuid = offlinePlayer.getUniqueId();
        return this.usermapStorage.containsKey(uuid);
    }

    public boolean hasNameChanged(Player player) {
        UUID uuid = player.getUniqueId();
        User user = this.usermapStorage.get(uuid);
        String lastKnownName = user.getLastKnownName();
        return !player.getName().equals(lastKnownName);
    }

    public void updatePlayerName(Player player) {
        UUID uuid = player.getUniqueId();
        User user = this.usermapStorage.get(uuid);
        String newPlayerName = player.getName();
        user.setLastKnownName(newPlayerName);
    }

    public User getUserByOnlinePlayer(Player player) {
        UUID uuid = player.getUniqueId();
        if (isUserExisting(player)){
            return this.usermapStorage.get(uuid);
        }
        return null;
    }

    public User getUserByOfflinePlayer(OfflinePlayer offlinePlayer) {
        UUID uuid = offlinePlayer.getUniqueId();
        if (isUserExisting(offlinePlayer)){
            return this.usermapStorage.get(uuid);
        }
        return null;
    }

    public User getUserByUUID(UUID uuid) {
        return this.usermapStorage.get(uuid);
    }

    public Player getBukkitPlayerByUser(User user) {
        UUID uuid = UUID.fromString(user.getUserUUID());
        return Bukkit.getPlayer(uuid);
    }

    public OfflinePlayer getBukkitOfflinePlayerByUser(User user) {
        UUID uuid = UUID.fromString(user.getUserUUID());
        return Bukkit.getOfflinePlayer(uuid);
    }

    public User getUserByLastKnownName(String lastKnownName) {
        for (User user : this.usermapStorage.values()){
            if (user.getLastKnownName().equalsIgnoreCase(lastKnownName)){
                return user;
            }
        }
        return null;
    }

    public OfflinePlayer getBukkitOfflinePlayerByLastKnownName(String lastKnownName) {
        User user = getUserByLastKnownName(lastKnownName);
        UUID uuid = UUID.fromString(user.getUserUUID());
        return Bukkit.getOfflinePlayer(uuid);
    }

    public boolean addHomeToUser(User user, String homeName, Location location) {
        HashMap<String, Location> userHomesList = user.getHomesList();
        for (Map.Entry<String, Location> home: userHomesList.entrySet()){
            if (home.getKey().equalsIgnoreCase(homeName)){
                Player player = getBukkitPlayerByUser(user);
                MessageUtils.sendPlayer(player, EpicHomes.getPlugin().getMessagesManager().getHomeSetFailAlreadyExists()
                        .replace(HOME_NAME_PLACEHOLDER, ColorUtils.translateColorCodes(homeName)));
                return false;
            }
        }
        userHomesList.put(homeName, location);
        return true;
    }

    public boolean removeHomeFromUser(User user, String homeName) throws IOException {
        String key = user.getUserUUID();
        HashMap<String, Location> userHomesList = user.getHomesList();
        for (Map.Entry<String, Location> home: userHomesList.entrySet()){
            if (home.getKey().equalsIgnoreCase(homeName)){
                userHomesList.remove(homeName);
                user.setHomesList(userHomesList);
                this.usermapStorage.replace(UUID.fromString(user.getUserUUID()), user);
                this.usermapConfig.set("users.data." + key + ".homes." + homeName, null);
                EpicHomes.getPlugin().getUsermapFileManager().saveUsermapConfig();
                return true;
            }
        }
        return false;
    }

    public Set<Map.Entry<String, Location>> getHomeLocationsListByUser(User user) {
        return user.getHomesList().entrySet();
    }

    public Location getHomeLocationByHomeName(User user, String homeName) {
        HashMap<String, Location> userHomesList = user.getHomesList();
        for (Map.Entry<String, Location> home: userHomesList.entrySet()){
            if (home.getKey().equalsIgnoreCase(homeName)){
                return home.getValue();
            }
        }
        return null;
    }

    public Set<Map.Entry<String, Location>> getHomeLocationsListByPlayer(Player player) {
        UUID uuid = player.getUniqueId();
        User user = this.usermapStorage.get(uuid);
        return user.getHomesList().entrySet();
    }

    public Set<Map.Entry<String, Location>> getHomeLocationsListByOfflinePlayer(OfflinePlayer offlinePlayer) {
        UUID uuid = offlinePlayer.getUniqueId();
        User user = this.usermapStorage.get(uuid);
        return user.getHomesList().entrySet();
    }

    public List<String> getHomeNamesListByUser(User user) {
        HashMap<String, Location> userHomesList = user.getHomesList();
        List<String> homeNames = new ArrayList<>();
        for (Map.Entry<String, Location> home: userHomesList.entrySet()){
            homeNames.add(home.getKey());
        }
        return homeNames;
    }

    public void setHomeImportCompleted() {
        this.usermapConfig.set("import-completed", true);
        EpicHomes.getPlugin().getUsermapFileManager().saveUsermapConfig();
    }

    public boolean getHomeImportCompleted() {
        return this.usermapConfig.getBoolean("import-completed");
    }

    public Set<UUID> getRawUsermapList() {
        return this.usermapStorage.keySet();
    }

    public HashMap<UUID, User> getUsermapStorage() {
        return this.usermapStorage;
    }
}
