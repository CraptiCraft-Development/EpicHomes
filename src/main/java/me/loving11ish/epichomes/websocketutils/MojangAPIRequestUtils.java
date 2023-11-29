package me.loving11ish.epichomes.websocketutils;

import com.github.lightlibs.simplehttpwrapper.SimpleHttpResponse;
import com.github.lightlibs.simplehttpwrapper.SimpleHttpWrapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.loving11ish.epichomes.EpicHomes;
import me.loving11ish.epichomes.utils.ColorUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.IOException;

public class MojangAPIRequestUtils {

    static ConsoleCommandSender console = Bukkit.getConsoleSender();

    static FileConfiguration config = EpicHomes.getPlugin().getConfig();

    public static boolean canGetOfflinePlayerData(String uuid, String playerName) throws IOException {
        SimpleHttpResponse response = SimpleHttpWrapper.get("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid, null);
        if (config.getBoolean("general.developer-debug-mode.enabled", false)){
            console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes-Debug: &ahttps request response code: &e" + response.getStatusCode()));
        }
        if (response.getStatusCode()/100 == 4 || response.getStatusCode() == 204){
            if (config.getBoolean("general.developer-debug-mode.enabled", false)){
                console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes-Debug: &aUnable to get offlinePlayerData"));
                console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes-Debug: &aServer/network is running offline"));
            }
            return false;
        }else {
            if (config.getBoolean("general.developer-debug-mode.enabled", false)){
                console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes-Debug: &aSuccessfully got offlinePlayerData for :&e" + uuid));
                console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes-Debug: &aServer/network is running online"));
            }
            JsonObject object = (JsonObject) JsonParser.parseString(response.getData());
            return object.get("name").getAsString().equalsIgnoreCase(playerName);
        }
    }
}
