package me.loving11ish.epichomes.updatesystem;

import me.loving11ish.epichomes.EpicHomes;
import me.loving11ish.epichomes.utils.MessageUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinEvent implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("epichomes.update") || player.hasPermission("epichomes.*") || player.isOp()) {
            if (EpicHomes.getPlugin().getConfigManager().isUpdateNotifications()) {
                new UpdateChecker(109590).getVersion(version -> {
                    try {
                        if (!(EpicHomes.getPlugin().getDescription().getVersion().equalsIgnoreCase(version))) {
                            MessageUtils.sendPlayer(player, EpicHomes.getPlugin().getMessagesManager().getUpdateCheckerUpdateAvailableOne());
                            MessageUtils.sendPlayer(player, EpicHomes.getPlugin().getMessagesManager().getUpdateCheckerUpdateAvailableTwo());
                            MessageUtils.sendPlayer(player, EpicHomes.getPlugin().getMessagesManager().getUpdateCheckerUpdateAvailableThree());
                        }
                    } catch (NullPointerException e) {
                        MessageUtils.sendPlayer(player, EpicHomes.getPlugin().getMessagesManager().getUpdateCheckerFailed());
                    }
                });
            }
        }
    }
}
