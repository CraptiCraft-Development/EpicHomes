package me.loving11ish.epichomes.updatesystem;

import me.loving11ish.epichomes.EpicHomes;
import me.loving11ish.epichomes.utils.ColorUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinEvent implements Listener {

    private final FileConfiguration config = EpicHomes.getPlugin().getConfig();
    private final FileConfiguration messagesConfig = EpicHomes.getPlugin().messagesFileManager.getMessagesConfig();

    private final String prefix = messagesConfig.getString("global-prefix");
    private static final String PREFIX_PLACEHOLDER = "%PREFIX%";

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("epichomes.update")||player.hasPermission("epichomes.*")||player.isOp()) {
            if (config.getBoolean("plugin-update-notifications.enabled")){
                new UpdateChecker(109590).getVersion(version -> {
                    try {
                        if (!(EpicHomes.getPlugin().getDescription().getVersion().equalsIgnoreCase(version))) {
                            player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("update-available.1").replace(PREFIX_PLACEHOLDER, prefix)));
                            player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("update-available.2").replace(PREFIX_PLACEHOLDER, prefix)));
                            player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("update-available.3").replace(PREFIX_PLACEHOLDER, prefix)));
                        }
                    }catch (NullPointerException e){
                        player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("Update-check-failure").replace(PREFIX_PLACEHOLDER, prefix)));
                    }
                });
            }
        }
    }
}
