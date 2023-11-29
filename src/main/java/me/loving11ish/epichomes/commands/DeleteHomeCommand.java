package me.loving11ish.epichomes.commands;

import me.loving11ish.epichomes.EpicHomes;
import me.loving11ish.epichomes.commands.subcommands.DeleteSubCommand;
import me.loving11ish.epichomes.menusystem.PlayerMenuUtility;
import me.loving11ish.epichomes.menusystem.menus.DeleteSingleGUI;
import me.loving11ish.epichomes.menusystem.paginatedmenus.DeleteHomesListGUI;
import me.loving11ish.epichomes.models.User;
import me.loving11ish.epichomes.utils.ColorUtils;
import me.loving11ish.epichomes.utils.UsermapStorageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class DeleteHomeCommand implements CommandExecutor, TabCompleter {

    ConsoleCommandSender console = Bukkit.getConsoleSender();

    FileConfiguration messagesConfig = EpicHomes.getPlugin().messagesFileManager.getMessagesConfig();
    private UsermapStorageUtil usermapStorageUtil = EpicHomes.getPlugin().usermapStorageUtil;

    private String prefix = messagesConfig.getString("global-prefix", "&f[&6Epic&bHomes&f]&r");
    private static final String PREFIX_PLACEHOLDER = "%PREFIX%";
    private static final String HOME_PLACEHOLDER = "%HOME%";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player){
            Player player = (Player) sender;
            User user = usermapStorageUtil.getUserByOnlinePlayer(player);
            if (args.length < 1){
                if (EpicHomes.isGUIEnabled()){
                    new DeleteHomesListGUI(EpicHomes.getPlayerMenuUtility(player)).open();
                }else {
                    player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("incorrect-delhome-command-usage.line-1").replace(PREFIX_PLACEHOLDER, prefix)));
                    player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("incorrect-delhome-command-usage.line-2").replace(PREFIX_PLACEHOLDER, prefix)));
                }
                return true;
            }else {
                if (args[0] != null){
                    if (EpicHomes.isGUIEnabled()){
                        PlayerMenuUtility playerMenuUtility = EpicHomes.getPlayerMenuUtility(player);
                        playerMenuUtility.setUser(user);
                        playerMenuUtility.setHomeName(args[0]);
                        Location location = usermapStorageUtil.getHomeLocationByHomeName(user, args[0]);
                        if (location != null){
                            playerMenuUtility.setHomeLocation(location);
                            new DeleteSingleGUI(playerMenuUtility).open();
                        }else {
                            player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("home-name-does-not-exist")
                                    .replace(PREFIX_PLACEHOLDER, prefix)
                                    .replace(HOME_PLACEHOLDER, args[0])));
                        }
                        return true;
                    }else {
                        return new DeleteSubCommand().deleteHomeSubCommand(sender, args);
                    }
                }else {
                    player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("incorrect-delhome-command-usage.line-2").replace(PREFIX_PLACEHOLDER, prefix)));
                    return true;
                }
            }
        }else {
            console.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("incorrect-command-usage")
                    .replace(PREFIX_PLACEHOLDER, prefix)));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        User user = usermapStorageUtil.getUserByOnlinePlayer(player);
        List<String> homesList = usermapStorageUtil.getHomeNamesListByUser(user);

        List<String> arguments = new ArrayList<>(homesList);
        List<String> result = new ArrayList<>();

        if (args.length == 1){
            for (String a : arguments){
                if (a.toLowerCase().startsWith(args[0].toLowerCase())){
                    result.add(a);
                }
            }
            return result;
        }
        return null;
    }
}
