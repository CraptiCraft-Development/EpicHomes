package me.loving11ish.epichomes.commands;

import com.tcoded.folialib.FoliaLib;
import me.loving11ish.epichomes.EpicHomes;
import me.loving11ish.epichomes.api.events.HomePreTeleportEvent;
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
import org.bukkit.command.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class HomeCommand implements CommandExecutor, TabCompleter {

    private final ConsoleCommandSender console = Bukkit.getConsoleSender();

    private final FileConfiguration config = EpicHomes.getPlugin().getConfig();
    private final FileConfiguration messagesConfig = EpicHomes.getPlugin().messagesFileManager.getMessagesConfig();

    private static final FoliaLib foliaLib = EpicHomes.getFoliaLib();
    private static List<String> bannedNames;

    private final String prefix = messagesConfig.getString("global-prefix", "&f[&6Epic&bHomes&f]&r");
    private static final String PREFIX_PLACEHOLDER = "%PREFIX%";
    private static final String HOME_NAME_PLACEHOLDER = "%HOME%";
    private final UsermapStorageUtil usermapStorageUtil = EpicHomes.getPlugin().usermapStorageUtil;

    public static void updateBannedNamesList() {
        foliaLib.getImpl().runLaterAsync(() ->
                bannedNames = EpicHomes.getPlugin().getConfig().getStringList("homes.disallowed-home-names"), 50L, TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            User user = usermapStorageUtil.getUserByOnlinePlayer(player);
            if (args.length < 1){
                if (EpicHomes.isGUIEnabled()){
                    new HomeListGUI(EpicHomes.getPlayerMenuUtility(player)).open();
                }else {
                    player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("incorrect-home-command-usage.line-1").replace(PREFIX_PLACEHOLDER, prefix)));
                    player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("incorrect-home-command-usage.line-2").replace(PREFIX_PLACEHOLDER, prefix)));
                    player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("incorrect-home-command-usage.line-3").replace(PREFIX_PLACEHOLDER, prefix)));
                    player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("incorrect-home-command-usage.line-4").replace(PREFIX_PLACEHOLDER, prefix)));
                    player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("incorrect-home-command-usage.line-5").replace(PREFIX_PLACEHOLDER, prefix)));
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
                                return new SetSubCommand().setSubCommand(sender, args, bannedNames);
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
                                if (EpicHomes.isGUIEnabled()){
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
                    default:
                        player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("incorrect-home-command-usage.line-1").replace(PREFIX_PLACEHOLDER, prefix)));
                        player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("incorrect-home-command-usage.line-2").replace(PREFIX_PLACEHOLDER, prefix)));
                        player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("incorrect-home-command-usage.line-3").replace(PREFIX_PLACEHOLDER, prefix)));
                        player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("incorrect-home-command-usage.line-4").replace(PREFIX_PLACEHOLDER, prefix)));
                        player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("incorrect-home-command-usage.line-5").replace(PREFIX_PLACEHOLDER, prefix)));
                }
            }else {
                List<String> userHomesList = usermapStorageUtil.getHomeNamesListByUser(user);
                for (String home : userHomesList){
                    if (!userHomesList.contains(args[0])){
                        player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("home-name-does-not-exist")
                                .replace(PREFIX_PLACEHOLDER, prefix)
                                .replace(HOME_NAME_PLACEHOLDER, args[0])));
                        return true;
                    }
                    if (args[0].equalsIgnoreCase(home)){
                        Location homeLocation = usermapStorageUtil.getHomeLocationByHomeName(user, args[0]);
                        if (config.getBoolean("homes.teleportation.delay-before-teleport.enabled")){
                            TeleportationUtils teleportationUtils = new TeleportationUtils();
                            fireHomePreTeleportEvent(player, user, args[0], homeLocation, player.getLocation());
                            if (config.getBoolean("general.developer-debug-mode.enabled", false)){
                                console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes-Debug: &aFired HomePreTeleportEvent"));
                            }
                            teleportationUtils.teleportPlayerAsyncTimed(player, homeLocation, args[0]);
                        }else {
                            TeleportationUtils teleportationUtils = new TeleportationUtils();
                            fireHomePreTeleportEvent(player, user, args[0], homeLocation, player.getLocation());
                            if (config.getBoolean("general.developer-debug-mode.enabled", false)){
                                console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes-Debug: &aFired HomePreTeleportEvent"));
                            }
                            teleportationUtils.teleportPlayerAsync(player, homeLocation, args[0]);
                        }
                        return true;
                    }
                }
            }
        }else if (sender instanceof ConsoleCommandSender) {
            console.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("incorrect-command-usage")
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
