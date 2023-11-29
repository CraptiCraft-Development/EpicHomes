package me.loving11ish.epichomes.api;

import com.tcoded.folialib.FoliaLib;
import com.tcoded.folialib.wrapper.task.WrappedTask;
import me.loving11ish.epichomes.EpicHomes;
import me.loving11ish.epichomes.api.events.HomePreTeleportEvent;
import me.loving11ish.epichomes.models.User;
import me.loving11ish.epichomes.utils.ColorUtils;
import me.loving11ish.epichomes.utils.TeleportationUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class EpicHomesAPI {

    private static final ConsoleCommandSender console = Bukkit.getConsoleSender();

    private static final FileConfiguration config = EpicHomes.getPlugin().getConfig();

    /**
     *
     * @return Returns the EpicHomes main plugin instance.
     */
    public static EpicHomes getPluginInstance() {
        return EpicHomes.getPlugin();
    }

    /**
     *
     * @return Returns a string of text that contains the full server package.
     */
    public static String getServerPackage() {
        return EpicHomes.getVersionCheckerUtils().getServerPackage();
    }

    /**
     *
     * @return Returns an integer that is the base major server version.
     */
    public static int getMajorServerVersion() {
        return EpicHomes.getVersionCheckerUtils().getVersion();
    }

    /**
     *
     * @return Returns `true` if the server or network is able to connect to the Mojang auth servers. Otherwise, returns `false`.
     *
     */
    public static boolean isServerRunningOnline() {
        return EpicHomes.isOnlineMode();
    }

    /**
     *
     * @return Returns the defined prefix for EpicHomes or the default if not user configured.
     */
    public static String getPluginPrefix() {
        return EpicHomes.getPlugin().messagesFileManager.getMessagesConfig().getString("global-prefix", "&f[&6Epic&bHomes&f]&r");
    }

    /**
     *
     * @return Returns the FoliaLib instance for Entity Aliases.
     */
    public static FoliaLib getFoliaLibInstance() {
        return EpicHomes.getFoliaLib();
    }

    /**
     *
     * @return Returns `true` if your current EpicHomes plugin version does NOT mach the latest version listed on SpigotMC.
     */
    public static boolean isEpicHomesPluginUpdateAvailable() {
        return EpicHomes.getPlugin().isUpdateAvailable();
    }

    /**
     *
     * @return Returns a HashMap of all stored Users saved by EpicHomes.
     */
    public static HashMap<UUID, User> getAllStoredUsers() {
        return EpicHomes.getPlugin().usermapStorageUtil.getUsermapStorage();
    }

    /**
     *
     * @param player The Bukkit Player object to get a user from.
     * @return Returns a User object from the provided player, or null if the User cannot be found.
     */
    public static User getUserByBukkitPlayer(Player player) {
        return EpicHomes.getPlugin().usermapStorageUtil.getUserByOnlinePlayer(player);
    }

    /**
     *
     * @param offlinePlayer The Bukkit OfflinePlayer object to get a user from.
     * @return Returns a User object from the provided offline player, or null if the User cannot be found.
     */
    public static User getUserByBukkitOfflinePlayer(OfflinePlayer offlinePlayer) {
        return EpicHomes.getPlugin().usermapStorageUtil.getUserByOfflinePlayer(offlinePlayer);
    }

    /**
     *
     * @param lastKnownPlayerName The String value of the last known name for the Offline Player to get.
     * @return Returns a Bukkit OfflinePlayer object or may throw an error if an OfflinePlayer cannot be found.
     */
    public static OfflinePlayer getBukkitOfflinePlayerFromLastKnownPlayerName(String lastKnownPlayerName) {
        return EpicHomes.getPlugin().usermapStorageUtil.getBukkitOfflinePlayerByLastKnownName(lastKnownPlayerName);
    }

    /**
     *
     * @param user The EpicHomes User object to get the home list from.
     * @return Returns a list of the provided Users home names, or an empty list if none are found.
     */
    public static List<String> getHomeNamesListByUser(User user) {
        return EpicHomes.getPlugin().usermapStorageUtil.getHomeNamesListByUser(user);
    }

    /**
     *
     * @param user The EpicHomes User object to get the home location from.
     * @param homeName The String name of the home to get.
     * @return Returns a Bukkit Location object for the stored player home, or null if none found.
     */
    public static Location getPlayerHomeLocationByHomeName(User user, String homeName) {
        return EpicHomes.getPlugin().usermapStorageUtil.getHomeLocationByHomeName(user, homeName);
    }

    /**
     *
     * @param uuid The UUID object of the player to get.
     * @return Returns a WrappedTask object of the players pending teleport, or null if none found.
     */
    public static WrappedTask getPlayerPendingTeleportTask(UUID uuid) {
        return EpicHomes.getPlugin().teleportQueue.get(uuid);
    }

    /**
     *
     * @param player The Bukkit Player object to teleport.
     * @param homeLocation The Bukkit Location object for the home destination.
     * @param homeName The string name of the players home destination.
     * @implNote This method DOES NOT apply the time before teleport system
     *           before the player is teleported to the provided home destination.
     */
    public static void teleportPlayerToHomeLocation(Player player, Location homeLocation, String homeName) {
        User user = EpicHomes.getPlugin().usermapStorageUtil.getUserByOnlinePlayer(player);
        TeleportationUtils teleportationUtils = new TeleportationUtils();
        fireHomePreTeleportEvent(player, user, homeName, homeLocation, player.getLocation());
        if (config.getBoolean("general.developer-debug-mode.enabled", false)){
            console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes-Debug: &aFired HomePreTeleportEvent"));
        }
        teleportationUtils.teleportPlayerAsync(player, homeLocation, homeName);
    }

    /**
     *
     * @param player The Bukkit Player object to teleport.
     * @param homeLocation The Bukkit Location object for the home destination.
     * @param homeName The string name of the players home destination.
     * @implNote This method applies the configured time before teleport system
     *           before the player is teleported to the provided home destination.
     */
    public static void teleportPlayerToHomeLocationTimed(Player player, Location homeLocation, String homeName) {
        User user = EpicHomes.getPlugin().usermapStorageUtil.getUserByOnlinePlayer(player);
        TeleportationUtils teleportationUtils = new TeleportationUtils();
        fireHomePreTeleportEvent(player, user, homeName, homeLocation, player.getLocation());
        if (config.getBoolean("general.developer-debug-mode.enabled", false)){
            console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes-Debug: &aFired HomePreTeleportEvent"));
        }
        teleportationUtils.teleportPlayerAsyncTimed(player, homeLocation, homeName);
    }

    /**
     *
     * @param user The EpicHomes User object to add the home too.
     * @param homeName The String of the home name to be created.
     * @param homeLocation The Bukkit Location for the home to be created.
     * @return Returns `true` if the home was created successfully or `false` if the creation failed.
     */
    public static boolean addNewHomeToUser(User user, String homeName, Location homeLocation) {
        return EpicHomes.getPlugin().usermapStorageUtil.addHomeToUser(user, homeName, homeLocation);
    }

    /**
     *
     * @param user The EpicHomes User object to remove the home from.
     * @param homeName The String of the home name to be removed.
     * @return Returns `true` if the home was removed successfully or `false` if the deletion failed.
     * @throws IOException If an error occurs when updating the usermap.yml file.
     */
    public static boolean removeHomeFromUser(User user, String homeName) throws IOException {
        return EpicHomes.getPlugin().usermapStorageUtil.removeHomeFromUser(user,homeName);
    }

    private static void fireHomePreTeleportEvent(Player player, User user, String homeName, Location homeLocation, Location oldLocation) {
        HomePreTeleportEvent homePreTeleportEvent = new HomePreTeleportEvent(player, user, homeName, homeLocation, oldLocation);
        Bukkit.getPluginManager().callEvent(homePreTeleportEvent);
    }
}
