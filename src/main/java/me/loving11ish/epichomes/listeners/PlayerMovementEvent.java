package me.loving11ish.epichomes.listeners;

import com.tcoded.folialib.wrapper.task.WrappedTask;
import me.loving11ish.epichomes.EpicHomes;
import me.loving11ish.epichomes.utils.ColorUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.UUID;

public class PlayerMovementEvent implements Listener {

    private final ConsoleCommandSender console = Bukkit.getConsoleSender();

    private final FileConfiguration config = EpicHomes.getPlugin().getConfig();
    private final FileConfiguration messagesConfig = EpicHomes.getPlugin().messagesFileManager.getMessagesConfig();

    private static final String PREFIX_PLACEHOLDER = "%PREFIX%";
    private final String prefix = messagesConfig.getString("global-prefix", "&f[&6Epic&bHomes&f]&r");

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        if (!config.getBoolean("homes.teleportation.delay-before-teleport.cancel-teleport-on-move")){
            return;
        }
        if (player.hasPermission("epichomes.bypass.movement")||player.hasPermission("epichomes.bypass.*")||player.isOp()){
            return;
        }
        if (EpicHomes.getPlugin().teleportQueue.containsKey(uuid)){
            if (event.getFrom().getX() != event.getTo().getX()
                    &&event.getFrom().getY() != event.getTo().getY()
                    &&event.getFrom().getZ() != event.getTo().getZ()){
                if (config.getBoolean("general.developer-debug-mode.enabled", false)){
                    console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes-Debug: &aPlayer "  + player.getName() + " has a pending teleport"));
                }
                try {
                    WrappedTask wrappedTask = EpicHomes.getPlugin().teleportQueue.get(uuid);
                    if (config.getBoolean("general.developer-debug-mode.enabled", false)){
                        console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes-Debug: &aWrapped task: " + wrappedTask.toString()));
                    }
                    wrappedTask.cancel();
                    if (config.getBoolean("general.developer-debug-mode.enabled", false)){
                        console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes-Debug: &aWrapped task canceled"));
                    }
                    EpicHomes.getPlugin().teleportQueue.remove(uuid);
                    if (config.getBoolean("general.developer-debug-mode.enabled", false)){
                        console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes-Debug: &aPlayer " + player.getName() + " has had teleport canceled and removed from queue"));
                    }
                    player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("timed-teleport-failed-player-moved")
                            .replace(PREFIX_PLACEHOLDER, prefix)));

                }catch (Exception e){
                    console.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("move-event-cancel-failed")
                            .replace(PREFIX_PLACEHOLDER, prefix)));
                    e.printStackTrace();
                }
            }
        }
    }
}
