package me.loving11ish.epichomes.listeners;

import me.loving11ish.epichomes.EpicHomes;
import me.loving11ish.epichomes.utils.MessageUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PlayerCommandSendEvent implements Listener {

    private static final String TIME_PLACEHOLDER = "%TIMELEFT%";

    private final HashMap<UUID, Long> coolDown = new HashMap<>();
    private final List<String> pluginCommands;

    public PlayerCommandSendEvent(List<String> pluginCommands) {
        this.pluginCommands = pluginCommands;
    }

    @EventHandler
    public void onCommandSend(PlayerCommandPreprocessEvent event) {
        if (!EpicHomes.getPlugin().getConfigManager().isUseCommandCooldown()) {
            return;
        }

        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (player.hasPermission("epichomes.bypass.cooldown") || player.hasPermission("epichomes.bypass.*")
                || player.hasPermission("epichomes.*") || player.isOp()) {
            return;
        }

        String[] message = event.getMessage().split(" ");
        String command = message[0];
        for (String string : pluginCommands) {

            if (command.equalsIgnoreCase(string)) {
                if (coolDown.containsKey(uuid)) {

                    if (coolDown.get(uuid) > System.currentTimeMillis()) {
                        long timeLeft = (coolDown.get(uuid) - System.currentTimeMillis()) / 1000;

                        MessageUtils.sendPlayer(player, EpicHomes.getPlugin().getMessagesManager().getCommandCooldownTimeRemaining()
                                .replace(TIME_PLACEHOLDER, Long.toString(timeLeft)));

                        event.setCancelled(true);
                        return;
                    }
                }
                coolDown.put(uuid, System.currentTimeMillis() + EpicHomes.getPlugin().getConfigManager().getCommandCooldownTime() * 1000L);
            }
        }
    }
}
