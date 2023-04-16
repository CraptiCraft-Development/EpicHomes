package me.loving11ish.epichomes.commands.subcommands;

import com.tcoded.folialib.wrapper.WrappedTask;
import me.loving11ish.epichomes.EpicHomes;
import me.loving11ish.epichomes.utils.ColorUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

public class ReloadSubCommand {

    Logger logger = EpicHomes.getPlugin().getLogger();
    FileConfiguration messagesConfig = EpicHomes.getPlugin().messagesFileManager.getMessagesConfig();

    private static final String PREFIX_PLACEHOLDER = "%PREFIX%";

    private String prefix = messagesConfig.getString("global-prefix");

    public boolean reloadSubCommand(CommandSender sender) {
        if (sender instanceof Player){
            Player player = (Player) sender;
            player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("plugin-reload-start").replace(PREFIX_PLACEHOLDER, prefix)));
            logger.info(ColorUtils.translateColorCodes(messagesConfig.getString("plugin-reload-start").replace(PREFIX_PLACEHOLDER, prefix)));
            EpicHomes.getPlugin().reloadConfig();
            EpicHomes.getPlugin().messagesFileManager.reloadMessagesConfig();
            if (!EpicHomes.getPlugin().teleportQueue.isEmpty()){
                for (Map.Entry<UUID, WrappedTask> wrappedTaskEntry: EpicHomes.getPlugin().teleportQueue.entrySet()){
                    WrappedTask wrappedTask = wrappedTaskEntry.getValue();
                    wrappedTask.cancel();
                    UUID uuid = wrappedTaskEntry.getKey();
                    Player p = Bukkit.getPlayer(uuid);
                    if (p != null){
                        p.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("timed-teleport-failed-plugin-reloaded").replace(PREFIX_PLACEHOLDER, prefix)));
                    }
                }
            }
            try {
                if (!EpicHomes.getPlugin().usermapStorageUtil.getRawUsermapList().isEmpty()){
                    EpicHomes.getPlugin().usermapStorageUtil.saveUsermap();
                }
            } catch (IOException e) {
                player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("plugin-reload-failed").replace(PREFIX_PLACEHOLDER, prefix)));
                logger.severe(ColorUtils.translateColorCodes(messagesConfig.getString("plugin-reload-failed").replace(PREFIX_PLACEHOLDER, prefix)));
                e.printStackTrace();
                return true;
            }
            try {
                if (EpicHomes.getPlugin().usermapFileManager.getUsermapConfig().contains("users.data")){
                    EpicHomes.getPlugin().usermapStorageUtil.loadUsermap();
                }
            } catch (IOException e) {
                player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("plugin-reload-failed").replace(PREFIX_PLACEHOLDER, prefix)));
                logger.severe(ColorUtils.translateColorCodes(messagesConfig.getString("plugin-reload-failed").replace(PREFIX_PLACEHOLDER, prefix)));
                e.printStackTrace();
                return true;
            }
            player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("plugin-reload-complete").replace(PREFIX_PLACEHOLDER, prefix)));
            logger.info(ColorUtils.translateColorCodes(messagesConfig.getString("plugin-reload-complete").replace(PREFIX_PLACEHOLDER, prefix)));
            return true;
        }
        return true;
    }
}
