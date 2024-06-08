package me.loving11ish.epichomes.api.events;

import me.loving11ish.epichomes.models.User;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class AsyncHomeDeleteEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final Player createdBy;
    private final User user;
    private final String homeName;

    /**
     * This event is called when a player deletes a home asynchronously.
     * @param createdBy The player who created the event.
     * @param user The user who owns the home.
     * @param homeName The name of the home that was deleted.
     */
    public AsyncHomeDeleteEvent(Player createdBy, User user, String homeName) {
        super(true);
        this.createdBy = createdBy;
        this.user = user;
        this.homeName = homeName;
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
     * @return Returns the user who owns the home.
     */
    public User getUser() {
        return user;
    }

    /**
     * @return Returns the name of the home that was deleted.
     */
    public String getHomeName() {
        return homeName;
    }
}
