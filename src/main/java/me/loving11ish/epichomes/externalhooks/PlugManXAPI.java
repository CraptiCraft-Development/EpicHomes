package me.loving11ish.epichomes.externalhooks;

import me.loving11ish.epichomes.EpicHomes;
import me.loving11ish.epichomes.utils.ColorUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;

public class PlugManXAPI {

    static ConsoleCommandSender console = Bukkit.getConsoleSender();

    static FileConfiguration config = EpicHomes.getPlugin().getConfig();

    public static boolean isPlugManXEnabled() {
        try {
            Class.forName("com.rylinaux.plugman.PlugMan");
            if (config.getBoolean("general.developer-debug-mode.enabled", false)){
                console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes-Debug: &aFound PlugManX main class at:"));
                console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes-Debug: &dcom.rylinaux.plugman.PlugMan"));
            }
            return true;
        }catch (ClassNotFoundException e){
            if (config.getBoolean("general.developer-debug-mode.enabled", false)){
                console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes-Debug: &aCould not find PlugManX main class at:"));
                console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes-Debug: &dcom.rylinaux.plugman.PlugMan"));
            }
            return false;
        }
    }
}
