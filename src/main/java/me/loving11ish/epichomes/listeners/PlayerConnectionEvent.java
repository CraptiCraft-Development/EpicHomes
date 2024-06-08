package me.loving11ish.epichomes.listeners;

import com.tcoded.folialib.FoliaLib;
import me.loving11ish.epichomes.EpicHomes;
import me.loving11ish.epichomes.api.events.AsyncPlayerNameChangedEvent;
import me.loving11ish.epichomes.api.events.AsyncUserAddedToUsermapEvent;
import me.loving11ish.epichomes.models.User;
import me.loving11ish.epichomes.utils.MessageUtils;
import me.loving11ish.epichomes.utils.UsermapStorageUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerConnectionEvent implements Listener {

    private final FoliaLib foliaLib = EpicHomes.getFoliaLib();

    private final UsermapStorageUtil usermapStorageUtil = EpicHomes.getPlugin().getUsermapStorageUtil();

    @EventHandler
    public void onPlayerConnection(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!usermapStorageUtil.isUserExisting(player)) {
            User user = usermapStorageUtil.addToUsermap(player);

            foliaLib.getImpl().runAsync((task) -> {
                fireUserAddedToUsermapEvent(player, user);
                MessageUtils.sendDebugConsole("&aFired AsyncUserAddedToUsermapEvent");
            });

            return;
        }
        if (usermapStorageUtil.hasNameChanged(player)) {
            User user = usermapStorageUtil.getUserByOnlinePlayer(player);
            String lastPlayerName = user.getLastKnownName();
            String newPlayerName = player.getName();

            foliaLib.getImpl().runAsync((task) -> {
                firePlayerNameChangedEvent(player, user, lastPlayerName, newPlayerName);
                MessageUtils.sendDebugConsole("&aFired AsyncPlayerNameChangedEvent");
            });

            usermapStorageUtil.updatePlayerName(player);
        }
    }

    private static void fireUserAddedToUsermapEvent(Player player, User user) {
        AsyncUserAddedToUsermapEvent asyncUserAddedToUsermapEvent = new AsyncUserAddedToUsermapEvent(player, user);
        Bukkit.getPluginManager().callEvent(asyncUserAddedToUsermapEvent);
    }

    private static void firePlayerNameChangedEvent(Player player, User user, String lastPlayerName, String newPlayerName) {
        AsyncPlayerNameChangedEvent asyncPlayerNameChangedEvent = new AsyncPlayerNameChangedEvent(player, user, lastPlayerName, newPlayerName);
        Bukkit.getPluginManager().callEvent(asyncPlayerNameChangedEvent);
    }
}
