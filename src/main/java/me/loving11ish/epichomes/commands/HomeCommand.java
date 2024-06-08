package me.loving11ish.epichomes.commands;

import com.tcoded.folialib.FoliaLib;
import me.loving11ish.epichomes.EpicHomes;
import me.loving11ish.epichomes.api.events.AsyncHomePreTeleportEvent;
import me.loving11ish.epichomes.commands.subcommands.DeleteSubCommand;
import me.loving11ish.epichomes.commands.subcommands.ListSubCommand;
import me.loving11ish.epichomes.commands.subcommands.SetSubCommand;
import me.loving11ish.epichomes.menusystem.PlayerMenuUtility;
import me.loving11ish.epichomes.menusystem.menus.DeleteSingleGUI;
import me.loving11ish.epichomes.menusystem.paginatedmenus.HomeListGUI;
import me.loving11ish.epichomes.models.User;
import me.loving11ish.epichomes.utils.ColorUtils;
import me.loving11ish.epichomes.utils.MessageUtils;
import me.loving11ish.epichomes.utils.TeleportationUtils;
import me.loving11ish.epichomes.utils.UsermapStorageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class HomeCommand implements CommandExecutor, TabCompleter {

    private static final FoliaLib foliaLib = EpicHomes.getFoliaLib();
    private static List<String> bannedNames;

    private static final String HOME_NAME_PLACEHOLDER = "%HOME%";
    private final UsermapStorageUtil usermapStorageUtil = EpicHomes.getPlugin().getUsermapStorageUtil();

    public static void updateBannedNamesList() {
        foliaLib.getImpl().runLaterAsync(() ->
                bannedNames = EpicHomes.getPlugin().getConfigManager().getBannedHomeNames(), 50L, TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            User user = usermapStorageUtil.getUserByOnlinePlayer(player);

            if (args.length < 1) {
                if (EpicHomes.getPlugin().isGUIEnabled()) {
                    new HomeListGUI(EpicHomes.getPlayerMenuUtility(player)).open();
                }

                else {
                    MessageUtils.sendPlayerNoPrefix(player, sendUsageMessage());
                }
                return true;
            }

            else if (args[0].equalsIgnoreCase("set")
                    || args[0].equalsIgnoreCase("delete")
                    || args[0].equalsIgnoreCase("list")
                    || args[0].equalsIgnoreCase("reload")) {

                switch (args[0]) {

                    case "set":
                        if (args.length == 2) {
                            if (args[1] != null) {
                                return new SetSubCommand().setSubCommand(sender, args, bannedNames);
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

                    case "delete":
                        if (args.length == 2) {
                            if (args[1] != null) {
                                if (EpicHomes.getPlugin().isGUIEnabled()) {

                                    PlayerMenuUtility playerMenuUtility = EpicHomes.getPlayerMenuUtility(player);
                                    playerMenuUtility.setUser(user);
                                    playerMenuUtility.setHomeName(args[1]);
                                    playerMenuUtility.setHomeLocation(usermapStorageUtil.getHomeLocationByHomeName(user, args[1]));
                                    new DeleteSingleGUI(playerMenuUtility).open();
                                    return true;
                                }

                                else {
                                    return new DeleteSubCommand().deleteSubCommand(sender, args);
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
                        return new ListSubCommand().listSubCommand(sender);

                    default:
                        MessageUtils.sendPlayerNoPrefix(player, sendUsageMessage());
                }
            }

            else {
                List<String> userHomesList = usermapStorageUtil.getHomeNamesListByUser(user);
                for (String home : userHomesList) {

                    if (!userHomesList.contains(args[0])) {
                        MessageUtils.sendPlayer(player, EpicHomes.getPlugin().getMessagesManager().getHomeNotFound()
                                .replace(HOME_NAME_PLACEHOLDER, args[0]));
                        return true;
                    }

                    if (args[0].equalsIgnoreCase(home)) {
                        Location homeLocation = usermapStorageUtil.getHomeLocationByHomeName(user, args[0]);
                        TeleportationUtils teleportationUtils = new TeleportationUtils();

                        if (EpicHomes.getPlugin().getConfigManager().isUseDelayBeforeHomeTP()) {
                            foliaLib.getImpl().runAsync((task) -> {
                                fireHomePreTeleportEvent(player, user, args[0], homeLocation, player.getLocation());
                                MessageUtils.sendDebugConsole("&aFired AsyncHomePreTeleportEvent");
                            });

                            teleportationUtils.teleportPlayerAsyncTimed(player, homeLocation, args[0]);
                        }

                        else {
                            foliaLib.getImpl().runAsync((task) -> {
                                fireHomePreTeleportEvent(player, user, args[0], homeLocation, player.getLocation());
                                MessageUtils.sendDebugConsole("&aFired AsyncHomePreTeleportEvent");
                            });

                            teleportationUtils.teleportPlayerAsync(player, homeLocation, args[0]);
                        }
                        return true;
                    }
                }
            }
        }

        else if (sender instanceof ConsoleCommandSender) {
            MessageUtils.sendConsole(EpicHomes.getPlugin().getMessagesManager().getPlayerOnly());
            return true;
        }
        return true;
    }

    private String sendUsageMessage() {
        List<String> usage = EpicHomes.getPlugin().getMessagesManager().getHomeCommandList();
        StringBuilder usageString = new StringBuilder();
        for (String line : usage) {
            usageString.append(ColorUtils.translateColorCodes(line));
        }
        return usageString.toString();
    }

    private static void fireHomePreTeleportEvent(Player player, User user, String homeName, Location homeLocation, Location oldLocation) {
        AsyncHomePreTeleportEvent asyncHomePreTeleportEvent = new AsyncHomePreTeleportEvent(player, user, homeName, homeLocation, oldLocation);
        Bukkit.getPluginManager().callEvent(asyncHomePreTeleportEvent);
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        Player player = (Player) sender;
        User user = usermapStorageUtil.getUserByOnlinePlayer(player);
        List<String> homesList = usermapStorageUtil.getHomeNamesListByUser(user);

        List<String> arguments = new ArrayList<>(homesList);
        arguments.add("set");
        arguments.add("delete");
        arguments.add("list");

        List<String> result = new ArrayList<>();

        if (args.length == 1) {
            for (String a : arguments) {
                if (a.toLowerCase().startsWith(args[0].toLowerCase())) {
                    result.add(a);
                }
            }
            return result;
        }
        return null;
    }
}
