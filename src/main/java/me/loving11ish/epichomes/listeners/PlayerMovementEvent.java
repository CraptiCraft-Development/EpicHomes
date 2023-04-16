package me.loving11ish.epichomes.listeners;

import com.tcoded.folialib.wrapper.WrappedTask;
import me.loving11ish.epichomes.EpicHomes;
import me.loving11ish.epichomes.utils.ColorUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.UUID;
import java.util.logging.Logger;

public class PlayerMovementEvent implements Listener {

    Logger logger = EpicHomes.getPlugin().getLogger();

    FileConfiguration config = EpicHomes.getPlugin().getConfig();
    FileConfiguration messagesConfig = EpicHomes.getPlugin().messagesFileManager.getMessagesConfig();

    private static final String PREFIX_PLACEHOLDER = "%PREFIX%";

    private String prefix = messagesConfig.getString("global-prefix");

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        if (!config.getBoolean("homes.teleportation.delay-before-teleport.cancel-teleport-on-move")){
            return;
        }
        if (EpicHomes.getPlugin().teleportQueue.containsKey(uuid)){
            if (config.getBoolean("general.developer-debug-mode.enabled")){
                logger.info(ColorUtils.translateColorCodes("&6EpicHomes-Debug: &aPlayer "  + player.getName() + " has a pending teleport"));
            }
            try {
                WrappedTask wrappedTask = EpicHomes.getPlugin().teleportQueue.get(uuid);
                if (config.getBoolean("general.developer-debug-mode.enabled")){
                    logger.info(ColorUtils.translateColorCodes("&6EpicHomes-Debug: &aWrapped task: " + wrappedTask.toString()));
                }
                wrappedTask.cancel();
                if (config.getBoolean("general.developer-debug-mode.enabled")){
                    logger.info(ColorUtils.translateColorCodes("&6EpicHomes-Debug: &aWrapped task canceled"));
                }
                EpicHomes.getPlugin().teleportQueue.remove(uuid);
                if (config.getBoolean("general.developer-debug-mode.enabled")){
                    logger.info(ColorUtils.translateColorCodes("&6EpicHomes-Debug: &aPlayer " + player.getName() + " has had teleport canceled and removed from queue"));
                }
                player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("timed-teleport-failed-player-moved")
                        .replace(PREFIX_PLACEHOLDER, prefix)));

            }catch (Exception e){
                logger.severe(ColorUtils.translateColorCodes(messagesConfig.getString("move-event-cancel-failed")
                        .replace(PREFIX_PLACEHOLDER, prefix)));
                e.printStackTrace();
            }
        }
    }
}
