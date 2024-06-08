package me.loving11ish.epichomes.api.events;

import me.loving11ish.epichomes.models.User;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class AsyncUserAddedToUsermapEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final Player createdBy;
    private final User user;

    /**
     * This event is called when a player is added to the usermap asynchronously.
     * @param createdBy The player who created the event.
     * @param user The user who was added to the usermap.
     */
    public AsyncUserAddedToUsermapEvent(Player createdBy, User user) {
        super(true);
        this.createdBy = createdBy;
        this.user = user;
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
     * @return Returns the user who was added to the usermap.
     */
    public User getUser() {
        return user;
    }
}
