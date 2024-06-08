package me.loving11ish.epichomes.api.events;

import me.loving11ish.epichomes.models.User;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class AsyncHomeSetEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final Player createdBy;
    private final User user;
    private final String homeName;
    private final Location homeLocation;

    /**
     * This event is called when a player sets a home asynchronously.
     * @param createdBy The player who created the event.
     * @param user The user who owns the home.
     * @param homeName The name of the home that was set.
     * @param homeLocation The location of the home that was set.
     */
    public AsyncHomeSetEvent(Player createdBy, User user, String homeName, Location homeLocation) {
        super(true);
        this.createdBy = createdBy;
        this.user = user;
        this.homeName = homeName;
        this.homeLocation = homeLocation;
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
     * @return Returns the name of the home that was set.
     */
    public String getHomeName() {
        return homeName;
    }

    /**
     * @return Returns the location of the home that was set.
     */
    public Location getHomeLocation() {
        return homeLocation;
    }
}
