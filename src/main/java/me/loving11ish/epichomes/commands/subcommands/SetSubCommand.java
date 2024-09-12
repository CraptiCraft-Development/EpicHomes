package me.loving11ish.epichomes.commands.subcommands;

import com.tcoded.folialib.FoliaLib;
import me.loving11ish.epichomes.EpicHomes;
import me.loving11ish.epichomes.api.events.AsyncHomeSetEvent;
import me.loving11ish.epichomes.models.User;
import me.loving11ish.epichomes.utils.MessageUtils;
import me.loving11ish.epichomes.utils.UsermapStorageUtil;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public class SetSubCommand {

    private final FoliaLib foliaLib = EpicHomes.getFoliaLib();
    private final UsermapStorageUtil usermapStorageUtil = EpicHomes.getPlugin().getUsermapStorageUtil();

    private static final String HOME_NAME_PLACEHOLDER = "%HOME%";
    private static final String LIMIT_PLACEHOLDER = "%LIMIT%";

    public boolean setSubCommand(CommandSender sender, String[] args, List<String> bannedNames) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length >= 2) {
                String homeName = args[1];
                if (setUserHome(bannedNames, player, homeName)) return true;
            }
        }
        return true;
    }

    public boolean setHomeSubCommand(CommandSender sender, String[] args, List<String> bannedNames) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            String homeName = args[0];
            if (setUserHome(bannedNames, player, homeName)) return true;
        }
        return true;
    }

    private boolean setUserHome(List<String> bannedNames, Player player, String homeName) {
        if (homeName != null) {

            if (isNameInvalidStringUtils(player, homeName, bannedNames)) {
                return true;
            }

            Location location = player.getLocation();
            User user = usermapStorageUtil.getUserByOnlinePlayer(player);
            if (user != null) {

                List<String> homesList = usermapStorageUtil.getHomeNamesListByUser(user);

                if (!(player.hasPermission("epichomes.maxhomes.*") || player.hasPermission("epichomes.*") || player.isOp())) {

                    if (!EpicHomes.getPlugin().getConfigManager().isUseTieredHomeLimit()) {

                        if (homesList.size() >= EpicHomes.getPlugin().getConfigManager().getDefaultHomeLimit()) {
                            MessageUtils.sendPlayer(player, EpicHomes.getPlugin().getMessagesManager().getHomeSetFailedMaxHomes());
                            return true;
                        }
                    }

                    else {
                        if (getTieredLimitCheck(player, homesList)) return true;
                    }
                }
                if (usermapStorageUtil.addHomeToUser(user, homeName, location)) {
                    MessageUtils.sendPlayer(player, EpicHomes.getPlugin().getMessagesManager().getHomeSetSuccess()
                            .replace(HOME_NAME_PLACEHOLDER, homeName));

                    foliaLib.getScheduler().runAsync((task) -> {
                        fireHomeSetEvent(player, user, homeName, location);
                        MessageUtils.sendDebugConsole("&aFired AsyncHomeSetEvent");
                    });
                }
            }
        }
        return false;
    }

    private boolean isNameInvalidStringUtils(Player player, String homeName, List<String> bannedNames) {
        if (StringUtils.containsAnyIgnoreCase(homeName, ".")
                || StringUtils.containsAnyIgnoreCase(homeName, "&")
                || StringUtils.containsAnyIgnoreCase(homeName, "#")) {
            MessageUtils.sendPlayer(player, EpicHomes.getPlugin().getMessagesManager().getHomeSetFailInvalidName());

            return true;
        }
        if (bannedNames.contains(homeName)) {
            MessageUtils.sendPlayer(player, EpicHomes.getPlugin().getMessagesManager().getHomeSetFailedIllegalName());
            return true;
        }
        return false;
    }

    private boolean getTieredLimitCheck(Player player, List<String> homesList) {
        for (Map<?, ?> groupMap : EpicHomes.getPlugin().getConfigManager().getTieredHomesMaxAmountGroups()) {
            String group = (String) groupMap.get("group");
            int maxAmount = (Integer) groupMap.get("maxAmount");

            if (player.hasPermission(group)) {
                if (homesList.size() >= maxAmount) {
                    MessageUtils.sendPlayer(player, EpicHomes.getPlugin().getMessagesManager().getHomeSetFailedTieredMaxHomes()
                            .replace(LIMIT_PLACEHOLDER, String.valueOf(maxAmount)));
                    return true;
                }
                break;
            }
        }
        return false;
    }

    private static void fireHomeSetEvent(Player player, User user, String homeName, Location location) {
        AsyncHomeSetEvent asyncHomeSetEvent = new AsyncHomeSetEvent(player, user, homeName, location);
        Bukkit.getPluginManager().callEvent(asyncHomeSetEvent);
    }
}
