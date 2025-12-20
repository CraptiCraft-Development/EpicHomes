package me.loving11ish.epichomes.externalhooks;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.loving11ish.epichomes.EpicHomes;
import me.loving11ish.epichomes.models.User;
import me.loving11ish.epichomes.utils.ColorUtils;
import me.loving11ish.epichomes.utils.MessageUtils;
import me.loving11ish.epichomes.utils.UsermapStorageUtil;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PlaceholderAPIHook extends PlaceholderExpansion {

    private final UsermapStorageUtil usermapStorageUtil = EpicHomes.getPlugin().getUsermapStorageUtil();

    @Override
    public @NotNull String getIdentifier() {
        return "epicHomes";
    }

    @Override
    public @NotNull String getAuthor() {
        return EpicHomes.getPlugin().getDescription().getAuthors().toString();
    }

    @Override
    public @NotNull String getVersion() {
        return EpicHomes.getPlugin().getDescription().getVersion();
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
            return ColorUtils.translateColorCodes(MessageUtils.prefix);
        }

        if (params.equalsIgnoreCase("pluginVersion")){
            //%epicHomes_pluginVersion%
            return EpicHomes.getPlugin().getDescription().getVersion();
        }

        if (params.equalsIgnoreCase("pluginAuthor")){
            //%epicHomes_pluginAuthor%
            return EpicHomes.getPlugin().getDescription().getAuthors().toString();
        }

        if (params.equalsIgnoreCase("baseServerVersion")){
            //%epicHomes_baseServerVersion%
            return String.valueOf(EpicHomes.getPlugin().getVersionCheckerUtils().getVersion());
        }

        if (params.equalsIgnoreCase("serverPackage")){
            //%epicHomes_serverPackage%
            return EpicHomes.getPlugin().getVersionCheckerUtils().getServerPackage();
        }

        if (params.equalsIgnoreCase("pluginEnabled")){
            //%epicHomes_pluginEnabled%
            return String.valueOf(EpicHomes.getPlugin().isPluginEnabled());
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

        if (params.equalsIgnoreCase("userHomeList")){
            //%epicHomes_userHomeList%
            if (userHomesList != null && !userHomesList.isEmpty()){
                return String.join(", ", userHomesList);
            }else {
                return "";
            }
        }
        return null;
    }
}
