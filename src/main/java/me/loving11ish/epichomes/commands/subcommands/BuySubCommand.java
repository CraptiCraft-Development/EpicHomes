package me.loving11ish.epichomes.commands.subcommands;

import me.loving11ish.epichomes.EpicHomes;
import me.loving11ish.epichomes.utils.HomePurchaseUtil;
import me.loving11ish.epichomes.utils.MessageUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BuySubCommand {

    private final HomePurchaseUtil homePurchaseUtil = new HomePurchaseUtil();

    public boolean buySubCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            MessageUtils.sendConsole(EpicHomes.getPlugin().getMessagesManager().getPlayerOnly());
            return true;
        }

        Player player = (Player) sender;

        if (args.length >= 2 && args[1].equalsIgnoreCase("confirm")) {
            return homePurchaseUtil.purchaseExtraHome(player);
        }

        return homePurchaseUtil.sendPurchaseDisclaimer(player);
    }

    public boolean buyHomeCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            MessageUtils.sendConsole(EpicHomes.getPlugin().getMessagesManager().getPlayerOnly());
            return true;
        }

        Player player = (Player) sender;

        if (args.length >= 1 && args[0].equalsIgnoreCase("confirm")) {
            return homePurchaseUtil.purchaseExtraHome(player);
        }

        return homePurchaseUtil.sendPurchaseDisclaimer(player);
    }
}
