package me.loving11ish.epichomes.utils;

import com.tcoded.folialib.FoliaLib;

import com.tcoded.folialib.wrapper.task.WrappedTask;
import io.papermc.lib.PaperLib;
import me.loving11ish.epichomes.EpicHomes;
import me.loving11ish.epichomes.api.events.AsyncHomeTeleportEvent;
import me.loving11ish.epichomes.models.User;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

public class TeleportationUtils {

    private final FoliaLib foliaLib = EpicHomes.getFoliaLib();

    private WrappedTask wrappedTask;

    private static final String HOME_NAME_PLACEHOLDER = "%HOME%";

    public void teleportPlayerAsync(Player player, Location location, String homeName) {
        User user = EpicHomes.getPlugin().getUsermapStorageUtil().getUserByOnlinePlayer(player);
        PaperLib.teleportAsync(player, location);

        foliaLib.getScheduler().runAsync((task) -> {
            fireAsyncHomeTeleportEvent(player, user, homeName, location);
            MessageUtils.sendDebugConsole("&aFired AsyncHomeTeleportEvent in async mode");
        });

        MessageUtils.sendPlayer(player, EpicHomes.getPlugin().getMessagesManager().getNonTimedCompleteTP()
                .replace(HOME_NAME_PLACEHOLDER, homeName));
    }

    public void teleportPlayerAsyncTimed(Player player, Location location, String homeName) {
        User user = EpicHomes.getPlugin().getUsermapStorageUtil().getUserByOnlinePlayer(player);
        MessageUtils.sendPlayer(player, EpicHomes.getPlugin().getMessagesManager().getTimedBeginTP()
                .replace(HOME_NAME_PLACEHOLDER, homeName));

        wrappedTask = foliaLib.getScheduler().runTimerAsync(new Runnable() {
            int time = EpicHomes.getPlugin().getConfigManager().getTeleportDelayTime();

            @Override
            public void run() {

                if (!EpicHomes.getPlugin().getTeleportationManager().getTeleportQueue().containsKey(player.getUniqueId())) {
                    EpicHomes.getPlugin().getTeleportationManager().getTeleportQueue().put(player.getUniqueId(), getWrappedTask());
                    MessageUtils.sendDebugConsole("&aPlayer " + player.getName() + " has been added to teleport queue");
                }
                if (time == 0) {
                    EpicHomes.getPlugin().getTeleportationManager().getTeleportQueue().remove(player.getUniqueId());
                    MessageUtils.sendDebugConsole("&aPlayer " + player.getName() + " has been removed from the teleport queue");

                    PaperLib.teleportAsync(player, location);
                    MessageUtils.sendPlayer(player, EpicHomes.getPlugin().getMessagesManager().getTimedCompleteTP()
                            .replace(HOME_NAME_PLACEHOLDER, homeName));

                    foliaLib.getScheduler().runAsync((task) -> {
                        fireAsyncHomeTeleportEvent(player, user, homeName, location);
                        MessageUtils.sendDebugConsole("&aFired AsyncHomeTeleportEvent in async mode");
                    });

                    getWrappedTask().cancel();
                    MessageUtils.sendDebugConsole("&aWrapped task: " + getWrappedTask().toString());
                    MessageUtils.sendDebugConsole("&ateleportPlayerAsyncTimed task canceled");

                    return;
                } else {
                    time--;
                    MessageUtils.sendDebugConsole("&ateleportPlayerAsyncTimed task running");
                    MessageUtils.sendDebugConsole("&aWrapped task: " + getWrappedTask().toString());
                }
            }
        }, 0, 1L, TimeUnit.SECONDS);
    }

    public WrappedTask getWrappedTask() {
        return wrappedTask;
    }

    private void fireAsyncHomeTeleportEvent(Player player, User user, String homeName, Location homeLocation) {
        AsyncHomeTeleportEvent asyncHomeTeleportEvent = new AsyncHomeTeleportEvent(player, user, homeName, homeLocation);
        Bukkit.getPluginManager().callEvent(asyncHomeTeleportEvent);
    }
}
