package me.loving11ish.epichomes.menusystem;

import me.loving11ish.epichomes.models.User;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PlayerMenuUtility {

    private final Player owner;
    public User user;
    public String homeName;
    public Location homeLocation;


    public PlayerMenuUtility(Player p) {
        this.owner = p;
    }

    public Player getOwner() {
        return owner;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getHomeName() {
        return homeName;
    }

    public void setHomeName(String homeName) {
        this.homeName = homeName;
    }

    public Location getHomeLocation() {
        return homeLocation;
    }

    public void setHomeLocation(Location homeLocation) {
        this.homeLocation = homeLocation;
    }
}
