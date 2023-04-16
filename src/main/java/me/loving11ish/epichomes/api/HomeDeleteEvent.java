package me.loving11ish.epichomes.api;

import me.loving11ish.epichomes.models.User;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class HomeDeleteEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final Player createdBy;
    private final User user;
    private final String homeName;

    public HomeDeleteEvent(Player createdBy, User user, String homeName) {
        this.createdBy = createdBy;
        this.user = user;
        this.homeName = homeName;
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
}
