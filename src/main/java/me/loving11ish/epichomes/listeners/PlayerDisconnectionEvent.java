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
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class PlayerDisconnectionEvent implements Listener {

    ConsoleCommandSender console = Bukkit.getConsoleSender();

    FileConfiguration config = EpicHomes.getPlugin().getConfig();

    @EventHandler
    public void onPlayerDisconnect(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        if (EpicHomes.getPlugin().teleportQueue.containsKey(uuid)){
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
        }
    }
}
