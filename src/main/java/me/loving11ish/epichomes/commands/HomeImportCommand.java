package me.loving11ish.epichomes.commands;

import me.loving11ish.epichomes.EpicHomes;
import me.loving11ish.epichomes.importers.EssentialsXHomesDataReader;
import me.loving11ish.epichomes.importers.StormerHomesDataReader;
import me.loving11ish.epichomes.importers.StormerHomesReloadedDataReader;
import me.loving11ish.epichomes.utils.ColorUtils;
import me.loving11ish.epichomes.utils.UsermapStorageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class HomeImportCommand implements CommandExecutor {

    private final ConsoleCommandSender console = Bukkit.getConsoleSender();

    private final FileConfiguration config = EpicHomes.getPlugin().getConfig();
    private final FileConfiguration messagesConfig = EpicHomes.getPlugin().messagesFileManager.getMessagesConfig();

    private final UsermapStorageUtil usermapStorageUtil = EpicHomes.getPlugin().usermapStorageUtil;
    private final String prefix = messagesConfig.getString("global-prefix", "&f[&6Epic&bHomes&f]&r");
    private static final String PREFIX_PLACEHOLDER = "%PREFIX%";
    private static final String PLUGIN_PLACEHOLDER = "%PLUGIN%";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!config.getBoolean("general.home-data-importer.enabled")){
            sender.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("home-data-import-disabled")
                    .replace(PREFIX_PLACEHOLDER, prefix)));
            return true;
        }
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.hasPermission("epichomes.command.import")
                    || player.hasPermission("epichomes.command.*")
                    || player.hasPermission("epichomes.*")
                    || player.isOp()) {
                if (usermapStorageUtil.getHomeImportCompleted()){
                    player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("home-data-import-failed-already-run")
                            .replace(PREFIX_PLACEHOLDER, prefix)));
                    return true;
                }
                if (config.getString("general.home-data-importer.import-from").equalsIgnoreCase("StormerHomes")){
                    StormerHomesDataReader stormerHomesDataReader = new StormerHomesDataReader();
                    if (stormerHomesDataReader.homesImportSuccessful) {
                        player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("home-data-import-successful")
                                .replace(PREFIX_PLACEHOLDER, prefix)
                                .replace(PLUGIN_PLACEHOLDER, "StormerHomes")));
                        usermapStorageUtil.setHomeImportCompleted();
                    }else {
                        player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("home-data-import-failed")
                                .replace(PREFIX_PLACEHOLDER, prefix)
                                .replace(PLUGIN_PLACEHOLDER, "StormerHomes")));
                    }
                }else if (config.getString("general.home-data-importer.import-from").equalsIgnoreCase("StormerHomesReloaded")){
                    StormerHomesReloadedDataReader stormerHomesReloadedDataReader = new StormerHomesReloadedDataReader();
                    if (stormerHomesReloadedDataReader.homesImportSuccessful){
                        player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("home-data-import-successful")
                                .replace(PREFIX_PLACEHOLDER, prefix)
                                .replace(PLUGIN_PLACEHOLDER, "StormerHomesReloaded")));
                        usermapStorageUtil.setHomeImportCompleted();
                    }else {
                        player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("home-data-import-failed")
                                .replace(PREFIX_PLACEHOLDER, prefix)
                                .replace(PLUGIN_PLACEHOLDER, "StormerHomesReloaded")));
                    }
                }else if (config.getString("general.home-data-importer.import-from").equalsIgnoreCase("EssentialsX")){
                    EssentialsXHomesDataReader essentialsXHomesDataReader = new EssentialsXHomesDataReader();
                    if (essentialsXHomesDataReader.homesImportSuccessful){
                        player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("home-data-import-successful")
                                .replace(PREFIX_PLACEHOLDER, prefix)
                                .replace(PLUGIN_PLACEHOLDER, "EssentialsX")));
                        usermapStorageUtil.setHomeImportCompleted();
                    }else {
                        player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("home-data-import-failed")
                                .replace(PREFIX_PLACEHOLDER, prefix)
                                .replace(PLUGIN_PLACEHOLDER, "EssentialsX")));
                    }
                }
            }

        }else if (sender instanceof ConsoleCommandSender){
            if (usermapStorageUtil.getHomeImportCompleted()){
                console.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("home-data-import-failed-already-run")
                        .replace(PREFIX_PLACEHOLDER, prefix)));
                return true;
            }
            if (config.getString("general.home-data-importer.import-from").equalsIgnoreCase("StormerHomes")){
                StormerHomesDataReader stormerHomesDataReader = new StormerHomesDataReader();
                if (stormerHomesDataReader.homesImportSuccessful){
                    console.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("home-data-import-successful")
                            .replace(PREFIX_PLACEHOLDER, prefix)
                            .replace(PLUGIN_PLACEHOLDER, "StormerHomes")));
                    usermapStorageUtil.setHomeImportCompleted();
                }else {
                    console.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("home-data-import-failed")
                            .replace(PREFIX_PLACEHOLDER, prefix)
                            .replace(PLUGIN_PLACEHOLDER, "StormerHomes")));
                }
            }else if (config.getString("general.home-data-importer.import-from").equalsIgnoreCase("StormerHomesReloaded")){
                StormerHomesReloadedDataReader stormerHomesReloadedDataReader = new StormerHomesReloadedDataReader();
                if (stormerHomesReloadedDataReader.homesImportSuccessful){
                    console.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("home-data-import-successful")
                            .replace(PREFIX_PLACEHOLDER, prefix)
                            .replace(PLUGIN_PLACEHOLDER, "StormerHomesReloaded")));
                    usermapStorageUtil.setHomeImportCompleted();
                }else {
                    console.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("home-data-import-failed")
                            .replace(PREFIX_PLACEHOLDER, prefix)
                            .replace(PLUGIN_PLACEHOLDER, "StormerHomesReloaded")));
                }
            }else if (config.getString("general.home-data-importer.import-from").equalsIgnoreCase("EssentialsX")){
                EssentialsXHomesDataReader essentialsXHomesDataReader = new EssentialsXHomesDataReader();
                if (essentialsXHomesDataReader.homesImportSuccessful){
                    console.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("home-data-import-successful")
                            .replace(PREFIX_PLACEHOLDER, prefix)
                            .replace(PLUGIN_PLACEHOLDER, "EssentialsX")));
                    usermapStorageUtil.setHomeImportCompleted();
                }else {
                    console.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("home-data-import-failed")
                            .replace(PREFIX_PLACEHOLDER, prefix)
                            .replace(PLUGIN_PLACEHOLDER, "EssentialsX")));
                }
            }
        }
        return true;
    }
}
