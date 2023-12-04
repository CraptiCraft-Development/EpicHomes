package me.loving11ish.epichomes.utils;

import me.loving11ish.epichomes.EpicHomes;
import me.loving11ish.epichomes.models.User;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.*;

public class UsermapStorageUtil {

    private final ConsoleCommandSender console = Bukkit.getConsoleSender();
    private final FileConfiguration messagesConfig = EpicHomes.getPlugin().messagesFileManager.getMessagesConfig();
    private final FileConfiguration usermapConfig = EpicHomes.getPlugin().usermapFileManager.getUsermapConfig();

    private static final String PREFIX_PLACEHOLDER = "%PREFIX%";
    private static final String HOME_NAME_PLACEHOLDER = "%HOME%";

    private final String prefix = messagesConfig.getString("global-prefix", "&f[&6Epic&bHomes&f]&r");

    private final HashMap<UUID, User> usermapStorage = new HashMap<>();

    public void saveUsermap() throws IOException {
        for (Map.Entry<UUID, User> entry : usermapStorage.entrySet()) {
            HashMap<String, Location> homeLocations = entry.getValue().getHomesList();
            usermapConfig.set("users.data." + entry.getKey() + ".userUUID", entry.getValue().getUserUUID());
            usermapConfig.set("users.data." + entry.getKey() + ".lastKnownName", entry.getValue().getLastKnownName());
            if (!entry.getValue().getHomesList().isEmpty()){
                for (Map.Entry<String, Location> homeEntry : homeLocations.entrySet()) {
                    if (homeEntry.getValue().getWorld() == null){
                        console.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("usermap-file-save-failure")
                                .replace(PREFIX_PLACEHOLDER, prefix)
                                .replace("%PLAYER%", entry.getValue().getLastKnownName())));
                        continue;
                    }
                    usermapConfig.set("users.data." + entry.getKey() + ".homes." + homeEntry.getKey() + ".homeName", homeEntry.getKey());
                    usermapConfig.set("users.data." + entry.getKey() + ".homes." + homeEntry.getKey() + ".homeWorld", homeEntry.getValue().getWorld().getName());
                    usermapConfig.set("users.data." + entry.getKey() + ".homes." + homeEntry.getKey() + ".homeX", homeEntry.getValue().getX());
                    usermapConfig.set("users.data." + entry.getKey() + ".homes." + homeEntry.getKey() + ".homeY", homeEntry.getValue().getY());
                    usermapConfig.set("users.data." + entry.getKey() + ".homes." + homeEntry.getKey() + ".homeZ", homeEntry.getValue().getZ());
                    usermapConfig.set("users.data." + entry.getKey() + ".homes." + homeEntry.getKey() + ".homeYaw", homeEntry.getValue().getYaw());
                    usermapConfig.set("users.data." + entry.getKey() + ".homes." + homeEntry.getKey() + ".homePitch", homeEntry.getValue().getPitch());
                }
            }
        }
        EpicHomes.getPlugin().usermapFileManager.saveUsermapConfig();
    }

    public void loadUsermap() throws IOException {
        usermapStorage.clear();
        usermapConfig.getConfigurationSection("users.data").getKeys(false).forEach(key -> {
            HashMap<String, Location> homeLocations = new HashMap<>();
            UUID uuid = UUID.fromString(key);
            String userUUID = usermapConfig.getString("users.data." + key + ".userUUID");
            String lastKnownName = usermapConfig.getString("users.data." + key + ".lastKnownName");
            ConfigurationSection homesSection = usermapConfig.getConfigurationSection("users.data." + key + ".homes");
            if (homesSection != null){
                usermapConfig.getConfigurationSection("users.data." + key + ".homes").getKeys(false).forEach(homeKey -> {
                    String homeName = usermapConfig.getString("users.data." + key + ".homes." + homeKey + ".homeName");
                    String homeWorld = usermapConfig.getString("users.data." + key + ".homes." + homeKey + ".homeWorld");
                    double homeX = usermapConfig.getDouble("users.data." + key + ".homes." + homeKey + ".homeX");
                    double homeY = usermapConfig.getDouble("users.data." + key + ".homes." + homeKey + ".homeY");
                    double homeZ = usermapConfig.getDouble("users.data." + key + ".homes." + homeKey + ".homeZ");
                    float homeYaw = (float) usermapConfig.getDouble("users.data." + key + ".homes." + homeKey + ".homeYaw");
                    float homePitch = (float) usermapConfig.getDouble("users.data." + key + ".homes." + homeKey + ".homePitch");
                    World world = Bukkit.getWorld(homeWorld);
                    Location location = new Location(world, homeX, homeY, homeZ, homeYaw, homePitch);
                    homeLocations.put(homeName, location);
                });
            }
            User user = new User(userUUID, lastKnownName);
            if (!homeLocations.isEmpty()){
                user.setHomesList(homeLocations);
            }
            usermapStorage.put(uuid, user);
        });
    }

    public User addToUsermap(Player player) {
        UUID uuid = player.getUniqueId();
        String userUUID = uuid.toString();
        String lastKnownName = player.getName();
        if (!isUserExisting(player)){
            User user = new User(userUUID, lastKnownName);
            usermapStorage.put(uuid, user);
            return user;
        }
        return null;
    }

    public boolean isUserExisting(Player player) {
        UUID uuid = player.getUniqueId();
        return usermapStorage.containsKey(uuid);
    }

    public boolean isUserExisting(OfflinePlayer offlinePlayer) {
        UUID uuid = offlinePlayer.getUniqueId();
        return usermapStorage.containsKey(uuid);
    }

    public boolean hasNameChanged(Player player) {
        UUID uuid = player.getUniqueId();
        User user = usermapStorage.get(uuid);
        String lastKnownName = user.getLastKnownName();
        return !player.getName().equals(lastKnownName);
    }

    public void updatePlayerName(Player player) {
        UUID uuid = player.getUniqueId();
        User user = usermapStorage.get(uuid);
        String newPlayerName = player.getName();
        user.setLastKnownName(newPlayerName);
    }

    public User getUserByOnlinePlayer(Player player) {
        UUID uuid = player.getUniqueId();
        if (isUserExisting(player)){
            return usermapStorage.get(uuid);
        }
        return null;
    }

    public User getUserByOfflinePlayer(OfflinePlayer offlinePlayer) {
        UUID uuid = offlinePlayer.getUniqueId();
        if (isUserExisting(offlinePlayer)){
            return usermapStorage.get(uuid);
        }
        return null;
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
        for (User user : usermapStorage.values()){
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
                player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("home-set-failed-home-exists")
                        .replace(PREFIX_PLACEHOLDER, ColorUtils.translateColorCodes(prefix))
                        .replace(HOME_NAME_PLACEHOLDER, ColorUtils.translateColorCodes(homeName))));
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
                usermapStorage.replace(UUID.fromString(user.getUserUUID()), user);
                usermapConfig.set("users.data." + key + ".homes." + homeName, null);
                EpicHomes.getPlugin().usermapFileManager.saveUsermapConfig();
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
        User user = usermapStorage.get(uuid);
        return user.getHomesList().entrySet();
    }

    public Set<Map.Entry<String, Location>> getHomeLocationsListByOfflinePlayer(OfflinePlayer offlinePlayer) {
        UUID uuid = offlinePlayer.getUniqueId();
        User user = usermapStorage.get(uuid);
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
        usermapConfig.set("import-completed", true);
        EpicHomes.getPlugin().usermapFileManager.saveUsermapConfig();
    }

    public boolean getHomeImportCompleted() {
        return usermapConfig.getBoolean("import-completed");
    }

    public Set<UUID> getRawUsermapList() {
        return usermapStorage.keySet();
    }

    public HashMap<UUID, User> getUsermapStorage() {
        return usermapStorage;
    }
}
