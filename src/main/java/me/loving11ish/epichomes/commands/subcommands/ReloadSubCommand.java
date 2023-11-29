package me.loving11ish.epichomes.commands.subcommands;

import com.tcoded.folialib.FoliaLib;
import me.loving11ish.epichomes.EpicHomes;
import me.loving11ish.epichomes.utils.ColorUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class ReloadSubCommand {

    ConsoleCommandSender console = Bukkit.getConsoleSender();

    FoliaLib foliaLib = EpicHomes.getFoliaLib();
    FileConfiguration messagesConfig = EpicHomes.getPlugin().messagesFileManager.getMessagesConfig();

    private static final String PREFIX_PLACEHOLDER = "%PREFIX%";
    private String prefix = messagesConfig.getString("global-prefix", "&f[&6Epic&bHomes&f]&r");
    private ArrayList<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());

    public boolean reloadSubCommand(CommandSender sender) {
        if (sender instanceof Player){
            Player player = (Player) sender;
            player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("plugin-reload-start").replace(PREFIX_PLACEHOLDER, prefix)));
            console.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("plugin-reload-start").replace(PREFIX_PLACEHOLDER, prefix)));
            for (Player p : onlinePlayers){
                if (p.getName().equalsIgnoreCase(player.getName())){
                    continue;
                }
                if (!onlinePlayers.isEmpty()){
                    p.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("plugin-reload-broadcast-start").replace(PREFIX_PLACEHOLDER, prefix)));
                }
            }
            EpicHomes.getPlugin().onDisable();
            foliaLib.getImpl().runLater(() ->
                    Bukkit.getPluginManager().getPlugin("EpicHomes").onEnable(),5L, TimeUnit.SECONDS);
            foliaLib.getImpl().runLater(() -> {
                EpicHomes.getPlugin().reloadConfig();
                EpicHomes.getPlugin().messagesFileManager.reloadMessagesConfig();
                player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("plugin-reload-complete").replace(PREFIX_PLACEHOLDER, prefix)));
                console.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("plugin-reload-complete").replace(PREFIX_PLACEHOLDER, prefix)));
                for (Player p : onlinePlayers){
                    if (p.getName().equalsIgnoreCase(player.getName())){
                        continue;
                    }
                    if (!onlinePlayers.isEmpty()){
                        p.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("plugin-reload-complete").replace(PREFIX_PLACEHOLDER, prefix)));
                    }
                }
            }, 5L, TimeUnit.SECONDS);
            return true;
        }
        return true;
    }

    public boolean reloadSubCommand() {
        console.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("plugin-reload-start").replace(PREFIX_PLACEHOLDER, prefix)));
        for (Player p : onlinePlayers){
            if (!onlinePlayers.isEmpty()){
                p.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("plugin-reload-broadcast-start").replace(PREFIX_PLACEHOLDER, prefix)));
            }
        }
        EpicHomes.getPlugin().onDisable();
        foliaLib.getImpl().runLater(() ->
                Bukkit.getPluginManager().getPlugin("EpicHomes").onEnable(),5L, TimeUnit.SECONDS);
        foliaLib.getImpl().runLater(() -> {
            EpicHomes.getPlugin().reloadConfig();
            EpicHomes.getPlugin().messagesFileManager.reloadMessagesConfig();
            console.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("plugin-reload-complete").replace(PREFIX_PLACEHOLDER, prefix)));
            for (Player p : onlinePlayers){
                if (!onlinePlayers.isEmpty()){
                    p.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("plugin-reload-complete").replace(PREFIX_PLACEHOLDER, prefix)));
                }
            }
        }, 5L, TimeUnit.SECONDS);
        return true;
    }
}
