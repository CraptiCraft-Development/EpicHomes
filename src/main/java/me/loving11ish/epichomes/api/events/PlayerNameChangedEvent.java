package me.loving11ish.epichomes.api.events;

import me.loving11ish.epichomes.models.User;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerNameChangedEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final Player createdBy;
    private final User user;
    private final String lastPlayerName;
    private final String newPlayerName;

    public PlayerNameChangedEvent(Player createdBy, User user, String lastPlayerName, String newPlayerName) {
        this.createdBy = createdBy;
        this.user = user;
        this.lastPlayerName = lastPlayerName;
        this.newPlayerName = newPlayerName;
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

    public String getLastPlayerName() {
        return lastPlayerName;
    }

    public String getNewPlayerName() {
        return newPlayerName;
    }
}
