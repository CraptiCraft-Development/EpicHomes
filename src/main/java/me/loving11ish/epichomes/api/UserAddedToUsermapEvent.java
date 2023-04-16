package me.loving11ish.epichomes.api;

import me.loving11ish.epichomes.models.User;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class UserAddedToUsermapEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final Player createdBy;
    private final User user;

    public UserAddedToUsermapEvent(Player createdBy, User user) {
        this.createdBy = createdBy;
        this.user = user;
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
}
