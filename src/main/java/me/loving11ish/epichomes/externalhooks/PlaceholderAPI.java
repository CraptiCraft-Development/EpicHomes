package me.loving11ish.epichomes.externalhooks;

import me.loving11ish.epichomes.utils.MessageUtils;

public class PlaceholderAPI {

    public static boolean isPlaceholderAPIEnabled() {
        try {
            Class.forName("me.clip.placeholderapi.PlaceholderAPIPlugin");
            MessageUtils.sendDebugConsole("&aFound PlaceholderAPI main class at:");
            MessageUtils.sendDebugConsole("&dme.clip.placeholderapi.PlaceholderAPIPlugin");
            return true;
        } catch (ClassNotFoundException e) {
            MessageUtils.sendDebugConsole("&cCould not find PlaceholderAPI main class at:");
            MessageUtils.sendDebugConsole("&dme.clip.placeholderapi.PlaceholderAPIPlugin");
            return false;
        }
    }
}
