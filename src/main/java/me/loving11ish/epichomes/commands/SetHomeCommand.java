package me.loving11ish.epichomes.commands;

import com.tcoded.folialib.FoliaLib;
import me.loving11ish.epichomes.EpicHomes;
import me.loving11ish.epichomes.commands.subcommands.SetSubCommand;
import me.loving11ish.epichomes.utils.ColorUtils;
import me.loving11ish.epichomes.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class SetHomeCommand implements CommandExecutor {

    private static final FoliaLib foliaLib = EpicHomes.getFoliaLib();
    private static List<String> bannedNames;

    public static void updateBannedNamesList() {
        foliaLib.getScheduler().runLaterAsync(() ->
                bannedNames = EpicHomes.getPlugin().getConfigManager().getBannedHomeNames(), 50L, TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length < 1) {
                MessageUtils.sendPlayerNoPrefix(player, sendUsageMessage());
                return true;
            }

            else {

                if (args[0] != null) {
                    return new SetSubCommand().setHomeSubCommand(sender, args, bannedNames);
                }

                else {
                    MessageUtils.sendPlayerNoPrefix(player, sendUsageMessage());
                    return true;
                }
            }
        }

        else {
            MessageUtils.sendConsole(EpicHomes.getPlugin().getMessagesManager().getPlayerOnly());
        }
        return true;
    }

    private String sendUsageMessage() {
        List<String> usage = EpicHomes.getPlugin().getMessagesManager().getSetHomeCommandList();
        StringBuilder usageString = new StringBuilder();
        for (String line : usage) {
            usageString.append(ColorUtils.translateColorCodes(line));
        }
        return usageString.toString();
    }
}
