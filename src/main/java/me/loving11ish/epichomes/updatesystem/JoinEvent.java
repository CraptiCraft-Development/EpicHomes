package me.loving11ish.epichomes.updatesystem;

import me.loving11ish.epichomes.EpicHomes;
import me.loving11ish.epichomes.utils.ColorUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinEvent implements Listener {

    FileConfiguration config = EpicHomes.getPlugin().getConfig();
    FileConfiguration messagesConfig = EpicHomes.getPlugin().messagesFileManager.getMessagesConfig();

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
//        if (player.hasPermission("epichomes.update")||player.hasPermission("epichomes.*")||player.isOp()) {
//            if (config.getBoolean("plugin-update-notifications.enabled")){
//                new UpdateChecker("TODO").getVersion(version -> {
//                    try {
//                        if (!(EpicHomes.getPlugin().getDescription().getVersion().equalsIgnoreCase(version))) {
//                            player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("update-available.1")));
//                            player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("update-available.2")));
//                            player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("update-available.3")));
//                        }
//                    }catch (NullPointerException e){
//                        player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("Update-check-failure")));
//                    }
//                });
//            }
//        }
    }
}
