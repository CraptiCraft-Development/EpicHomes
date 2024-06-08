package me.loving11ish.epichomes.listeners;

import me.loving11ish.epichomes.EpicHomes;
import me.loving11ish.epichomes.utils.MessageUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class PlayerDisconnectionEvent implements Listener {

    @EventHandler
    public void onPlayerDisconnect(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (EpicHomes.getPlugin().getTeleportationManager().getTeleportQueue().containsKey(uuid)) {
            if (!EpicHomes.getPlugin().getTeleportationManager().removeTimedTeleport(uuid)) {
                MessageUtils.sendConsole("error", "Failed to remove timed teleport for player " + player.getName() + "!");
            }
        }
    }
}
