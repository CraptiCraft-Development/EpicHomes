package me.loving11ish.epichomes.commands.subcommands;

import me.loving11ish.epichomes.EpicHomes;
import me.loving11ish.epichomes.api.events.HomeDeleteEvent;
import me.loving11ish.epichomes.models.User;
import me.loving11ish.epichomes.utils.ColorUtils;
import me.loving11ish.epichomes.utils.UsermapStorageUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.List;

public class DeleteSubCommand {

    private final ConsoleCommandSender console = Bukkit.getConsoleSender();

    private final FileConfiguration config = EpicHomes.getPlugin().getConfig();
    private final FileConfiguration messagesConfig = EpicHomes.getPlugin().messagesFileManager.getMessagesConfig();

    private static final String PREFIX_PLACEHOLDER = "%PREFIX%";
    private static final String TARGET_PLACEHOLDER = "%TARGET%";
    private static final String HOME_NAME_PLACEHOLDER = "%HOME%";

    private final String prefix = messagesConfig.getString("global-prefix", "&f[&6Epic&bHomes&f]&r");
    private final UsermapStorageUtil usermapStorageUtil = EpicHomes.getPlugin().usermapStorageUtil;

    public boolean deleteSubCommand(CommandSender sender, String[] args) {
        if (sender instanceof Player){
            Player player = (Player) sender;
            if (args.length >= 2) {
                String homeName = args[1];
                if (homeName != null) {
                    User user = usermapStorageUtil.getUserByOnlinePlayer(player);
                    if (user != null){
                        List<String> userHomesList = usermapStorageUtil.getHomeNamesListByUser(user);
                        for (String home : userHomesList){
                            if (homeName.equalsIgnoreCase(home)){
                                try {
                                    if (usermapStorageUtil.removeHomeFromUser(user, homeName)){
                                        fireHomeDeleteEvent(player, user, homeName);
                                        if (config.getBoolean("general.developer-debug-mode.enabled", false)){
                                            console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes-Debug: &aFired HomeDeleteEvent"));
                                        }
                                        player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("home-delete-successful")
                                                .replace(PREFIX_PLACEHOLDER, prefix)
                                                .replace(HOME_NAME_PLACEHOLDER, homeName)));
                                    }else {
                                        player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("home-delete-failed")
                                                .replace(PREFIX_PLACEHOLDER, prefix)
                                                .replace(HOME_NAME_PLACEHOLDER, homeName)));
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
                if (user != null){
                    List<String> userHomesList = usermapStorageUtil.getHomeNamesListByUser(user);
                    for (String home : userHomesList){
                        if (homeName.equalsIgnoreCase(home)){
                            try {
                                if (usermapStorageUtil.removeHomeFromUser(user, homeName)){
                                    fireHomeDeleteEvent(player, user, homeName);
                                    if (config.getBoolean("general.developer-debug-mode.enabled", false)){
                                        console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes-Debug: &aFired HomeDeleteEvent"));
                                    }
                                    player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("home-delete-successful")
                                            .replace(PREFIX_PLACEHOLDER, prefix)
                                            .replace(HOME_NAME_PLACEHOLDER, homeName)));
                                }else {
                                    player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("home-delete-failed")
                                            .replace(PREFIX_PLACEHOLDER, prefix)
                                            .replace(HOME_NAME_PLACEHOLDER, homeName)));
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
                                fireHomeDeleteEvent(offlineTarget.getPlayer(), targetUser, homeName);
                                if (config.getBoolean("general.developer-debug-mode.enabled", false)){
                                    console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes-Debug: &aFired HomeDeleteEvent"));
                                }
                                player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("homeadmin-delete-successful")
                                        .replace(PREFIX_PLACEHOLDER, prefix)
                                        .replace(TARGET_PLACEHOLDER, targetUser.getLastKnownName())
                                        .replace(HOME_NAME_PLACEHOLDER, homeName)));
                            }else {
                                player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("homeadmin-delete-failed")
                                        .replace(PREFIX_PLACEHOLDER, prefix)
                                        .replace(TARGET_PLACEHOLDER, targetUser.getLastKnownName())
                                        .replace(HOME_NAME_PLACEHOLDER, homeName)));
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
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

    private static void fireHomeDeleteEvent(Player player, User user, String homeName) {
        HomeDeleteEvent homeDeleteEvent = new HomeDeleteEvent(player, user, homeName);
        Bukkit.getPluginManager().callEvent(homeDeleteEvent);
    }
}
