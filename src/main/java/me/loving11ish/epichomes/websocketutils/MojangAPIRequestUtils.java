package me.loving11ish.epichomes.websocketutils;

import com.github.lightlibs.simplehttpwrapper.SimpleHttpResponse;
import com.github.lightlibs.simplehttpwrapper.SimpleHttpWrapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.loving11ish.epichomes.utils.MessageUtils;

import java.io.IOException;

public class MojangAPIRequestUtils {

    public static boolean canGetOfflinePlayerData(String uuid, String playerName) throws IOException {
        SimpleHttpResponse response = SimpleHttpWrapper.get("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid, null);
        MessageUtils.sendDebugConsole("&ahttps request response code: " + response.getStatusCode());

        if (response.getStatusCode() / 100 == 4 || response.getStatusCode() == 204) {
            MessageUtils.sendDebugConsole("&aUnable to get offlinePlayerData");
            MessageUtils.sendDebugConsole("&aServer/network is running offline");
            return false;
        }

        else {
            MessageUtils.sendDebugConsole("&aSuccessfully got offlinePlayerData for :&e" + uuid);
            MessageUtils.sendDebugConsole("&aServer/network is running online");

            JsonObject object = (JsonObject) JsonParser.parseString(response.getData());
            return object.get("name").getAsString().equalsIgnoreCase(playerName);
        }
    }
}
