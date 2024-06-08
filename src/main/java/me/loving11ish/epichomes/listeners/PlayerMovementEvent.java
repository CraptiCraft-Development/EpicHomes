package me.loving11ish.epichomes.listeners;

import me.loving11ish.epichomes.EpicHomes;
import me.loving11ish.epichomes.utils.MessageUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.UUID;

public class PlayerMovementEvent implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {

        if (!EpicHomes.getPlugin().getConfigManager().isUseMovementTPCancel()) {
            return;
        }

        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (EpicHomes.getPlugin().getTeleportationManager().getTeleportQueue().containsKey(uuid)) {

            if (player.hasPermission("epichomes.bypass.movement") || player.hasPermission("epichomes.bypass.*") || player.isOp()) {
                MessageUtils.sendDebugConsole("&aPlayer " + player.getName() + " has bypass permission for movement");
                return;
            }

            if (event.getFrom().getX() != event.getTo().getX()
                    && event.getFrom().getY() != event.getTo().getY()
                    && event.getFrom().getZ() != event.getTo().getZ()) {
                MessageUtils.sendDebugConsole("&aPlayer " + player.getName() + " has a pending teleport");

                try {
                    if (EpicHomes.getPlugin().getTeleportationManager().removeTimedTeleport(uuid)) {
                        MessageUtils.sendPlayer(player, EpicHomes.getPlugin().getMessagesManager().getTimedCancelTP());
                    }

                    else {
                        MessageUtils.sendConsole("error", EpicHomes.getPlugin().getMessagesManager().getMoveEventCancelFailed());
                    }

                } catch (Exception e) {
                    MessageUtils.sendConsole("error", EpicHomes.getPlugin().getMessagesManager().getMoveEventCancelFailed());
                    e.printStackTrace();
                }
            }
        }
    }
}
