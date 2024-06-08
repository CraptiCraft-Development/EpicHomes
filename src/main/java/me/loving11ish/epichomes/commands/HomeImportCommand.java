package me.loving11ish.epichomes.commands;

import me.loving11ish.epichomes.EpicHomes;
import me.loving11ish.epichomes.importers.EssentialsXHomesDataReader;
import me.loving11ish.epichomes.importers.StormerHomesDataReader;
import me.loving11ish.epichomes.importers.StormerHomesReloadedDataReader;
import me.loving11ish.epichomes.utils.MessageUtils;
import me.loving11ish.epichomes.utils.UsermapStorageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class HomeImportCommand implements CommandExecutor {

    private final UsermapStorageUtil usermapStorageUtil = EpicHomes.getPlugin().getUsermapStorageUtil();
    private static final String PLUGIN_PLACEHOLDER = "%PLUGIN%";

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        if (!EpicHomes.getPlugin().getConfigManager().isUseDataImporter()) {
            MessageUtils.sendSender(sender, EpicHomes.getPlugin().getMessagesManager().getHomeDataImportFailedSystemDisabled());
            return true;
        }

        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.hasPermission("epichomes.command.import") || player.hasPermission("epichomes.command.*")
                    || player.hasPermission("epichomes.*") || player.isOp()) {

                handleImport(sender);
                return true;
            }

        } else if (sender instanceof ConsoleCommandSender) {
            handleImport(sender);
        }
        return true;
    }

    private void handleImport(CommandSender sender) {
        if (usermapStorageUtil.getHomeImportCompleted()) {
            MessageUtils.sendSender(sender, EpicHomes.getPlugin().getMessagesManager().getHomeDataImportFailedAlreadyImported());
            return;
        }

        if (EpicHomes.getPlugin().getConfigManager().getDataImportPlugin().equalsIgnoreCase("StormerHomes")) {
            StormerHomesDataReader stormerHomesDataReader = new StormerHomesDataReader();

            if (stormerHomesDataReader.homesImportSuccessful) {
                MessageUtils.sendSender(sender, EpicHomes.getPlugin().getMessagesManager().getHomeDataImportSuccess()
                        .replace(PLUGIN_PLACEHOLDER, "StormerHomes"));
                usermapStorageUtil.setHomeImportCompleted();
            }

            else {
                MessageUtils.sendSender(sender, EpicHomes.getPlugin().getMessagesManager().getHomeDataImportFailed()
                        .replace(PLUGIN_PLACEHOLDER, "StormerHomes"));
            }
        }

        else if (EpicHomes.getPlugin().getConfigManager().getDataImportPlugin().equalsIgnoreCase("StormerHomesReloaded")) {
            StormerHomesReloadedDataReader stormerHomesReloadedDataReader = new StormerHomesReloadedDataReader();

            if (stormerHomesReloadedDataReader.homesImportSuccessful) {
                MessageUtils.sendSender(sender, EpicHomes.getPlugin().getMessagesManager().getHomeDataImportSuccess()
                        .replace(PLUGIN_PLACEHOLDER, "StormerHomesReloaded"));
                usermapStorageUtil.setHomeImportCompleted();
            }

            else {
                MessageUtils.sendSender(sender, EpicHomes.getPlugin().getMessagesManager().getHomeDataImportFailed()
                        .replace(PLUGIN_PLACEHOLDER, "StormerHomesReloaded"));
            }
        }

        else if (EpicHomes.getPlugin().getConfigManager().getDataImportPlugin().equalsIgnoreCase("EssentialsX")) {
            EssentialsXHomesDataReader essentialsXHomesDataReader = new EssentialsXHomesDataReader();

            if (essentialsXHomesDataReader.homesImportSuccessful) {
                MessageUtils.sendSender(sender, EpicHomes.getPlugin().getMessagesManager().getHomeDataImportSuccess()
                        .replace(PLUGIN_PLACEHOLDER, "EssentialsX"));
                usermapStorageUtil.setHomeImportCompleted();
            }

            else {
                MessageUtils.sendSender(sender, EpicHomes.getPlugin().getMessagesManager().getHomeDataImportFailed()
                        .replace(PLUGIN_PLACEHOLDER, "EssentialsX"));
            }
        }
    }
}
