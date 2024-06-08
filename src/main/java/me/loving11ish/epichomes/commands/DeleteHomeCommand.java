package me.loving11ish.epichomes.commands;

import me.loving11ish.epichomes.EpicHomes;
import me.loving11ish.epichomes.commands.subcommands.DeleteSubCommand;
import me.loving11ish.epichomes.menusystem.PlayerMenuUtility;
import me.loving11ish.epichomes.menusystem.menus.DeleteSingleGUI;
import me.loving11ish.epichomes.menusystem.paginatedmenus.DeleteHomesListGUI;
import me.loving11ish.epichomes.models.User;
import me.loving11ish.epichomes.utils.ColorUtils;
import me.loving11ish.epichomes.utils.MessageUtils;
import me.loving11ish.epichomes.utils.UsermapStorageUtil;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DeleteHomeCommand implements CommandExecutor, TabCompleter {

    private final UsermapStorageUtil usermapStorageUtil = EpicHomes.getPlugin().getUsermapStorageUtil();

    private static final String HOME_PLACEHOLDER = "%HOME%";

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            User user = usermapStorageUtil.getUserByOnlinePlayer(player);
            if (args.length < 1) {

                if (EpicHomes.getPlugin().isGUIEnabled()) {
                    new DeleteHomesListGUI(EpicHomes.getPlayerMenuUtility(player)).open();
                }

                else {
                    MessageUtils.sendPlayerNoPrefix(player, sendUsage());
                }
                return true;
            }

            else {
                if (args[0] != null) {
                    if (EpicHomes.getPlugin().isGUIEnabled()) {

                        PlayerMenuUtility playerMenuUtility = EpicHomes.getPlayerMenuUtility(player);
                        playerMenuUtility.setUser(user);
                        playerMenuUtility.setHomeName(args[0]);
                        Location location = usermapStorageUtil.getHomeLocationByHomeName(user, args[0]);

                        if (location != null) {
                            playerMenuUtility.setHomeLocation(location);
                            new DeleteSingleGUI(playerMenuUtility).open();
                        }

                        else {
                            MessageUtils.sendPlayer(player, EpicHomes.getPlugin().getMessagesManager()
                                    .getHomeNotFound().replace(HOME_PLACEHOLDER, args[0]));
                        }
                        return true;
                    }

                    else {
                        return new DeleteSubCommand().deleteHomeSubCommand(sender, args);
                    }
                }

                else {
                    MessageUtils.sendPlayerNoPrefix(player, sendUsage());
                    return true;
                }
            }
        }

        else {
            MessageUtils.sendConsole(EpicHomes.getPlugin().getMessagesManager().getPlayerOnly());
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        Player player = (Player) sender;
        User user = usermapStorageUtil.getUserByOnlinePlayer(player);
        List<String> homesList = usermapStorageUtil.getHomeNamesListByUser(user);

        List<String> arguments = new ArrayList<>(homesList);
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

    private String sendUsage() {
        List<String> configList = EpicHomes.getPlugin().getMessagesManager().getDeleteHomeCommandList();
        StringBuilder listString = new StringBuilder();
        for (String line : configList) {
            listString.append(ColorUtils.translateColorCodes(line));
        }
        return listString.toString();
    }
}
