package me.loving11ish.epichomes.commands;

import me.loving11ish.epichomes.EpicHomes;
import me.loving11ish.epichomes.commands.subcommands.SetSubCommand;
import me.loving11ish.epichomes.utils.ColorUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.logging.Logger;

public class SetHomeCommand implements CommandExecutor {

    Logger logger = EpicHomes.getPlugin().getLogger();
    FileConfiguration messagesConfig = EpicHomes.getPlugin().messagesFileManager.getMessagesConfig();

    private String prefix = messagesConfig.getString("global-prefix");
    private static final String PREFIX_PLACEHOLDER = "%PREFIX%";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player){
            if (args.length < 1){
                player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("incorrect-sethome-command-usage.line-1").replace(PREFIX_PLACEHOLDER, prefix)));
                player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("incorrect-sethome-command-usage.line-2").replace(PREFIX_PLACEHOLDER, prefix)));
                return true;
            }else {
                if (args[0] != null){
                    return new SetSubCommand().setHomeSubCommand(sender, args);
                }else {
                    player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("incorrect-sethome-command-usage.line-2").replace(PREFIX_PLACEHOLDER, prefix)));
                    return true;
                }
            }
        }else {
            logger.warning(ColorUtils.translateColorCodes(messagesConfig.getString("incorrect-command-usage")
                    .replace(PREFIX_PLACEHOLDER, prefix)));
        }
        return true;
    }
}
