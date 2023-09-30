package me.loving11ish.epichomes.commands.subcommands;

import me.loving11ish.epichomes.EpicHomes;
import me.loving11ish.epichomes.api.HomeSetEvent;
import me.loving11ish.epichomes.models.User;
import me.loving11ish.epichomes.utils.ColorUtils;
import me.loving11ish.epichomes.utils.UsermapStorageUtil;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;

public class SetSubCommand {

    ConsoleCommandSender console = Bukkit.getConsoleSender();

    FileConfiguration config = EpicHomes.getPlugin().getConfig();
    FileConfiguration messagesConfig = EpicHomes.getPlugin().messagesFileManager.getMessagesConfig();

    private static final String PREFIX_PLACEHOLDER = "%PREFIX%";
    private static final String HOME_NAME_PLACEHOLDER = "%HOME%";
    private static final String LIMIT_PLACEHOLDER = "%LIMIT%";

    private String prefix = messagesConfig.getString("global-prefix");
    private UsermapStorageUtil usermapStorageUtil = EpicHomes.getPlugin().usermapStorageUtil;

    public boolean setSubCommand(CommandSender sender, String[] args, List<String> bannedNames) {
        if (sender instanceof Player){
            Player player = (Player) sender;
            if (args.length >= 2){
                String homeName = args[1];
                if (homeName != null){
                    if (EpicHomes.getVersionCheckerUtils().getVersion() < 16){
                        if (!isNameValidBasicStringCheck(player, homeName, bannedNames)){
                            return true;
                        }
                    }if (EpicHomes.getVersionCheckerUtils().getVersion() >= 16){
                        if (!isNameValidStringUtils(player, homeName, bannedNames)){
                            return true;
                        }
                    }
                    Location location = player.getLocation();
                    User user = usermapStorageUtil.getUserByOnlinePlayer(player);
                    if (user != null){
                        List<String> homesList = usermapStorageUtil.getHomeNamesListByUser(user);
                        if (!(player.hasPermission("epichomes.maxhomes.*")||player.hasPermission("epichomes.*")||player.isOp())){
                            if (!config.getBoolean("homes.permission-based-homes-max-amount.enabled")){
                                if (homesList.size() >= config.getInt("homes.default-max-homes")){
                                    player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("home-set-failed-max-homes-reached")
                                            .replace(PREFIX_PLACEHOLDER, prefix)));
                                    return true;
                                }
                            }else {
                                if (player.hasPermission("epichomes.maxhomes.group6")){
                                    int maxAmount = config.getInt("homes.permission-based-homes-max-amount.permission-group-list.group-6");
                                    if (homesList.size() >= config.getInt("homes.permission-based-homes-max-amount.permission-group-list.group-6")){
                                        player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("home-set-failed-max-tiered-reached")
                                                .replace(PREFIX_PLACEHOLDER, prefix).replace(LIMIT_PLACEHOLDER, String.valueOf(maxAmount))));
                                        return true;
                                    }
                                }else if (player.hasPermission("epichomes.maxhomes.group5")){
                                    int maxAmount = config.getInt("homes.permission-based-homes-max-amount.permission-group-list.group-5");
                                    if (homesList.size() >= config.getInt("homes.permission-based-homes-max-amount.permission-group-list.group-5")){
                                        player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("home-set-failed-max-tiered-reached")
                                                .replace(PREFIX_PLACEHOLDER, prefix).replace(LIMIT_PLACEHOLDER, String.valueOf(maxAmount))));
                                        return true;
                                    }
                                }else if (player.hasPermission("epichomes.maxhomes.group4")){
                                    int maxAmount = config.getInt("homes.permission-based-homes-max-amount.permission-group-list.group-4");
                                    if (homesList.size() >= config.getInt("homes.permission-based-homes-max-amount.permission-group-list.group-4")){
                                        player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("home-set-failed-max-tiered-reached")
                                                .replace(PREFIX_PLACEHOLDER, prefix).replace(LIMIT_PLACEHOLDER, String.valueOf(maxAmount))));
                                        return true;
                                    }
                                }else if (player.hasPermission("epichomes.maxhomes.group3")){
                                    int maxAmount = config.getInt("homes.permission-based-homes-max-amount.permission-group-list.group-3");
                                    if (homesList.size() >= config.getInt("homes.permission-based-homes-max-amount.permission-group-list.group-3")){
                                        player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("home-set-failed-max-tiered-reached")
                                                .replace(PREFIX_PLACEHOLDER, prefix).replace(LIMIT_PLACEHOLDER, String.valueOf(maxAmount))));
                                        return true;
                                    }
                                }else if (player.hasPermission("epichomes.maxhomes.group2")){
                                    int maxAmount = config.getInt("homes.permission-based-homes-max-amount.permission-group-list.group-2");
                                    if (homesList.size() >= config.getInt("homes.permission-based-homes-max-amount.permission-group-list.group-2")){
                                        player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("home-set-failed-max-tiered-reached")
                                                .replace(PREFIX_PLACEHOLDER, prefix).replace(LIMIT_PLACEHOLDER, String.valueOf(maxAmount))));
                                        return true;
                                    }
                                }else if (player.hasPermission("epichomes.maxhomes.group1")){
                                    int maxAmount = config.getInt("homes.permission-based-homes-max-amount.permission-group-list.group-1");
                                    if (homesList.size() >= config.getInt("homes.permission-based-homes-max-amount.permission-group-list.group-1")){
                                        player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("home-set-failed-max-tiered-reached")
                                                .replace(PREFIX_PLACEHOLDER, prefix).replace(LIMIT_PLACEHOLDER, String.valueOf(maxAmount))));
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
                                console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes-Debug: &aFired HomeSetEvent"));
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    public boolean setHomeSubCommand(CommandSender sender, String[] args, List<String> bannedNames) {
        if (sender instanceof Player){
            Player player = (Player) sender;
            String homeName = args[0];
            if (homeName != null){
                if (EpicHomes.getVersionCheckerUtils().getVersion() < 16){
                    if (!isNameValidBasicStringCheck(player, homeName, bannedNames)){
                        return true;
                    }
                }if (EpicHomes.getVersionCheckerUtils().getVersion() >= 16){
                    if (!isNameValidStringUtils(player, homeName, bannedNames)){
                        return true;
                    }
                }
                Location location = player.getLocation();
                User user = usermapStorageUtil.getUserByOnlinePlayer(player);
                if (user != null){
                    List<String> homesList = usermapStorageUtil.getHomeNamesListByUser(user);
                    if (!(player.hasPermission("epichomes.maxhomes.*")||player.hasPermission("epichomes.*")||player.isOp())){
                        if (!config.getBoolean("homes.permission-based-homes-max-amount.enabled")) {
                            if (homesList.size() >= config.getInt("homes.default-max-homes")){
                                player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("home-set-failed-max-homes-reached")
                                        .replace(PREFIX_PLACEHOLDER, prefix)));
                                return true;
                            }
                        }else {
                            if (player.hasPermission("epichomes.maxhomes.group6")){
                                int maxAmount = config.getInt("homes.permission-based-homes-max-amount.permission-group-list.group-6");
                                if (homesList.size() >= config.getInt("homes.permission-based-homes-max-amount.permission-group-list.group-6")){
                                    player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("home-set-failed-max-tiered-reached")
                                            .replace(PREFIX_PLACEHOLDER, prefix).replace(LIMIT_PLACEHOLDER, String.valueOf(maxAmount))));
                                    return true;
                                }
                            }else if (player.hasPermission("epichomes.maxhomes.group5")){
                                int maxAmount = config.getInt("homes.permission-based-homes-max-amount.permission-group-list.group-5");
                                if (homesList.size() >= config.getInt("homes.permission-based-homes-max-amount.permission-group-list.group-5")){
                                    player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("home-set-failed-max-tiered-reached")
                                            .replace(PREFIX_PLACEHOLDER, prefix).replace(LIMIT_PLACEHOLDER, String.valueOf(maxAmount))));
                                    return true;
                                }
                            }else if (player.hasPermission("epichomes.maxhomes.group4")){
                                int maxAmount = config.getInt("homes.permission-based-homes-max-amount.permission-group-list.group-4");
                                if (homesList.size() >= config.getInt("homes.permission-based-homes-max-amount.permission-group-list.group-4")){
                                    player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("home-set-failed-max-tiered-reached")
                                            .replace(PREFIX_PLACEHOLDER, prefix).replace(LIMIT_PLACEHOLDER, String.valueOf(maxAmount))));
                                    return true;
                                }
                            }else if (player.hasPermission("epichomes.maxhomes.group3")){
                                int maxAmount = config.getInt("homes.permission-based-homes-max-amount.permission-group-list.group-3");
                                if (homesList.size() >= config.getInt("homes.permission-based-homes-max-amount.permission-group-list.group-3")){
                                    player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("home-set-failed-max-tiered-reached")
                                            .replace(PREFIX_PLACEHOLDER, prefix).replace(LIMIT_PLACEHOLDER, String.valueOf(maxAmount))));
                                    return true;
                                }
                            }else if (player.hasPermission("epichomes.maxhomes.group2")){
                                int maxAmount = config.getInt("homes.permission-based-homes-max-amount.permission-group-list.group-2");
                                if (homesList.size() >= config.getInt("homes.permission-based-homes-max-amount.permission-group-list.group-2")){
                                    player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("home-set-failed-max-tiered-reached")
                                            .replace(PREFIX_PLACEHOLDER, prefix).replace(LIMIT_PLACEHOLDER, String.valueOf(maxAmount))));
                                    return true;
                                }
                            }else if (player.hasPermission("epichomes.maxhomes.group1")){
                                int maxAmount = config.getInt("homes.permission-based-homes-max-amount.permission-group-list.group-1");
                                if (homesList.size() >= config.getInt("homes.permission-based-homes-max-amount.permission-group-list.group-1")){
                                    player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("home-set-failed-max-tiered-reached")
                                            .replace(PREFIX_PLACEHOLDER, prefix).replace(LIMIT_PLACEHOLDER, String.valueOf(maxAmount))));
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
                            console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes-Debug: &aFired HomeSetEvent"));
                        }
                    }
                }
            }
        }
        return true;
    }

    private boolean isNameValidStringUtils(Player player, String homeName, List<String> bannedNames) {
        if (StringUtils.containsAnyIgnoreCase(homeName, ".")
                ||StringUtils.containsAnyIgnoreCase(homeName, "&")
                ||StringUtils.containsAnyIgnoreCase(homeName, "#")){
            player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("home-set-failed-invalid-name")
                    .replace(PREFIX_PLACEHOLDER, prefix)));
            return false;
        }
        if (bannedNames.contains(homeName)){
            player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("home-set-failed-name-not-allowed")
                    .replace(PREFIX_PLACEHOLDER, prefix)));
            return false;
        }
        return true;
    }

    private boolean isNameValidBasicStringCheck(Player player, String homeName, List<String> bannedNames) {
        if (homeName.toLowerCase().contains(".")||homeName.toLowerCase().contains("&")||homeName.toLowerCase().contains("#")){
            player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("home-set-failed-invalid-name")
                    .replace(PREFIX_PLACEHOLDER, prefix)));
            return false;
        }
        if (bannedNames.contains(homeName)){
            player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("home-set-failed-name-not-allowed")
                    .replace(PREFIX_PLACEHOLDER, prefix)));
            return false;
        }
        return true;
    }

    private static void fireHomeSetEvent(Player player, User user, String homeName, Location location) {
        HomeSetEvent homeSetEvent = new HomeSetEvent(player, user, homeName, location);
        Bukkit.getPluginManager().callEvent(homeSetEvent);
    }
}
