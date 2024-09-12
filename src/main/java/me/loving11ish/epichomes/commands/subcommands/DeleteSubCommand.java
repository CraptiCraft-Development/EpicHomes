package me.loving11ish.epichomes.commands.subcommands;

import com.tcoded.folialib.FoliaLib;
import me.loving11ish.epichomes.EpicHomes;
import me.loving11ish.epichomes.api.events.AsyncHomeDeleteEvent;
import me.loving11ish.epichomes.models.User;
import me.loving11ish.epichomes.utils.MessageUtils;
import me.loving11ish.epichomes.utils.UsermapStorageUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.List;

public class DeleteSubCommand {

    private final FoliaLib foliaLib = EpicHomes.getFoliaLib();
    private final UsermapStorageUtil usermapStorageUtil = EpicHomes.getPlugin().getUsermapStorageUtil();

    private static final String TARGET_PLACEHOLDER = "%TARGET%";
    private static final String HOME_NAME_PLACEHOLDER = "%HOME%";

    public boolean deleteSubCommand(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length >= 2) {
                String homeName = args[1];

                if (homeName != null) {
                    User user = usermapStorageUtil.getUserByOnlinePlayer(player);

                    if (user != null) {
                        List<String> userHomesList = usermapStorageUtil.getHomeNamesListByUser(user);

                        for (String home : userHomesList) {
                            if (homeName.equalsIgnoreCase(home)) {
                                try {
                                    if (usermapStorageUtil.removeHomeFromUser(user, homeName)) {

                                        foliaLib.getScheduler().runAsync((task) -> {
                                            fireHomeDeleteEvent(player, user, homeName);
                                            MessageUtils.sendDebugConsole("&aFired AsyncHomeDeleteEvent");
                                        });

                                        MessageUtils.sendPlayer(player, EpicHomes.getPlugin().getMessagesManager().getHomeDeleteSuccess()
                                                .replace(HOME_NAME_PLACEHOLDER, homeName));
                                    }

                                    else {
                                        MessageUtils.sendPlayer(player, EpicHomes.getPlugin().getMessagesManager().getHomeDeleteFail()
                                                .replace(HOME_NAME_PLACEHOLDER, homeName));
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    public boolean deleteHomeSubCommand(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            String homeName = args[0];

            if (homeName != null) {
                User user = usermapStorageUtil.getUserByOnlinePlayer(player);

                if (user != null) {
                    List<String> userHomesList = usermapStorageUtil.getHomeNamesListByUser(user);

                    for (String home : userHomesList) {
                        if (homeName.equalsIgnoreCase(home)) {
                            try {
                                if (usermapStorageUtil.removeHomeFromUser(user, homeName)) {

                                    foliaLib.getScheduler().runAsync((task) -> {
                                        fireHomeDeleteEvent(player, user, homeName);
                                        MessageUtils.sendDebugConsole("&aFired AsyncHomeDeleteEvent");
                                    });

                                    MessageUtils.sendPlayer(player, EpicHomes.getPlugin().getMessagesManager().getHomeDeleteSuccess()
                                            .replace(HOME_NAME_PLACEHOLDER, homeName));
                                }

                                else {
                                    MessageUtils.sendPlayer(player, EpicHomes.getPlugin().getMessagesManager().getHomeDeleteFail()
                                            .replace(HOME_NAME_PLACEHOLDER, homeName));
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    public boolean adminDeleteHomeSubCommand(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            OfflinePlayer offlineTarget = usermapStorageUtil.getBukkitOfflinePlayerByLastKnownName(args[1]);

            if (offlineTarget != null) {
                User targetUser = usermapStorageUtil.getUserByOfflinePlayer(offlineTarget);
                String homeName = args[2];

                if (targetUser != null) {
                    List<String> targetHomesList = usermapStorageUtil.getHomeNamesListByUser(targetUser);

                    if (homeName != null && targetHomesList.contains(homeName)) {
                        try {
                            if (usermapStorageUtil.removeHomeFromUser(targetUser, homeName)) {

                                foliaLib.getScheduler().runAsync((task) -> {
                                    fireHomeDeleteEvent(offlineTarget.getPlayer(), targetUser, homeName);
                                    MessageUtils.sendDebugConsole("&aFired AsyncHomeDeleteEvent");
                                });

                                MessageUtils.sendPlayer(player, EpicHomes.getPlugin().getMessagesManager().getHomeAdminDeleteSuccess()
                                        .replace(TARGET_PLACEHOLDER, targetUser.getLastKnownName())
                                        .replace(HOME_NAME_PLACEHOLDER, homeName));
                            }

                            else {
                                MessageUtils.sendPlayer(player, EpicHomes.getPlugin().getMessagesManager().getHomeAdminDeleteFail()
                                        .replace(TARGET_PLACEHOLDER, targetUser.getLastKnownName())
                                        .replace(HOME_NAME_PLACEHOLDER, homeName));
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                else {
                    MessageUtils.sendPlayer(player, EpicHomes.getPlugin().getMessagesManager().getUserNotFound()
                            .replace(TARGET_PLACEHOLDER, args[1]));
                }
            }

            else {
                MessageUtils.sendPlayer(player, EpicHomes.getPlugin().getMessagesManager().getPlayerNotFound()
                        .replace(TARGET_PLACEHOLDER, args[1]));
            }
        }
        return true;
    }

    private static void fireHomeDeleteEvent(Player player, User user, String homeName) {
        AsyncHomeDeleteEvent asyncHomeDeleteEvent = new AsyncHomeDeleteEvent(player, user, homeName);
        Bukkit.getPluginManager().callEvent(asyncHomeDeleteEvent);
    }
}
