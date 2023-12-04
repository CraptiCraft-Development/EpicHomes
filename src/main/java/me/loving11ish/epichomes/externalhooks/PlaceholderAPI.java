package me.loving11ish.epichomes.externalhooks;

import me.loving11ish.epichomes.EpicHomes;
import me.loving11ish.epichomes.utils.ColorUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;

public class PlaceholderAPI {

    private static final ConsoleCommandSender console = Bukkit.getConsoleSender();

    private static final FileConfiguration config = EpicHomes.getPlugin().getConfig();

    public static boolean isPlaceholderAPIEnabled() {
        try {
            Class.forName("me.clip.placeholderapi.PlaceholderAPIPlugin");
            if (config.getBoolean("general.developer-debug-mode.enabled", false)){
                console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes-Debug: &aFound PlaceholderAPI main class at:"));
                console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes-Debug: &dme.clip.placeholderapi.PlaceholderAPIPlugin"));
            }
            return true;
        }catch (ClassNotFoundException e){
            if (config.getBoolean("general.developer-debug-mode.enabled", false)){
                console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes-Debug: &aCould not find PlaceholderAPI main class at:"));
                console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes-Debug: &dme.clip.placeholderapi.PlaceholderAPIPlugin"));
            }
            return false;
        }
    }
}
