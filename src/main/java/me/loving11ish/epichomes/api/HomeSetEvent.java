package me.loving11ish.epichomes.api;

import me.loving11ish.epichomes.models.User;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class HomeSetEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final Player createdBy;
    private final User user;
    private final String homeName;
    private final Location homeLocation;

    public HomeSetEvent(Player createdBy, User user, String homeName, Location homeLocation) {
        this.createdBy = createdBy;
        this.user = user;
        this.homeName = homeName;
        this.homeLocation = homeLocation;
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
}
