package me.loving11ish.epichomes.listeners;

import me.loving11ish.epichomes.EpicHomes;
import me.loving11ish.epichomes.api.events.PlayerNameChangedEvent;
import me.loving11ish.epichomes.api.events.UserAddedToUsermapEvent;
import me.loving11ish.epichomes.models.User;
import me.loving11ish.epichomes.utils.ColorUtils;
import me.loving11ish.epichomes.utils.UsermapStorageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerConnectionEvent implements Listener {

    ConsoleCommandSender console = Bukkit.getConsoleSender();

    FileConfiguration config = EpicHomes.getPlugin().getConfig();

    private UsermapStorageUtil usermapStorageUtil = EpicHomes.getPlugin().usermapStorageUtil;

    @EventHandler
    public void onPlayerConnection(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!usermapStorageUtil.isUserExisting(player)){
            User user = usermapStorageUtil.addToUsermap(player);
            fireUserAddedToUsermapEvent(player, user);
            if (config.getBoolean("general.developer-debug-mode.enabled", false)){
                console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes-Debug: &aFired UserAddedToUsermapEvent"));
            }
            return;
        }
        if (usermapStorageUtil.hasNameChanged(player)){
            User user = usermapStorageUtil.getUserByOnlinePlayer(player);
            String lastPlayerName = user.getLastKnownName();
            String newPlayerName = player.getName();
            firePlayerNameChangedEvent(player, user, lastPlayerName, newPlayerName);
            if (config.getBoolean("general.developer-debug-mode.enabled", false)){
                console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes-Debug: &aFired PlayerNameChangedEvent"));
            }
            usermapStorageUtil.updatePlayerName(player);
        }
    }

    private static void fireUserAddedToUsermapEvent(Player player, User user) {
        UserAddedToUsermapEvent userAddedToUsermapEvent = new UserAddedToUsermapEvent(player, user);
        Bukkit.getPluginManager().callEvent(userAddedToUsermapEvent);
    }

    private static void firePlayerNameChangedEvent(Player player, User user, String lastPlayerName, String newPlayerName) {
        PlayerNameChangedEvent playerNameChangedEvent = new PlayerNameChangedEvent(player, user, lastPlayerName, newPlayerName);
        Bukkit.getPluginManager().callEvent(playerNameChangedEvent);
    }
}
