package me.loving11ish.epichomes.utils;

import com.tcoded.folialib.FoliaLib;
import com.tcoded.folialib.wrapper.WrappedTask;
import io.papermc.lib.PaperLib;
import me.loving11ish.epichomes.EpicHomes;
import me.loving11ish.epichomes.api.HomeTeleportEvent;
import me.loving11ish.epichomes.models.User;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class TeleportationUtils {

    Logger logger = EpicHomes.getPlugin().getLogger();

    private FoliaLib foliaLib = new FoliaLib(EpicHomes.getPlugin());

    public WrappedTask wrappedTask;

    FileConfiguration config = EpicHomes.getPlugin().getConfig();
    FileConfiguration messagesConfig = EpicHomes.getPlugin().messagesFileManager.getMessagesConfig();

    private static final String PREFIX_PLACEHOLDER = "%PREFIX%";
    private static final String HOME_NAME_PLACEHOLDER = "%HOME%";

    private String prefix = messagesConfig.getString("global-prefix");

    public void teleportPlayerAsync(Player player, Location location, String homeName) {
        User user = EpicHomes.getPlugin().usermapStorageUtil.getUserByOnlinePlayer(player);
        PaperLib.teleportAsync(player, location);
        fireHomeTeleportEvent(player, user, homeName, location);
        if (config.getBoolean("general.developer-debug-mode.enabled")){
            logger.info(ColorUtils.translateColorCodes("&6EpicHomes-Debug: &aFired HomeTeleportEvent"));
        }
        player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("non-timed-teleporting-complete")
                .replace(PREFIX_PLACEHOLDER, ColorUtils.translateColorCodes(prefix))
                .replace(HOME_NAME_PLACEHOLDER, homeName)));
    }

    public void teleportPlayerAsyncTimed(Player player, Location location, String homeName) {
        User user = EpicHomes.getPlugin().usermapStorageUtil.getUserByOnlinePlayer(player);
        player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("timed-teleporting-begin-tp")
                .replace(PREFIX_PLACEHOLDER, ColorUtils.translateColorCodes(prefix)).replace(HOME_NAME_PLACEHOLDER, homeName)));
        wrappedTask = foliaLib.getImpl().runTimerAsync(new Runnable() {
            int time = config.getInt("homes.teleportation.delay-before-teleport.time");
            @Override
            public void run() {
                if (!EpicHomes.getPlugin().teleportQueue.containsKey(player.getUniqueId())){
                    EpicHomes.getPlugin().teleportQueue.put(player.getUniqueId(), getWrappedTask());
                    if (config.getBoolean("general.developer-debug-mode.enabled")){
                        logger.info(ColorUtils.translateColorCodes("&6EpicHomes-Debug: &aPlayer "  + player.getName() + " has been added to teleport queue"));
                    }
                }
                if (time == 0) {
                    EpicHomes.getPlugin().teleportQueue.remove(player.getUniqueId());
                    if (config.getBoolean("general.developer-debug-mode.enabled")){
                        logger.info(ColorUtils.translateColorCodes("&6EpicHomes-Debug: &aPlayer "  + player.getName() + " has been removed from the teleport queue"));
                    }
                    PaperLib.teleportAsync(player, location);
                    fireHomeTeleportEvent(player, user, homeName, location);
                    if (config.getBoolean("general.developer-debug-mode.enabled")){
                        logger.info(ColorUtils.translateColorCodes("&6EpicHomes-Debug: &aFired HomeTeleportEvent"));
                    }
                    player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("timed-teleporting-complete")
                            .replace(PREFIX_PLACEHOLDER, ColorUtils.translateColorCodes(prefix))
                            .replace(HOME_NAME_PLACEHOLDER, homeName)));
                    getWrappedTask().cancel();
                    if (config.getBoolean("general.developer-debug-mode.enabled")){
                        logger.info(ColorUtils.translateColorCodes("&6EpicHomes-Debug: &aWrapped task: " + getWrappedTask().toString()));
                        logger.info(ColorUtils.translateColorCodes("&6EpicHomes-Debug: &ateleportPlayerAsyncTimed task canceled"));
                    }
                    return;
                }else {
                    time --;
                    if (config.getBoolean("general.developer-debug-mode.enabled")){
                        logger.info(ColorUtils.translateColorCodes("&6EpicHomes-Debug: &ateleportPlayerAsyncTimed task running"));
                        logger.info(ColorUtils.translateColorCodes("&6EpicHomes-Debug: &aWrapped task: " + getWrappedTask().toString()));
                    }
                }
            }
        }, 0, 1L, TimeUnit.SECONDS);
    }

    public WrappedTask getWrappedTask() {
        return wrappedTask;
    }

    private void fireHomeTeleportEvent(Player player, User user, String homeName, Location homeLocation) {
        HomeTeleportEvent homeTeleportEvent = new HomeTeleportEvent(player, user, homeName, homeLocation);
        Bukkit.getPluginManager().callEvent(homeTeleportEvent);
    }
}
