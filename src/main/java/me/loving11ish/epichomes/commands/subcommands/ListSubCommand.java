package me.loving11ish.epichomes.commands.subcommands;

import me.loving11ish.epichomes.EpicHomes;
import me.loving11ish.epichomes.models.User;
import me.loving11ish.epichomes.utils.ColorUtils;
import me.loving11ish.epichomes.utils.UsermapStorageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;

public class ListSubCommand {

    FileConfiguration messagesConfig = EpicHomes.getPlugin().messagesFileManager.getMessagesConfig();

    private static final String PREFIX_PLACEHOLDER = "%PREFIX%";
    private static final String HOME_NAME_PLACEHOLDER = "%HOME%";

    private String prefix = messagesConfig.getString("global-prefix");
    private UsermapStorageUtil usermapStorageUtil = EpicHomes.getPlugin().usermapStorageUtil;

    public boolean listSubCommand(CommandSender sender) {
        if (sender instanceof Player){
            Player player = (Player) sender;
            User user = usermapStorageUtil.getUserByOnlinePlayer(player);
            if (user != null){
                List<String> userHomesList = usermapStorageUtil.getHomeNamesListByUser(user);
                StringBuilder listString = new StringBuilder();
                listString.append(ColorUtils.translateColorCodes(messagesConfig.getString("list.header")));
                listString.append(ColorUtils.translateColorCodes(messagesConfig.getString("list.sub-header")));
                for (String home : userHomesList){
                    listString.append(ColorUtils.translateColorCodes(messagesConfig.getString("list.home").replace(HOME_NAME_PLACEHOLDER, home)));
                }
                listString.append(ColorUtils.translateColorCodes(messagesConfig.getString("list.sub-footer")));
                listString.append(ColorUtils.translateColorCodes(messagesConfig.getString("list.footer")));

                player.sendMessage(ColorUtils.translateColorCodes(listString.toString().replace(PREFIX_PLACEHOLDER, prefix)));
            }
        }
        return true;
    }

}
