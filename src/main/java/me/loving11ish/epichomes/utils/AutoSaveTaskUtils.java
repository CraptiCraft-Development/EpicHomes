package me.loving11ish.epichomes.utils;

import com.tcoded.folialib.FoliaLib;

import com.tcoded.folialib.wrapper.task.WrappedTask;
import me.loving11ish.epichomes.EpicHomes;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class AutoSaveTaskUtils {

    private static final FoliaLib foliaLib = EpicHomes.getFoliaLib();

    public static WrappedTask autoSaveTask;

    public static void runAutoSaveTask() {
        autoSaveTask = foliaLib.getImpl().runTimerAsync(() -> {
            try {
                EpicHomes.getPlugin().getUsermapStorageUtil().saveUsermap();

                if (EpicHomes.getPlugin().getConfigManager().isShowAutoSaveConsoleMessage()){
                    MessageUtils.sendConsole(EpicHomes.getPlugin().getMessagesManager().getAutoSaveComplete());
                }

                MessageUtils.sendDebugConsole("&aWrapped task:" + autoSaveTask.toString());
                MessageUtils.sendDebugConsole("&aAuto save timed task run successfully");

            } catch (IOException e) {
                MessageUtils.sendConsole("error", EpicHomes.getPlugin().getMessagesManager().getAutoSaveFailed());
                e.printStackTrace();
            }
        }, 1L, 900L, TimeUnit.SECONDS);
    }

    public static WrappedTask getAutoSaveTask() {
        return autoSaveTask;
    }
}
