package me.loving11ish.epichomes.listeners;

import me.loving11ish.epichomes.EpicHomes;
import me.loving11ish.epichomes.utils.MessageUtils;
import me.loving11ish.epichomes.websocketutils.MojangAPIRequestUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.io.IOException;
import java.util.UUID;

public class PlayerPreConnectionEvent implements Listener {

    private boolean firstPlayerConnected = true;

    @EventHandler
    public void onPlayerPreConnect(AsyncPlayerPreLoginEvent event) {
        if (firstPlayerConnected) {
            try {
                UUID uuid = event.getUniqueId();
                if (MojangAPIRequestUtils.canGetOfflinePlayerData(uuid.toString(), event.getName())) {
                    EpicHomes.getPlugin().setOnlineMode(true);
                    MessageUtils.sendDebugConsole("Server is running in online mode!");
                } else if (!MojangAPIRequestUtils.canGetOfflinePlayerData(uuid.toString(), event.getName())) {
                    EpicHomes.getPlugin().setOnlineMode(false);
                    MessageUtils.sendDebugConsole("Server is running in offline mode!");
                }
            } catch (IOException e) {
                MessageUtils.sendConsole("severe", "&4-------------------------------------------");
                MessageUtils.sendConsole("severe", "&4Unable to reach Mojang OfflinePlayer database");
                MessageUtils.sendConsole("severe", "&4See stacktrace below for more details.");
                e.printStackTrace();
                MessageUtils.sendConsole("severe", "&4-------------------------------------------");
            }
            firstPlayerConnected = false;
        }
    }
}
