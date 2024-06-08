package me.loving11ish.epichomes.commands.subcommands;

import me.loving11ish.epichomes.EpicHomes;
import me.loving11ish.epichomes.models.User;
import me.loving11ish.epichomes.utils.ColorUtils;
import me.loving11ish.epichomes.utils.MessageUtils;
import me.loving11ish.epichomes.utils.UsermapStorageUtil;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ListSubCommand {

    private final UsermapStorageUtil usermapStorageUtil = EpicHomes.getPlugin().getUsermapStorageUtil();

    private static final String TARGET_PLACEHOLDER = "%TARGET%";
    private static final String HOME_NAME_PLACEHOLDER = "%HOME%";

    public boolean listSubCommand(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            User user = usermapStorageUtil.getUserByOnlinePlayer(player);

            if (user != null) {
                MessageUtils.sendPlayerNoPrefix(player, buildHomeList(user));
            }
        }
        return true;
    }

    public boolean adminListSubCommand(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            OfflinePlayer offlineTarget = usermapStorageUtil.getBukkitOfflinePlayerByLastKnownName(args[1]);

            if (offlineTarget != null) {
                User user = usermapStorageUtil.getUserByOfflinePlayer(offlineTarget);
                if (user != null) {
                    MessageUtils.sendPlayerNoPrefix(player, buildAdminHomeList(user)
                            .replace(TARGET_PLACEHOLDER, args[1]));
                }

                else {
                    MessageUtils.sendPlayer(player, EpicHomes.getPlugin().getMessagesManager().getUserNotFound()
                            .replace(TARGET_PLACEHOLDER, args[1]));
                }
            }
        }
        return true;
    }

    private String buildHomeList(User user) {
        List<String> userHomesList = usermapStorageUtil.getHomeNamesListByUser(user);
        StringBuilder listString = new StringBuilder();

        List<String> configList = EpicHomes.getPlugin().getMessagesManager().getPlayerHomeList();
        return getBuiltString(userHomesList, listString, configList);
    }

    private String buildAdminHomeList(User user) {
        List<String> userHomesList = usermapStorageUtil.getHomeNamesListByUser(user);
        StringBuilder listString = new StringBuilder();

        List<String> configList = EpicHomes.getPlugin().getMessagesManager().getAdminHomeList();
        return getBuiltString(userHomesList, listString, configList);
    }

    private String getBuiltString(List<String> userHomesList, StringBuilder listString, List<String> configList) {
        for (String line : configList) {
            if (line.contains(HOME_NAME_PLACEHOLDER)) {
                for (String home : userHomesList) {
                    listString.append(line.replace(HOME_NAME_PLACEHOLDER, home));
                }
            } else {
                listString.append(ColorUtils.translateColorCodes(line));
            }
        }
        return listString.toString();
    }
}
