package me.loving11ish.epichomes.commands;

import me.loving11ish.epichomes.EpicHomes;
import me.loving11ish.epichomes.commands.subcommands.DeleteSubCommand;
import me.loving11ish.epichomes.commands.subcommands.HomeAdminVisitSubCommand;
import me.loving11ish.epichomes.commands.subcommands.ListSubCommand;
import me.loving11ish.epichomes.commands.subcommands.ReloadSubCommand;
import me.loving11ish.epichomes.models.User;
import me.loving11ish.epichomes.utils.ColorUtils;
import me.loving11ish.epichomes.utils.MessageUtils;
import me.loving11ish.epichomes.utils.UsermapStorageUtil;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class HomeAdminCommand implements CommandExecutor, TabCompleter {

    private final UsermapStorageUtil usermapStorageUtil = EpicHomes.getPlugin().getUsermapStorageUtil();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length < 1) {
                MessageUtils.sendPlayerNoPrefix(player, sendUsageMessage());
                return true;
            }

            else if (args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("list")
                    || args[0].equalsIgnoreCase("visit") || args[0].equalsIgnoreCase("reload")) {
                switch (args[0]) {


                    case "delete":
                        if (args.length == 3) {
                            if (args[1] != null && args[2] != null) {

                                if (player.hasPermission("epichomes.command.deleteothers") || player.hasPermission("epichomes.command.*")
                                        || player.hasPermission("epichomes.admin") || player.hasPermission("epichomes.*") || player.isOp()) {
                                    return new DeleteSubCommand().adminDeleteHomeSubCommand(sender, args);
                                }

                                else {
                                    MessageUtils.sendPlayer(player, EpicHomes.getPlugin().getMessagesManager().getNoPermission());
                                    return true;
                                }
                            }

                            else {
                                MessageUtils.sendPlayerNoPrefix(player, sendUsageMessage());
                                return true;
                            }
                        }

                        else {
                            MessageUtils.sendPlayerNoPrefix(player, sendUsageMessage());
                            return true;
                        }


                    case "visit":
                        if (args.length == 3) {
                            if (args[1] != null && args[2] != null) {

                                if (player.hasPermission("epichomes.command.visitothers") || player.hasPermission("epichomes.command.*")
                                        || player.hasPermission("epichomes.admin") || player.hasPermission("epichomes.*") || player.isOp()) {
                                    return new HomeAdminVisitSubCommand().homeAdminVisitSubCommand(sender, args);
                                }

                                else {
                                    MessageUtils.sendPlayer(player, EpicHomes.getPlugin().getMessagesManager().getNoPermission());
                                    return true;
                                }
                            }

                            else {
                                MessageUtils.sendPlayerNoPrefix(player, sendUsageMessage());
                                return true;
                            }
                        }

                        else {
                            MessageUtils.sendPlayerNoPrefix(player, sendUsageMessage());
                            return true;
                        }


                    case "list":
                        if (args.length == 2) {
                            if (args[1] != null) {

                                if (player.hasPermission("epichomes.command.listothers") || player.hasPermission("epichomes.command.*")
                                        || player.hasPermission("epichomes.admin") || player.hasPermission("epichomes.*") || player.isOp()) {
                                    return new ListSubCommand().adminListSubCommand(sender, args);
                                }

                                else {
                                    MessageUtils.sendPlayer(player, EpicHomes.getPlugin().getMessagesManager().getNoPermission());
                                    return true;
                                }
                            }

                            else {
                                MessageUtils.sendPlayerNoPrefix(player, sendUsageMessage());
                                return true;
                            }
                        }

                        else {
                            MessageUtils.sendPlayerNoPrefix(player, sendUsageMessage());
                            return true;
                        }


                    case "reload":
                        if (player.hasPermission("epichomes.command.reload") || player.hasPermission("epichomes.command.*")
                                || player.hasPermission("epichomes.admin") || player.hasPermission("epichomes.*") || player.isOp()) {
                            return new ReloadSubCommand().reloadSubCommand(sender);
                        }

                        else {
                            MessageUtils.sendPlayer(player, EpicHomes.getPlugin().getMessagesManager().getNoPermission());
                            return true;
                        }


                    default:
                        MessageUtils.sendPlayerNoPrefix(player, sendUsageMessage());
                }


            }

            else {
                MessageUtils.sendPlayerNoPrefix(player, sendUsageMessage());
            }
        }

        else if (sender instanceof ConsoleCommandSender) {
            if (args.length == 1) {

                if (args[0].equalsIgnoreCase("reload")) {
                    return new ReloadSubCommand().reloadSubCommand();
                }

                else {
                    MessageUtils.sendConsole(sendUsageMessage());
                    return true;
                }
            }

            else {
                MessageUtils.sendConsole(sendUsageMessage());
                return true;
            }
        }
        return true;
    }

    private String sendUsageMessage() {
        List<String> usage = EpicHomes.getPlugin().getMessagesManager().getHomeAdminCommandList();
        StringBuilder usageString = new StringBuilder();
        for (String line : usage) {
            usageString.append(ColorUtils.translateColorCodes(line));
        }
        return usageString.toString();
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        List<String> arguments0 = new ArrayList<>();

        arguments0.add("delete");
        arguments0.add("visit");
        arguments0.add("list");
        arguments0.add("reload");

        List<String> result = new ArrayList<>();
        List<String> result2 = new ArrayList<>();

        if (args.length == 1) {
            for (String a : arguments0) {
                if (a.toLowerCase().startsWith(args[0].toLowerCase())) {
                    result.add(a);
                }
            }
            return result;
        }

        else if (args.length == 2) {
            return null;
        }

        else if (args.length == 3) {
            OfflinePlayer offlinePlayer = usermapStorageUtil.getBukkitOfflinePlayerByLastKnownName(args[1]);

            if (offlinePlayer != null) {
                User user = usermapStorageUtil.getUserByOfflinePlayer(offlinePlayer);

                if (user != null) {
                    List<String> userHomesList = usermapStorageUtil.getHomeNamesListByUser(user);

                    for (String home : userHomesList) {
                        if (home.toLowerCase().startsWith(args[2].toLowerCase())) {
                            result2.add(home);
                        }
                    }
                    return result2;
                }
            }
        }

        return null;
    }
}
