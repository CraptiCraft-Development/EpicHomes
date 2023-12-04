package me.loving11ish.epichomes.commands.subcommands;

import me.loving11ish.epichomes.EpicHomes;
import me.loving11ish.epichomes.models.User;
import me.loving11ish.epichomes.utils.ColorUtils;
import me.loving11ish.epichomes.utils.UsermapStorageUtil;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;

public class ListSubCommand {

    private final FileConfiguration messagesConfig = EpicHomes.getPlugin().messagesFileManager.getMessagesConfig();

    private static final String PREFIX_PLACEHOLDER = "%PREFIX%";
    private static final String TARGET_PLACEHOLDER = "%TARGET%";
    private static final String HOME_NAME_PLACEHOLDER = "%HOME%";

    private final String prefix = messagesConfig.getString("global-prefix", "&f[&6Epic&bHomes&f]&r");
    private final UsermapStorageUtil usermapStorageUtil = EpicHomes.getPlugin().usermapStorageUtil;

    public boolean listSubCommand(CommandSender sender) {
        if (sender instanceof Player){
            Player player = (Player) sender;
            User user = usermapStorageUtil.getUserByOnlinePlayer(player);
            if (user != null){
                player.sendMessage(ColorUtils.translateColorCodes(buildHomeList(user).replace(PREFIX_PLACEHOLDER, prefix)));
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
                    player.sendMessage(ColorUtils.translateColorCodes(buildAdminHomeList(user)
                            .replace(PREFIX_PLACEHOLDER, prefix)
                            .replace(TARGET_PLACEHOLDER, args[1])));
                }else {
                    player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("homeadmin-unable-to-find-user")
                            .replace(PREFIX_PLACEHOLDER, prefix)
                            .replace(TARGET_PLACEHOLDER, args[1])));
                }
            }
        }
        return true;
    }

    private String buildHomeList(User user) {
        List<String> userHomesList = usermapStorageUtil.getHomeNamesListByUser(user);
        StringBuilder listString = new StringBuilder();
        listString.append(ColorUtils.translateColorCodes(messagesConfig.getString("list.header")));
        listString.append(ColorUtils.translateColorCodes(messagesConfig.getString("list.sub-header")));
        for (String home : userHomesList){
            listString.append(ColorUtils.translateColorCodes(messagesConfig.getString("list.home").replace(HOME_NAME_PLACEHOLDER, home)));
        }
        listString.append(ColorUtils.translateColorCodes(messagesConfig.getString("list.sub-footer")));
        listString.append(ColorUtils.translateColorCodes(messagesConfig.getString("list.footer")));

        return listString.toString();
    }

    private String buildAdminHomeList(User user) {
        List<String> userHomesList = usermapStorageUtil.getHomeNamesListByUser(user);
        StringBuilder listString = new StringBuilder();
        listString.append(ColorUtils.translateColorCodes(messagesConfig.getString("homeadmin-list.header")));
        listString.append(ColorUtils.translateColorCodes(messagesConfig.getString("homeadmin-list.sub-header")));
        for (String home : userHomesList){
            listString.append(ColorUtils.translateColorCodes(messagesConfig.getString("homeadmin-list.home").replace(HOME_NAME_PLACEHOLDER, home)));
        }
        listString.append(ColorUtils.translateColorCodes(messagesConfig.getString("homeadmin-list.sub-footer")));
        listString.append(ColorUtils.translateColorCodes(messagesConfig.getString("homeadmin-list.footer")));

        return listString.toString();
    }
}
