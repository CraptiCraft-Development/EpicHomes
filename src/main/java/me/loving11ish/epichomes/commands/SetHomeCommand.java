package me.loving11ish.epichomes.commands;

import com.tcoded.folialib.FoliaLib;
import me.loving11ish.epichomes.EpicHomes;
import me.loving11ish.epichomes.commands.subcommands.SetSubCommand;
import me.loving11ish.epichomes.utils.ColorUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class SetHomeCommand implements CommandExecutor {

    ConsoleCommandSender console = Bukkit.getConsoleSender();

    FileConfiguration messagesConfig = EpicHomes.getPlugin().messagesFileManager.getMessagesConfig();

    private static FoliaLib foliaLib = EpicHomes.getFoliaLib();
    private static List<String> bannedNames;

    private String prefix = messagesConfig.getString("global-prefix");
    private static final String PREFIX_PLACEHOLDER = "%PREFIX%";

    public static void updateBannedNamesList() {
        foliaLib.getImpl().runLaterAsync(() ->
                bannedNames = EpicHomes.getPlugin().getConfig().getStringList("homes.disallowed-home-names"), 50L, TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player){
            Player player = (Player) sender;
            if (args.length < 1){
                player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("incorrect-sethome-command-usage.line-1").replace(PREFIX_PLACEHOLDER, prefix)));
                player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("incorrect-sethome-command-usage.line-2").replace(PREFIX_PLACEHOLDER, prefix)));
                return true;
            }else {
                if (args[0] != null){
                    return new SetSubCommand().setHomeSubCommand(sender, args, bannedNames);
                }else {
                    player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("incorrect-sethome-command-usage.line-2").replace(PREFIX_PLACEHOLDER, prefix)));
                    return true;
                }
            }
        }else {
            console.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("incorrect-command-usage")
                    .replace(PREFIX_PLACEHOLDER, prefix)));
        }
        return true;
    }
}
