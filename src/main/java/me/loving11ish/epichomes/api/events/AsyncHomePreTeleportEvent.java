package me.loving11ish.epichomes.api.events;

import me.loving11ish.epichomes.models.User;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class AsyncHomePreTeleportEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final Player createdBy;
    private final User user;
    private final String homeName;
    private final Location homeLocation;
    private final Location oldLocation;

    /**
     * This event is called when a player teleports to a home asynchronously.
     * @param createdBy The player who created the event.
     * @param user The user who owns the home.
     * @param homeName The name of the home that was teleported to.
     * @param homeLocation The location of the home that was teleported to.
     * @param oldLocation The location of the player before teleporting.
     */
    public AsyncHomePreTeleportEvent(Player createdBy, User user, String homeName, Location homeLocation, Location oldLocation) {
        super(true);
        this.createdBy = createdBy;
        this.user = user;
        this.homeName = homeName;
        this.homeLocation = homeLocation;
        this.oldLocation = oldLocation;
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
     * @return Returns the name of the home that was teleported to.
     */
    public String getHomeName() {
        return homeName;
    }

    /**
     * @return Returns the location of the home that was teleported to.
     */
    public Location getHomeLocation() {
        return homeLocation;
    }

    /**
     * @return Returns the location of the player before teleporting.
     */
    public Location getOldLocation() {
        return oldLocation;
    }
}
