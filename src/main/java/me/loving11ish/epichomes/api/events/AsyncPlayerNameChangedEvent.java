package me.loving11ish.epichomes.api.events;

import me.loving11ish.epichomes.models.User;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class AsyncPlayerNameChangedEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final Player createdBy;
    private final User user;
    private final String lastPlayerName;
    private final String newPlayerName;

    /**
     * This event is called when a player changes their name asynchronously.
     * @param createdBy The player who created the event.
     * @param user The user who changed their name.
     * @param lastPlayerName The last name of the player.
     * @param newPlayerName The new name of the player.
     */
    public AsyncPlayerNameChangedEvent(Player createdBy, User user, String lastPlayerName, String newPlayerName) {
        super(true);
        this.createdBy = createdBy;
        this.user = user;
        this.lastPlayerName = lastPlayerName;
        this.newPlayerName = newPlayerName;
    }

    /**
     * @return Returns the handler list.
     */
    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    /**
     * @return Returns the player who created the event.
     */
    public Player getCreatedBy() {
        return createdBy;
    }

    /**
     * @return Returns the user who changed their name.
     */
    public User getUser() {
        return user;
    }

    /**
     * @return Returns the last name of the player.
     */
    public String getLastPlayerName() {
        return lastPlayerName;
    }

    /**
     * @return Returns the new name of the player.
     */
    public String getNewPlayerName() {
        return newPlayerName;
    }
}
