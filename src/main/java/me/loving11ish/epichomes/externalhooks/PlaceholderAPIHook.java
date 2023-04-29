package me.loving11ish.epichomes.externalhooks;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.loving11ish.epichomes.EpicHomes;
import me.loving11ish.epichomes.models.User;
import me.loving11ish.epichomes.utils.ColorUtils;
import me.loving11ish.epichomes.utils.UsermapStorageUtil;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PlaceholderAPIHook extends PlaceholderExpansion {

    FileConfiguration messagesConfig = EpicHomes.getPlugin().messagesFileManager.getMessagesConfig();

    private String prefix = messagesConfig.getString("global-prefix");
    private UsermapStorageUtil usermapStorageUtil = EpicHomes.getPlugin().usermapStorageUtil;

    @Override
    public @NotNull String getIdentifier() {
        return "epicHomes";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Loving11ish";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0-BETA-03";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer offlinePlayer, String params) {
        User user = usermapStorageUtil.getUserByOfflinePlayer(offlinePlayer);
        List<String> userHomesList = usermapStorageUtil.getHomeNamesListByUser(user);

        if (params.equalsIgnoreCase("pluginPrefix")){
            //%epicHomes_pluginPrefix%
            return ColorUtils.translateColorCodes(prefix);
        }

        if (params.equalsIgnoreCase("pluginVersion")){
            //%epicHomes_pluginVersion%
            return EpicHomes.getPlugin().getDescription().getVersion();
        }

        if (params.equalsIgnoreCase("pluginAuthor")){
            //%epicHomes_pluginAuthor%
            return EpicHomes.getPlugin().getDescription().getAuthors().get(0);
        }

        if (params.equalsIgnoreCase("totalPlayerJoined")){
            //%epicHomes_totalPlayerJoined%
            return String.valueOf(usermapStorageUtil.getUsermapStorage().size());
        }

        if (params.equalsIgnoreCase("userLastKnownName")){
            //%epicHomes_userLastKnownName%
            return user.getLastKnownName();
        }

        if (params.equalsIgnoreCase("userUUID")){
            //%epicHomes_userUUID%
            return user.getUserUUID();
        }

        if (params.equalsIgnoreCase("userHomeListSize")){
            //%epicHomes_userHomeListSize%
            if (userHomesList != null && !userHomesList.isEmpty()){
                return String.valueOf(userHomesList.size());
            }else {
                return "";
            }
        }
        return null;
    }
}
