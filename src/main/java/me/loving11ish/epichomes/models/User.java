package me.loving11ish.epichomes.models;

import org.bukkit.Location;

import java.util.HashMap;

public class User {

    private String userUUID;
    private String lastKnownName;
    private HashMap<String, Location> homesList = new HashMap<>();

    public User(String uuid, String name) {
        userUUID = uuid;
        lastKnownName = name;
    }

    public String getUserUUID() {
        return userUUID;
    }

    public void setUserUUID(String userUUID) {
        this.userUUID = userUUID;
    }

    public String getLastKnownName() {
        return lastKnownName;
    }

    public void setLastKnownName(String lastKnownName) {
        this.lastKnownName = lastKnownName;
    }

    public HashMap<String, Location> getHomesList() {
        return homesList;
    }

    public void setHomesList(HashMap<String, Location> homesList) {
        this.homesList = homesList;
    }
}
