package me.loving11ish.epichomes.api.events;

import me.loving11ish.epichomes.models.User;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class HomePreTeleportEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final Player createdBy;
    private final User user;
    private final String homeName;
    private final Location homeLocation;
    private final Location oldLocation;

    public HomePreTeleportEvent(Player createdBy, User user, String homeName, Location homeLocation, Location oldLocation) {
        this.createdBy = createdBy;
        this.user = user;
        this.homeName = homeName;
        this.homeLocation = homeLocation;
        this.oldLocation = oldLocation;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public Player getCreatedBy() {
        return createdBy;
    }

    public User getUser() {
        return user;
    }

    public String getHomeName() {
        return homeName;
    }

    public Location getHomeLocation() {
        return homeLocation;
    }

    public Location getOldLocation() {
        return oldLocation;
    }
}
