package me.loving11ish.epichomes.commands;

import me.loving11ish.epichomes.EpicHomes;
import me.loving11ish.epichomes.api.HomePreTeleportEvent;
import me.loving11ish.epichomes.commands.subcommands.DeleteSubCommand;
import me.loving11ish.epichomes.commands.subcommands.ListSubCommand;
import me.loving11ish.epichomes.commands.subcommands.ReloadSubCommand;
import me.loving11ish.epichomes.commands.subcommands.SetSubCommand;
import me.loving11ish.epichomes.menusystem.PlayerMenuUtility;
import me.loving11ish.epichomes.menusystem.menus.DeleteSingleGUI;
import me.loving11ish.epichomes.menusystem.paginatedmenus.HomeListGUI;
import me.loving11ish.epichomes.models.User;
import me.loving11ish.epichomes.utils.ColorUtils;
import me.loving11ish.epichomes.utils.TeleportationUtils;
import me.loving11ish.epichomes.utils.UsermapStorageUtil;
import org.bukkit.Bukkit;
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

public class HomeCommand implements CommandExecutor, TabCompleter {

    Logger logger = EpicHomes.getPlugin().getLogger();
    FileConfiguration config = EpicHomes.getPlugin().getConfig();
    FileConfiguration messagesConfig = EpicHomes.getPlugin().messagesFileManager.getMessagesConfig();

    private String prefix = messagesConfig.getString("global-prefix");
    private static final String PREFIX_PLACEHOLDER = "%PREFIX%";
    private static final String HOME_NAME_PLACEHOLDER = "%HOME%";
    private UsermapStorageUtil usermapStorageUtil = EpicHomes.getPlugin().usermapStorageUtil;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            User user = usermapStorageUtil.getUserByOnlinePlayer(player);
            if (args.length < 1){
                if (config.getBoolean("gui-system.use-global-gui.enabled")){
                    new HomeListGUI(EpicHomes.getPlayerMenuUtility(player)).open();
                }else {
                    player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("incorrect-home-command-usage.line-1").replace(PREFIX_PLACEHOLDER, prefix)));
                    player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("incorrect-home-command-usage.line-2").replace(PREFIX_PLACEHOLDER, prefix)));
                    player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("incorrect-home-command-usage.line-3").replace(PREFIX_PLACEHOLDER, prefix)));
                    player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("incorrect-home-command-usage.line-4").replace(PREFIX_PLACEHOLDER, prefix)));
                    player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("incorrect-home-command-usage.line-5").replace(PREFIX_PLACEHOLDER, prefix)));
                    player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("incorrect-home-command-usage.line-6").replace(PREFIX_PLACEHOLDER, prefix)));
                }
                return true;
            }else if (args[0].equalsIgnoreCase("set")
                    ||args[0].equalsIgnoreCase("delete")
                    ||args[0].equalsIgnoreCase("list")
                    ||args[0].equalsIgnoreCase("reload")){
                switch (args[0]) {
                    case "set":
                        if (args.length == 2){
                            if (args[1] != null){
                                return new SetSubCommand().setSubCommand(sender, args);
                            }else {
                                player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("incorrect-home-command-usage.line-3").replace(PREFIX_PLACEHOLDER, prefix)));
                                return true;
                            }
                        }else {
                            player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("incorrect-home-command-usage.line-3").replace(PREFIX_PLACEHOLDER, prefix)));
                            return true;
                        }
                    case "delete":
                        if (args.length == 2){
                            if (args[1] != null){
                                if (config.getBoolean("gui-system.use-global-gui.enabled")){
                                    PlayerMenuUtility playerMenuUtility = EpicHomes.getPlayerMenuUtility(player);
                                    playerMenuUtility.setUser(user);
                                    playerMenuUtility.setHomeName(args[1]);
                                    playerMenuUtility.setHomeLocation(usermapStorageUtil.getHomeLocationByHomeName(user, args[1]));
                                    new DeleteSingleGUI(playerMenuUtility).open();
                                    return true;
                                }else {
                                    return new DeleteSubCommand().deleteSubCommand(sender, args);
                                }
                            }else {
                                player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("incorrect-home-command-usage.line-4").replace(PREFIX_PLACEHOLDER, prefix)));
                                return true;
                            }
                        }else {
                            player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("incorrect-home-command-usage.line-4")
                                    .replace(PREFIX_PLACEHOLDER, prefix)));
                            return true;
                        }
                    case "list":
                        return new ListSubCommand().listSubCommand(sender);
                    case "reload":
                        if (player.hasPermission("epichomes.command.reload")
                                ||player.hasPermission("epichomes.command.*")
                                ||player.hasPermission("epichomes.*")
                                ||player.isOp()){
                            return new ReloadSubCommand().reloadSubCommand(sender);
                        }else {
                            player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("no-permission")
                                    .replace(PREFIX_PLACEHOLDER, prefix)));
                            return true;
                        }
                }
            }else {
                List<String> homesList = usermapStorageUtil.getHomeNamesListByUser(user);
                for (String home : homesList){
                    if (args[0].equalsIgnoreCase(home)){
                        Location homeLocation = usermapStorageUtil.getHomeLocationByHomeName(user, args[0]);
                        if (config.getBoolean("homes.teleportation.delay-before-teleport.enabled")){
                            TeleportationUtils teleportationUtils = new TeleportationUtils();
                            fireHomePreTeleportEvent(player, user, args[0], homeLocation, player.getLocation());
                            if (config.getBoolean("general.developer-debug-mode.enabled")){
                                logger.info(ColorUtils.translateColorCodes("&6EpicHomes-Debug: &aFired HomePreTeleportEvent"));
                            }
                            teleportationUtils.teleportPlayerAsyncTimed(player, homeLocation, args[0]);
                        }else {
                            TeleportationUtils teleportationUtils = new TeleportationUtils();
                            fireHomePreTeleportEvent(player, user, args[0], homeLocation, player.getLocation());
                            if (config.getBoolean("general.developer-debug-mode.enabled")){
                                logger.info(ColorUtils.translateColorCodes("&6EpicHomes-Debug: &aFired HomePreTeleportEvent"));
                            }
                            teleportationUtils.teleportPlayerAsync(player, homeLocation, args[0]);
                        }
                        return true;
                    }else {
                        player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("home-name-does-not-exist")
                                .replace(PREFIX_PLACEHOLDER, prefix)
                                .replace(HOME_NAME_PLACEHOLDER, args[0])));
                        return true;
                    }
                }
            }
        }else {
            logger.warning(ColorUtils.translateColorCodes(messagesConfig.getString("incorrect-command-usage")
                    .replace(PREFIX_PLACEHOLDER, prefix)));
            return true;
        }
        return true;
    }

    private static void fireHomePreTeleportEvent(Player player, User user, String homeName, Location homeLocation, Location oldLocation) {
        HomePreTeleportEvent homePreTeleportEvent = new HomePreTeleportEvent(player, user, homeName, homeLocation, oldLocation);
        Bukkit.getPluginManager().callEvent(homePreTeleportEvent);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        User user = usermapStorageUtil.getUserByOnlinePlayer(player);
        List<String> homesList = usermapStorageUtil.getHomeNamesListByUser(user);

        List<String> arguments = new ArrayList<>(homesList);
        arguments.add("set");
        arguments.add("delete");
        arguments.add("list");
        if (player.hasPermission("epichomes.command.reload")
                ||player.hasPermission("epichomes.command.*")
                ||player.hasPermission("epichomes.*")
                ||player.isOp()){
            arguments.add("reload");
        }

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
