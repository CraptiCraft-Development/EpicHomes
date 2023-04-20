package me.loving11ish.epichomes.commands.subcommands;

import me.loving11ish.epichomes.EpicHomes;
import me.loving11ish.epichomes.api.HomeSetEvent;
import me.loving11ish.epichomes.models.User;
import me.loving11ish.epichomes.utils.ColorUtils;
import me.loving11ish.epichomes.utils.UsermapStorageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.logging.Logger;

public class SetSubCommand {

    Logger logger = EpicHomes.getPlugin().getLogger();

    FileConfiguration config = EpicHomes.getPlugin().getConfig();
    FileConfiguration messagesConfig = EpicHomes.getPlugin().messagesFileManager.getMessagesConfig();

    private static final String PREFIX_PLACEHOLDER = "%PREFIX%";
    private static final String HOME_NAME_PLACEHOLDER = "%HOME%";

    private String prefix = messagesConfig.getString("global-prefix");
    private UsermapStorageUtil usermapStorageUtil = EpicHomes.getPlugin().usermapStorageUtil;

    public boolean setSubCommand(CommandSender sender, String[] args) {
        if (sender instanceof Player){
            Player player = (Player) sender;
            if (args.length >= 2){
                String homeName = args[1];
                if (homeName != null){
                    Location location = player.getLocation();
                    User user = usermapStorageUtil.getUserByOnlinePlayer(player);
                    if (user != null){
                        List<String> homesList = usermapStorageUtil.getHomeNamesListByUser(user);
                        if (!(player.hasPermission("epichomes.maxhomes.*")||player.hasPermission("epichomes.maxhomes")
                                ||player.hasPermission("epichomes.*")||player.isOp())){
                            if (!config.getBoolean("homes.permission-based-homes-max-amount.enabled")){
                                if (homesList.size() >= config.getInt("homes.default-max-homes")){
                                    player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("home-set-failed-max-homes-reached")
                                            .replace(PREFIX_PLACEHOLDER, prefix)));
                                    return true;
                                }
                            }else {
                                if (player.hasPermission("epichomes.maxhomes.group6")){
                                    if (homesList.size() >= config.getInt("homes.permission-based-homes-max-amount.permission-group-list.group-6")){
                                        player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("home-set-failed-max-homes-reached")
                                                .replace(PREFIX_PLACEHOLDER, prefix)));
                                        return true;
                                    }
                                }else if (player.hasPermission("epichomes.maxhomes.group5")){
                                    if (homesList.size() >= config.getInt("homes.permission-based-homes-max-amount.permission-group-list.group-5")){
                                        player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("home-set-failed-max-homes-reached")
                                                .replace(PREFIX_PLACEHOLDER, prefix)));
                                        return true;
                                    }
                                }else if (player.hasPermission("epichomes.maxhomes.group4")){
                                    if (homesList.size() >= config.getInt("homes.permission-based-homes-max-amount.permission-group-list.group-4")){
                                        player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("home-set-failed-max-homes-reached")
                                                .replace(PREFIX_PLACEHOLDER, prefix)));
                                        return true;
                                    }
                                }else if (player.hasPermission("epichomes.maxhomes.group3")){
                                    if (homesList.size() >= config.getInt("homes.permission-based-homes-max-amount.permission-group-list.group-3")){
                                        player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("home-set-failed-max-homes-reached")
                                                .replace(PREFIX_PLACEHOLDER, prefix)));
                                        return true;
                                    }
                                }else if (player.hasPermission("epichomes.maxhomes.group2")){
                                    if (homesList.size() >= config.getInt("homes.permission-based-homes-max-amount.permission-group-list.group-2")){
                                        player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("home-set-failed-max-homes-reached")
                                                .replace(PREFIX_PLACEHOLDER, prefix)));
                                        return true;
                                    }
                                }else if (player.hasPermission("epichomes.maxhomes.group1")){
                                    if (homesList.size() >= config.getInt("homes.permission-based-homes-max-amount.permission-group-list.group-1")){
                                        player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("home-set-failed-max-homes-reached")
                                                .replace(PREFIX_PLACEHOLDER, prefix)));
                                        return true;
                                    }
                                }
                            }
                        }
                        if (usermapStorageUtil.addHomeToUser(user, homeName, location)){
                            player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("home-set-successfully")
                                    .replace(PREFIX_PLACEHOLDER, prefix)
                                    .replace(HOME_NAME_PLACEHOLDER, homeName)));
                            fireHomeSetEvent(player, user, homeName, location);
                            if (config.getBoolean("general.developer-debug-mode.enabled")){
                                logger.info(ColorUtils.translateColorCodes("&6EpicHomes-Debug: &aFired HomeSetEvent"));
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    public boolean setHomeSubCommand(CommandSender sender, String[] args) {
        if (sender instanceof Player){
            Player player = (Player) sender;
            String homeName = args[0];
            if (homeName != null){
                Location location = player.getLocation();
                User user = usermapStorageUtil.getUserByOnlinePlayer(player);
                if (user != null){
                    List<String> homesList = usermapStorageUtil.getHomeNamesListByUser(user);
                    if (!(player.hasPermission("epichomes.maxhomes.*")||player.hasPermission("epichomes.maxhomes")
                            ||player.hasPermission("epichomes.*")||player.isOp())){
                        if (!config.getBoolean("homes.permission-based-homes-max-amount.enabled")){
                            if (homesList.size() >= config.getInt("homes.default-max-homes")){
                                player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("home-set-failed-max-homes-reached")
                                        .replace(PREFIX_PLACEHOLDER, prefix)));
                                return true;
                            }else {
                                if (player.hasPermission("epichomes.maxhomes.group6")){
                                    if (homesList.size() >= config.getInt("homes.permission-based-homes-max-amount.permission-group-list.group-6")){
                                        player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("home-set-failed-max-homes-reached")
                                                .replace(PREFIX_PLACEHOLDER, prefix)));
                                        return true;
                                    }
                                }else if (player.hasPermission("epichomes.maxhomes.group5")){
                                    if (homesList.size() >= config.getInt("homes.permission-based-homes-max-amount.permission-group-list.group-5")){
                                        player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("home-set-failed-max-homes-reached")
                                                .replace(PREFIX_PLACEHOLDER, prefix)));
                                        return true;
                                    }
                                }else if (player.hasPermission("epichomes.maxhomes.group4")){
                                    if (homesList.size() >= config.getInt("homes.permission-based-homes-max-amount.permission-group-list.group-4")){
                                        player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("home-set-failed-max-homes-reached")
                                                .replace(PREFIX_PLACEHOLDER, prefix)));
                                        return true;
                                    }
                                }else if (player.hasPermission("epichomes.maxhomes.group3")){
                                    if (homesList.size() >= config.getInt("homes.permission-based-homes-max-amount.permission-group-list.group-3")){
                                        player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("home-set-failed-max-homes-reached")
                                                .replace(PREFIX_PLACEHOLDER, prefix)));
                                        return true;
                                    }
                                }else if (player.hasPermission("epichomes.maxhomes.group2")){
                                    if (homesList.size() >= config.getInt("homes.permission-based-homes-max-amount.permission-group-list.group-2")){
                                        player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("home-set-failed-max-homes-reached")
                                                .replace(PREFIX_PLACEHOLDER, prefix)));
                                        return true;
                                    }
                                }else if (player.hasPermission("epichomes.maxhomes.group1")){
                                    if (homesList.size() >= config.getInt("homes.permission-based-homes-max-amount.permission-group-list.group-1")){
                                        player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("home-set-failed-max-homes-reached")
                                                .replace(PREFIX_PLACEHOLDER, prefix)));
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                    if (usermapStorageUtil.addHomeToUser(user, homeName, location)){
                        player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("home-set-successfully")
                                .replace(PREFIX_PLACEHOLDER, prefix)
                                .replace(HOME_NAME_PLACEHOLDER, homeName)));
                        fireHomeSetEvent(player, user, homeName, location);
                        if (config.getBoolean("general.developer-debug-mode.enabled")){
                            logger.info(ColorUtils.translateColorCodes("&6EpicHomes-Debug: &aFired HomeSetEvent"));
                        }
                    }
                }
            }
        }
        return true;
    }

    private static void fireHomeSetEvent(Player player, User user, String homeName, Location location) {
        HomeSetEvent homeSetEvent = new HomeSetEvent(player, user, homeName, location);
        Bukkit.getPluginManager().callEvent(homeSetEvent);
    }
}
