package me.loving11ish.epichomes.utils;

import com.tcoded.folialib.FoliaLib;

import com.tcoded.folialib.wrapper.task.WrappedTask;
import me.loving11ish.epichomes.EpicHomes;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class AutoSaveTaskUtils {

    private static final ConsoleCommandSender console = Bukkit.getConsoleSender();

    private static final FoliaLib foliaLib = EpicHomes.getFoliaLib();
    private static final UsermapStorageUtil usermapStorageUtil = EpicHomes.getPlugin().usermapStorageUtil;

    public static WrappedTask autoSaveTask;

    private static final FileConfiguration config = EpicHomes.getPlugin().getConfig();
    private static final FileConfiguration messagesConfig = EpicHomes.getPlugin().messagesFileManager.getMessagesConfig();

    private static final String PREFIX_PLACEHOLDER = "%PREFIX%";

    private static final String prefix = messagesConfig.getString("global-prefix", "&f[&6Epic&bHomes&f]&r");

    public static void runAutoSaveTask() {
        autoSaveTask = foliaLib.getImpl().runTimerAsync(() -> {
            try {
                usermapStorageUtil.saveUsermap();
                if (config.getBoolean("general.show-auto-save-task-message.enabled", true)){
                    console.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("auto-save-complete")
                            .replace(PREFIX_PLACEHOLDER, prefix)));
                }
                if (config.getBoolean("general.developer-debug-mode.enabled", false)){
                    console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes-Debug: &aWrapped task: " + autoSaveTask.toString()));
                    console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes-Debug: &aAuto save timed task run successfully"));
                }
            } catch (IOException e) {
                console.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("auto-save-failed")
                        .replace(PREFIX_PLACEHOLDER, prefix)));
                e.printStackTrace();
            }
        }, 1L, 900L, TimeUnit.SECONDS);
    }

    public static WrappedTask getAutoSaveTask() {
        return autoSaveTask;
    }
}
