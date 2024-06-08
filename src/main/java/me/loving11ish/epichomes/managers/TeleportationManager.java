package me.loving11ish.epichomes.managers;

import com.tcoded.folialib.wrapper.task.WrappedTask;
import me.loving11ish.epichomes.EpicHomes;
import me.loving11ish.epichomes.models.User;
import me.loving11ish.epichomes.utils.MessageUtils;

import java.util.HashMap;
import java.util.UUID;

public class TeleportationManager {

    private final HashMap<UUID, WrappedTask> teleportQueue = new HashMap<>();

    public boolean removeTimedTeleport(UUID uuid) {
        User user = EpicHomes.getPlugin().getUsermapStorageUtil().getUserByUUID(uuid);
        if (getTeleportQueue().containsKey(uuid)) {
            WrappedTask wrappedTask = getTeleportQueue().get(uuid);
            MessageUtils.sendDebugConsole("&aWrapped task: " + wrappedTask.toString());

            wrappedTask.cancel();
            MessageUtils.sendDebugConsole("&aWrapped task canceled");

            getTeleportQueue().remove(uuid);
            MessageUtils.sendDebugConsole("&aPlayer " + user.getLastKnownName() + " has had teleport canceled and removed from queue");
            return true;
        }
        return false;
    }

    public HashMap<UUID, WrappedTask> getTeleportQueue() {
        return teleportQueue;
    }
}
