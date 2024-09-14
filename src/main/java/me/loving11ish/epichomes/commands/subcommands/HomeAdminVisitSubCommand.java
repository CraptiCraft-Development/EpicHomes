package me.loving11ish.epichomes.commands.subcommands;

import com.tcoded.folialib.FoliaLib;
import me.loving11ish.epichomes.EpicHomes;
import me.loving11ish.epichomes.api.events.AsyncHomePreTeleportEvent;
import me.loving11ish.epichomes.models.User;
import me.loving11ish.epichomes.utils.MessageUtils;
import me.loving11ish.epichomes.utils.TeleportationUtils;
import me.loving11ish.epichomes.utils.UsermapStorageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class HomeAdminVisitSubCommand {

    private final FoliaLib foliaLib = EpicHomes.getFoliaLib();
    private final UsermapStorageUtil usermapStorageUtil = EpicHomes.getPlugin().getUsermapStorageUtil();

    private static final String TARGET_PLACEHOLDER = "%TARGET%";
    private static final String HOME_NAME_PLACEHOLDER = "%HOME%";

    public boolean homeAdminVisitSubCommand(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            OfflinePlayer offlineTarget = usermapStorageUtil.getBukkitOfflinePlayerByLastKnownName(args[1]);

            if (offlineTarget != null) {
                User user = usermapStorageUtil.getUserByOfflinePlayer(offlineTarget);

                if (user != null) {
                    List<String> userHomesList = usermapStorageUtil.getHomeNamesListByUser(user);

                    for (String home : userHomesList) {
                        if (!userHomesList.contains(args[2])) {
                            MessageUtils.sendPlayer(player, EpicHomes.getPlugin().getMessagesManager().getHomeNotFound()
                                    .replace(HOME_NAME_PLACEHOLDER, args[2]));
                            return true;
                        }

                        if (args[2].equalsIgnoreCase(home)) {
                            Location homeLocation = usermapStorageUtil.getHomeLocationByHomeName(user, args[2]);

                            TeleportationUtils teleportationUtils = new TeleportationUtils();
                            if (EpicHomes.getPlugin().getConfigManager().isUseDelayBeforeHomeTP()) {

                                foliaLib.getScheduler().runAsync((task) -> {
                                    fireHomePreTeleportEvent(player, user, args[2], homeLocation, player.getLocation());
                                    MessageUtils.sendDebugConsole("&aFired AsyncHomePreTeleportEvent");
                                });

                                teleportationUtils.teleportPlayerAsyncTimed(player, homeLocation, args[2]);
                            }

                            else {
                                foliaLib.getScheduler().runAsync((task) -> {
                                    fireHomePreTeleportEvent(player, user, args[2], homeLocation, player.getLocation());
                                    MessageUtils.sendDebugConsole("&aFired AsyncHomePreTeleportEvent");
                                });

                                teleportationUtils.teleportPlayerAsync(player, homeLocation, args[2]);
                            }
                            return true;
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

    private static void fireHomePreTeleportEvent(Player player, User user, String homeName, Location homeLocation, Location oldLocation) {
        AsyncHomePreTeleportEvent asyncHomePreTeleportEvent = new AsyncHomePreTeleportEvent(player, user, homeName, homeLocation, oldLocation);
        Bukkit.getPluginManager().callEvent(asyncHomePreTeleportEvent);
    }
}
