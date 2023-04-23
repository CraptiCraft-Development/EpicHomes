package me.loving11ish.epichomes.commands;

import me.loving11ish.epichomes.EpicHomes;
import me.loving11ish.epichomes.commands.subcommands.DeleteSubCommand;
import me.loving11ish.epichomes.menusystem.PlayerMenuUtility;
import me.loving11ish.epichomes.menusystem.menus.DeleteSingleGUI;
import me.loving11ish.epichomes.menusystem.paginatedmenus.DeleteHomesListGUI;
import me.loving11ish.epichomes.models.User;
import me.loving11ish.epichomes.utils.ColorUtils;
import me.loving11ish.epichomes.utils.UsermapStorageUtil;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class DeleteHomeCommand implements CommandExecutor, TabCompleter {

    Logger logger = EpicHomes.getPlugin().getLogger();
    FileConfiguration config = EpicHomes.getPlugin().getConfig();
    FileConfiguration messagesConfig = EpicHomes.getPlugin().messagesFileManager.getMessagesConfig();
    private UsermapStorageUtil usermapStorageUtil = EpicHomes.getPlugin().usermapStorageUtil;

    private String prefix = messagesConfig.getString("global-prefix");
    private static final String PREFIX_PLACEHOLDER = "%PREFIX%";
    private static final String HOME_PLACEHOLDER = "%HOME%";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player){
            Player player = (Player) sender;
            User user = usermapStorageUtil.getUserByOnlinePlayer(player);
            if (args.length < 1){
                if (config.getBoolean("gui-system.use-global-gui.enabled")){
                    new DeleteHomesListGUI(EpicHomes.getPlayerMenuUtility(player)).open();
                }else {
                    player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("incorrect-delhome-command-usage.line-1").replace(PREFIX_PLACEHOLDER, prefix)));
                    player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("incorrect-delhome-command-usage.line-2").replace(PREFIX_PLACEHOLDER, prefix)));
                }
                return true;
            }else {
                if (args[0] != null){
                    if (config.getBoolean("gui-system.use-global-gui.enabled")){
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
            logger.warning(ColorUtils.translateColorCodes(messagesConfig.getString("incorrect-command-usage")
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
