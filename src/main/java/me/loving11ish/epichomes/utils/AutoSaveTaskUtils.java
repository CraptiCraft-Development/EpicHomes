package me.loving11ish.epichomes.utils;

import com.tcoded.folialib.FoliaLib;
import com.tcoded.folialib.wrapper.WrappedTask;
import me.loving11ish.epichomes.EpicHomes;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class AutoSaveTaskUtils {

    private static Logger logger = EpicHomes.getPlugin().getLogger();

    private static FoliaLib foliaLib = new FoliaLib(EpicHomes.getPlugin());
    private static UsermapStorageUtil usermapStorageUtil = EpicHomes.getPlugin().usermapStorageUtil;

    public static WrappedTask wrappedTaskTwo;
    public static WrappedTask wrappedTaskThree;

    private static FileConfiguration config = EpicHomes.getPlugin().getConfig();
    private static FileConfiguration messagesConfig = EpicHomes.getPlugin().messagesFileManager.getMessagesConfig();

    private static final String PREFIX_PLACEHOLDER = "%PREFIX%";

    private static String prefix = messagesConfig.getString("global-prefix");

    public static void runAutoSaveTaskOne() {
        wrappedTaskTwo = foliaLib.getImpl().runTimerAsync(new Runnable() {
            int time = 900;
            @Override
            public void run() {
                if (time == 1){
                    try {
                        if (config.getBoolean("general.show-auto-save-task-message.enabled")){
                            logger.info(ColorUtils.translateColorCodes(messagesConfig.getString("auto-save-complete")
                                    .replace(PREFIX_PLACEHOLDER, prefix)));
                        }
                        usermapStorageUtil.saveUsermap();
                    } catch (IOException e) {
                        logger.warning(ColorUtils.translateColorCodes(messagesConfig.getString("auto-save-failed")
                                .replace(PREFIX_PLACEHOLDER, prefix)));
                        e.printStackTrace();
                    }
                    runAutoSaveTaskTwo();
                    wrappedTaskTwo.cancel();
                    return;
                }else {
                    time --;
                }
            }
        }, 0L, 1L, TimeUnit.SECONDS);
    }

    public static void runAutoSaveTaskTwo() {
        wrappedTaskThree = foliaLib.getImpl().runTimerAsync(new Runnable() {
            int time = 900;
            @Override
            public void run() {
                if (time == 1){
                    try {
                        if (config.getBoolean("general.show-auto-save-task-message.enabled")){
                            logger.info(ColorUtils.translateColorCodes(messagesConfig.getString("auto-save-complete")
                                    .replace(PREFIX_PLACEHOLDER, prefix)));
                        }
                        usermapStorageUtil.saveUsermap();
                    } catch (IOException e) {
                        logger.warning(ColorUtils.translateColorCodes(messagesConfig.getString("auto-save-failed")
                                .replace(PREFIX_PLACEHOLDER, prefix)));
                        e.printStackTrace();
                    }
                    runAutoSaveTaskOne();
                    wrappedTaskThree.cancel();
                    return;
                }else {
                    time --;
                }
            }
        }, 0L, 1L, TimeUnit.SECONDS);
    }
}
