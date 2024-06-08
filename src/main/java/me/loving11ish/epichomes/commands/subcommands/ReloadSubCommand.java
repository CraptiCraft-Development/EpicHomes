package me.loving11ish.epichomes.commands.subcommands;

import com.tcoded.folialib.FoliaLib;
import com.tcoded.folialib.wrapper.task.WrappedTask;
import me.loving11ish.epichomes.EpicHomes;
import me.loving11ish.epichomes.commands.HomeCommand;
import me.loving11ish.epichomes.commands.SetHomeCommand;
import me.loving11ish.epichomes.files.MessagesFileManager;
import me.loving11ish.epichomes.managers.filemanagers.ConfigManager;
import me.loving11ish.epichomes.managers.filemanagers.MessagesManager;
import me.loving11ish.epichomes.updatesystem.UpdateChecker;
import me.loving11ish.epichomes.utils.AutoSaveTaskUtils;
import me.loving11ish.epichomes.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ReloadSubCommand {

    private final FoliaLib foliaLib = EpicHomes.getFoliaLib();

    public boolean reloadSubCommand(CommandSender sender) {
        if (sender instanceof Player){
            Player player = (Player) sender;

            MessageUtils.sendPlayer(player, EpicHomes.getPlugin().getMessagesManager().getPluginReloadStart());
            MessageUtils.sendConsole(EpicHomes.getPlugin().getMessagesManager().getPluginReloadStart());
            MessageUtils.broadcastMessage(EpicHomes.getPlugin().getMessagesManager().getPluginReloadBroadcastStart());

            handleReload();

            foliaLib.getImpl().runLater(() ->
                    MessageUtils.broadcastMessage(EpicHomes.getPlugin().getMessagesManager().getPluginReloadComplete())
                    , 8L, TimeUnit.SECONDS);
            return true;
        }
        return true;
    }

    public boolean reloadSubCommand() {

        MessageUtils.sendConsole(EpicHomes.getPlugin().getMessagesManager().getPluginReloadStart());
        MessageUtils.broadcastMessage(EpicHomes.getPlugin().getMessagesManager().getPluginReloadBroadcastStart());

        handleReload();

        foliaLib.getImpl().runLater(() ->
                MessageUtils.broadcastMessage(EpicHomes.getPlugin().getMessagesManager().getPluginReloadComplete())
                , 8L, TimeUnit.SECONDS);
        return true;
    }

    private void handleReload() {
        FoliaLib foliaLib = EpicHomes.getFoliaLib();

        // Cancel pending teleportation tasks
        try {
            if (!EpicHomes.getPlugin().getTeleportationManager().getTeleportQueue().isEmpty()) {
                for (Map.Entry<UUID, WrappedTask> wrappedTaskEntry : EpicHomes.getPlugin().getTeleportationManager().getTeleportQueue().entrySet()) {

                    Player player = EpicHomes.getPlugin().getServer().getPlayer(wrappedTaskEntry.getKey());
                    if (player != null && player.isOnline()) {
                        MessageUtils.sendPlayer(player, EpicHomes.getPlugin().getMessagesManager().getTimedTeleportCancelledPluginReload());
                    }

                    WrappedTask wrappedTask = wrappedTaskEntry.getValue();
                    wrappedTask.cancel();
                    MessageUtils.sendDebugConsole( "&aWrapped task: " + wrappedTask.toString());
                    MessageUtils.sendDebugConsole( "&aTimed task canceled successfully");
                    EpicHomes.getPlugin().getTeleportationManager().getTeleportQueue().remove(wrappedTaskEntry.getKey());
                }
            }
        } catch (Exception e) {
            MessageUtils.sendConsole("error", "&cAn error occurred while canceling timed teleportation tasks!");
            MessageUtils.sendConsole("error", "&cSee below for more information:");
            e.printStackTrace();
        }

        // Cancel auto save task
        try {
            if (!AutoSaveTaskUtils.getAutoSaveTask().isCancelled()) {
                MessageUtils.sendDebugConsole( "&aWrapped task: " + AutoSaveTaskUtils.getAutoSaveTask().toString());
                MessageUtils.sendDebugConsole( "&aAuto save timed task canceled successfully");
                AutoSaveTaskUtils.getAutoSaveTask().cancel();
            }
            foliaLib.getImpl().cancelAllTasks();
            if (foliaLib.isUnsupported()) {
                Bukkit.getScheduler().cancelTasks(EpicHomes.getPlugin());
                MessageUtils.sendDebugConsole( "&aBukkit scheduler tasks canceled successfully");
            }
            MessageUtils.sendConsole("&3All background tasks stopped successfully.");
        } catch (Exception e) {
            MessageUtils.sendConsole("&3All background tasks stopped successfully.");
        }

        // Reload main config
        EpicHomes.getPlugin().reloadConfig();

        // Reload messages.yml
        MessagesFileManager messagesFileManager = new MessagesFileManager(EpicHomes.getPlugin());
        EpicHomes.getPlugin().setMessagesFileManager(messagesFileManager);

        foliaLib.getImpl().runLaterAsync(() -> {

            // Reload memory values for config.yml
            ConfigManager configManager = new ConfigManager(EpicHomes.getPlugin().getConfig());
            configManager.loadConfigValues();
            EpicHomes.getPlugin().setConfigManager(configManager);

            // Reload memory values for messages.yml
            MessagesManager messagesManager = new MessagesManager(EpicHomes.getPlugin().getMessagesFileManager().getMessagesConfig());
            messagesManager.loadMessagesValues();
            EpicHomes.getPlugin().setMessagesManager(messagesManager);
            MessageUtils.prefix = EpicHomes.getPlugin().getMessagesManager().getPrefix();
            MessageUtils.setDebug(EpicHomes.getPlugin().getConfigManager().isDebugMode());
            MessageUtils.sendConsole("&aReloading config.yml...");
            MessageUtils.sendConsole("&aReloading messages.yml...");

        }, 4L, TimeUnit.SECONDS);

        foliaLib.getImpl().runLater(() -> {
            // Update banned names lists
            HomeCommand.updateBannedNamesList();
            SetHomeCommand.updateBannedNamesList();

            // Check for available updates
            new UpdateChecker(109590).getVersion(version -> {
                if (EpicHomes.getPlugin().getDescription().getVersion().equalsIgnoreCase(version)) {
                    MessageUtils.sendConsole(EpicHomes.getPlugin().getMessagesManager().getUpdateCheckerNoUpdateOne());
                    MessageUtils.sendConsole(EpicHomes.getPlugin().getMessagesManager().getUpdateCheckerNoUpdateTwo());
                    MessageUtils.sendConsole(EpicHomes.getPlugin().getMessagesManager().getUpdateCheckerNoUpdateThree());
                    EpicHomes.getPlugin().setUpdateAvailable(false);
                } else {
                    MessageUtils.sendConsole(EpicHomes.getPlugin().getMessagesManager().getUpdateCheckerUpdateAvailableOne());
                    MessageUtils.sendConsole(EpicHomes.getPlugin().getMessagesManager().getUpdateCheckerUpdateAvailableTwo());
                    MessageUtils.sendConsole(EpicHomes.getPlugin().getMessagesManager().getUpdateCheckerUpdateAvailableThree());
                    EpicHomes.getPlugin().setUpdateAvailable(true);
                }
            });

            // Restart auto save task
            foliaLib.getImpl().runLaterAsync(() -> {
                AutoSaveTaskUtils.runAutoSaveTask();
                MessageUtils.sendConsole(EpicHomes.getPlugin().getMessagesManager().getAutoSaveStart());
            }, 5L, TimeUnit.SECONDS);
        }, 6L, TimeUnit.SECONDS);
    }
}
