package me.loving11ish.epichomes.commands.subcommands;

import me.loving11ish.epichomes.EpicHomes;
import me.loving11ish.epichomes.api.events.HomePreTeleportEvent;
import me.loving11ish.epichomes.models.User;
import me.loving11ish.epichomes.utils.ColorUtils;
import me.loving11ish.epichomes.utils.TeleportationUtils;
import me.loving11ish.epichomes.utils.UsermapStorageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;

public class HomeAdminVisitSubCommand {

    private final ConsoleCommandSender console = Bukkit.getConsoleSender();

    private final FileConfiguration config = EpicHomes.getPlugin().getConfig();
    private final FileConfiguration messagesConfig = EpicHomes.getPlugin().messagesFileManager.getMessagesConfig();

    private static final String PREFIX_PLACEHOLDER = "%PREFIX%";
    private static final String TARGET_PLACEHOLDER = "%TARGET%";
    private static final String HOME_NAME_PLACEHOLDER = "%HOME%";

    private final String prefix = messagesConfig.getString("global-prefix", "&f[&6Epic&bHomes&f]&r");
    private final UsermapStorageUtil usermapStorageUtil = EpicHomes.getPlugin().usermapStorageUtil;

    public boolean homeAdminVisitSubCommand(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            OfflinePlayer offlineTarget = usermapStorageUtil.getBukkitOfflinePlayerByLastKnownName(args[1]);
            if (offlineTarget != null) {
                User user = usermapStorageUtil.getUserByOfflinePlayer(offlineTarget);
                if (user != null) {
                    List<String> userHomesList = usermapStorageUtil.getHomeNamesListByUser(user);
                    for (String home : userHomesList) {
                        if (!userHomesList.contains(args[2])){
                            player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("home-name-does-not-exist")
                                    .replace(PREFIX_PLACEHOLDER, prefix)
                                    .replace(HOME_NAME_PLACEHOLDER, args[2])));
                            return true;
                        }
                        if (args[2].equalsIgnoreCase(home)){
                            Location homeLocation = usermapStorageUtil.getHomeLocationByHomeName(user, args[2]);
                            if (config.getBoolean("homes.teleportation.delay-before-teleport.enabled")){
                                TeleportationUtils teleportationUtils = new TeleportationUtils();
                                fireHomePreTeleportEvent(player, user, args[2], homeLocation, player.getLocation());
                                if (config.getBoolean("general.developer-debug-mode.enabled", false)){
                                    console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes-Debug: &aFired HomePreTeleportEvent"));
                                }
                                teleportationUtils.teleportPlayerAsyncTimed(player, homeLocation, args[2]);
                            }else {
                                TeleportationUtils teleportationUtils = new TeleportationUtils();
                                fireHomePreTeleportEvent(player, user, args[2], homeLocation, player.getLocation());
                                if (config.getBoolean("general.developer-debug-mode.enabled", false)){
                                    console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes-Debug: &aFired HomePreTeleportEvent"));
                                }
                                teleportationUtils.teleportPlayerAsync(player, homeLocation, args[2]);
                            }
                            return true;
                        }
                    }
                }else {
                    player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("homeadmin-unable-to-find-user")
                            .replace(PREFIX_PLACEHOLDER, prefix)
                            .replace(TARGET_PLACEHOLDER, args[1])));
                }
            }else {
                player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("homeadmin-unable-to-find-player")
                        .replace(PREFIX_PLACEHOLDER, prefix)
                        .replace(TARGET_PLACEHOLDER, args[1])));
            }
        }
        return true;
    }

    private static void fireHomePreTeleportEvent(Player player, User user, String homeName, Location homeLocation, Location oldLocation) {
        HomePreTeleportEvent homePreTeleportEvent = new HomePreTeleportEvent(player, user, homeName, homeLocation, oldLocation);
        Bukkit.getPluginManager().callEvent(homePreTeleportEvent);
    }
}
