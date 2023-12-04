package me.loving11ish.epichomes.listeners;

import me.loving11ish.epichomes.EpicHomes;
import me.loving11ish.epichomes.utils.ColorUtils;
import me.loving11ish.epichomes.websocketutils.MojangAPIRequestUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.io.IOException;
import java.util.UUID;

public class PlayerPreConnectionEvent implements Listener {

    private final ConsoleCommandSender console = Bukkit.getConsoleSender();

    private final FileConfiguration config = EpicHomes.getPlugin().getConfig();

    private boolean firstPlayerConnected = true;

    @EventHandler
    public void onPlayerPreConnect(AsyncPlayerPreLoginEvent event) {
        if (firstPlayerConnected){
            try {
                UUID uuid = event.getUniqueId();
                if (MojangAPIRequestUtils.canGetOfflinePlayerData(uuid.toString(), event.getName())){
                    EpicHomes.setOnlineMode(true);
                    if (EpicHomes.getVersionCheckerUtils().getVersion() >= 14){
                        EpicHomes.setGUIEnabled(config.getBoolean("gui-system.use-global-gui.enabled"));
                        if (EpicHomes.isGUIEnabled()){
                            console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &3Global GUI system enabled!"));
                        }else {
                            console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &c&lGlobal GUI system disabled!"));
                        }
                    }else {
                        EpicHomes.setGUIEnabled(false);
                        console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &cYour current server version does not support PersistentDataContainers!"));
                        console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &c&lGlobal GUI system disabled!"));
                    }
                }else if (!MojangAPIRequestUtils.canGetOfflinePlayerData(uuid.toString(), event.getName())){
                    EpicHomes.setOnlineMode(false);
                    if (EpicHomes.getVersionCheckerUtils().getVersion() >= 14){
                        EpicHomes.setGUIEnabled(false);
                        console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &c&lGlobal GUI system disabled!"));
                    }else {
                        EpicHomes.setGUIEnabled(false);
                        console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &cYour current server version does not support PersistentDataContainers!"));
                        console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &c&lGlobal GUI system disabled!"));
                    }
                    console.sendMessage(ColorUtils.translateColorCodes("&4-------------------------------------------"));
                    console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &4This plugin is only supported on online servers or servers running in an online network situation!"));
                    console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &4Some features may behave incorrectly or may be broken completely!"));
                    console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &4Please set &e'online-mode=true' &4in &e'server.properties'"));
                    console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &4Or ensure your proxy setup is correct and your proxy is set to online mode!"));
                    console.sendMessage(ColorUtils.translateColorCodes("&4-------------------------------------------"));
                }
            }catch (IOException e){
                console.sendMessage(ColorUtils.translateColorCodes("&4-------------------------------------------"));
                console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &4Unable to reach Mojang player databaseutils!"));
                console.sendMessage(ColorUtils.translateColorCodes("&6ClansLite: &4See stacktrace below for more details."));
                e.printStackTrace();
                console.sendMessage(ColorUtils.translateColorCodes("&4-------------------------------------------"));
            }
            firstPlayerConnected = false;
        }
    }
}
