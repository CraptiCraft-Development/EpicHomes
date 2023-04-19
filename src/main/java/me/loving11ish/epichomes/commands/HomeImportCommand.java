package me.loving11ish.epichomes.commands;

import me.loving11ish.epichomes.EpicHomes;
import me.loving11ish.epichomes.importers.EssentialsXHomesDataReader;
import me.loving11ish.epichomes.importers.StormerHomesDataReader;
import me.loving11ish.epichomes.importers.StormerHomesReloadedDataReader;
import me.loving11ish.epichomes.utils.ColorUtils;
import me.loving11ish.epichomes.utils.UsermapStorageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.logging.Logger;

public class HomeImportCommand implements CommandExecutor {

    Logger logger = EpicHomes.getPlugin().getLogger();
    FileConfiguration config = EpicHomes.getPlugin().getConfig();
    FileConfiguration messagesConfig = EpicHomes.getPlugin().messagesFileManager.getMessagesConfig();

    private UsermapStorageUtil usermapStorageUtil = EpicHomes.getPlugin().usermapStorageUtil;
    private String prefix = messagesConfig.getString("global-prefix");
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
                logger.warning(ColorUtils.translateColorCodes(messagesConfig.getString("home-data-import-failed-already-run")
                        .replace(PREFIX_PLACEHOLDER, prefix)));
                return true;
            }
            if (config.getString("general.home-data-importer.import-from").equalsIgnoreCase("StormerHomes")){
                StormerHomesDataReader stormerHomesDataReader = new StormerHomesDataReader();
                if (stormerHomesDataReader.homesImportSuccessful){
                    logger.info(ColorUtils.translateColorCodes(messagesConfig.getString("home-data-import-successful")
                            .replace(PREFIX_PLACEHOLDER, prefix)
                            .replace(PLUGIN_PLACEHOLDER, "StormerHomes")));
                    usermapStorageUtil.setHomeImportCompleted();
                }else {
                    logger.warning(ColorUtils.translateColorCodes(messagesConfig.getString("home-data-import-failed")
                            .replace(PREFIX_PLACEHOLDER, prefix)
                            .replace(PLUGIN_PLACEHOLDER, "StormerHomes")));
                }
            }else if (config.getString("general.home-data-importer.import-from").equalsIgnoreCase("StormerHomesReloaded")){
                StormerHomesReloadedDataReader stormerHomesReloadedDataReader = new StormerHomesReloadedDataReader();
                if (stormerHomesReloadedDataReader.homesImportSuccessful){
                    logger.info(ColorUtils.translateColorCodes(messagesConfig.getString("home-data-import-successful")
                            .replace(PREFIX_PLACEHOLDER, prefix)
                            .replace(PLUGIN_PLACEHOLDER, "StormerHomesReloaded")));
                    usermapStorageUtil.setHomeImportCompleted();
                }else {
                    logger.warning(ColorUtils.translateColorCodes(messagesConfig.getString("home-data-import-failed")
                            .replace(PREFIX_PLACEHOLDER, prefix)
                            .replace(PLUGIN_PLACEHOLDER, "StormerHomesReloaded")));
                }
            }else if (config.getString("general.home-data-importer.import-from").equalsIgnoreCase("EssentialsX")){
                EssentialsXHomesDataReader essentialsXHomesDataReader = new EssentialsXHomesDataReader();
                if (essentialsXHomesDataReader.homesImportSuccessful){
                    logger.info(ColorUtils.translateColorCodes(messagesConfig.getString("home-data-import-successful")
                            .replace(PREFIX_PLACEHOLDER, prefix)
                            .replace(PLUGIN_PLACEHOLDER, "EssentialsX")));
                    usermapStorageUtil.setHomeImportCompleted();
                }else {
                    logger.warning(ColorUtils.translateColorCodes(messagesConfig.getString("home-data-import-failed")
                            .replace(PREFIX_PLACEHOLDER, prefix)
                            .replace(PLUGIN_PLACEHOLDER, "EssentialsX")));
                }
            }
        }
        return true;
    }
}
