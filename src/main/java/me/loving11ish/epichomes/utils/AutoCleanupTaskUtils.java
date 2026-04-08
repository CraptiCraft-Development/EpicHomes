package me.loving11ish.epichomes.utils;

import com.tcoded.folialib.FoliaLib;
import com.tcoded.folialib.wrapper.task.WrappedTask;
import me.loving11ish.epichomes.EpicHomes;

import java.util.concurrent.TimeUnit;

public class AutoCleanupTaskUtils {

    private static final FoliaLib foliaLib = EpicHomes.getFoliaLib();

    public static WrappedTask autoCleanupTask;

    public static void runAutoCleanupTask() {
        autoCleanupTask = foliaLib.getScheduler().runTimerAsync(() -> {
            EpicHomes.getPlugin().getRawPlayerMenuUtilityMap().clear();
            MessageUtils.sendDebugConsole("Player menu utility map cleared by auto-cleanup task.");
        }, 1L, 3600L, TimeUnit.SECONDS); // Runs every hour
    }

    public static WrappedTask getAutoCleanupTask() {
        return autoCleanupTask;
    }
}
