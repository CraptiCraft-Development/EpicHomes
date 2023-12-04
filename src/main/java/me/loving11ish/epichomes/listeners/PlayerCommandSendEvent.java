package me.loving11ish.epichomes.listeners;

import me.loving11ish.epichomes.EpicHomes;
import me.loving11ish.epichomes.utils.ColorUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PlayerCommandSendEvent implements Listener {

    private final FileConfiguration config = EpicHomes.getPlugin().getConfig();
    private final FileConfiguration messagesConfig = EpicHomes.getPlugin().messagesFileManager.getMessagesConfig();

    private final String prefix = messagesConfig.getString("global-prefix", "&f[&6Epic&bHomes&f]&r");
    private static final String PREFIX_PLACEHOLDER = "%PREFIX%";
    private static final String TIME_PLACEHOLDER = "%TIMELEFT%";

    private final HashMap<UUID, Long> coolDown = new HashMap<>();
    private final List<String> pluginCommands;

    public PlayerCommandSendEvent(List<String> pluginCommands) {
        this.pluginCommands = pluginCommands;
    }

    @EventHandler
    public void onCommandSend(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (!config.getBoolean("general.command-cool-down.enabled")){
            return;
        }
        if (player.hasPermission("epichomes.bypass.cooldown")||player.hasPermission("epichomes.bypass.*")
                ||player.hasPermission("epichomes.*")||player.isOp()){
            return;
        }

        String[] message = event.getMessage().split(" ");
        String command = message[0];
        for (String string : pluginCommands){
            if (command.equalsIgnoreCase(string)){
                if (coolDown.containsKey(uuid)){
                    if (coolDown.get(uuid) > System.currentTimeMillis()){
                        Long timeLeft = (coolDown.get(uuid) - System.currentTimeMillis()) / 1000;
                        player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("command-cool-down-time-left")
                                .replace(PREFIX_PLACEHOLDER, prefix).replace(TIME_PLACEHOLDER, timeLeft.toString())));
                        event.setCancelled(true);
                        return;
                    }
                }
                coolDown.put(uuid, System.currentTimeMillis() + config.getLong("general.command-cool-down.cool-down-time") * 1000);
            }
        }
    }
}
