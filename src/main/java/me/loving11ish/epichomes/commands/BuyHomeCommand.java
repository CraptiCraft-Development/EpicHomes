package me.loving11ish.epichomes.commands;

import me.loving11ish.epichomes.EpicHomes;
import me.loving11ish.epichomes.commands.subcommands.BuySubCommand;
import me.loving11ish.epichomes.utils.ColorUtils;
import me.loving11ish.epichomes.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class BuyHomeCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length > 1) {
            MessageUtils.sendSender(sender, sendUsageMessage());
            return true;
        }

        return new BuySubCommand().buyHomeCommand(sender, args);
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        List<String> result = new ArrayList<>();
        if (args.length == 1 && "confirm".startsWith(args[0].toLowerCase())) {
            result.add("confirm");
        }
        return result;
    }

    private String sendUsageMessage() {
        List<String> usage = EpicHomes.getPlugin().getMessagesManager().getBuyHomeCommandList();
        StringBuilder usageString = new StringBuilder();
        for (String line : usage) {
            usageString.append(ColorUtils.translateColorCodes(line));
        }
        return usageString.toString();
    }
}
