package me.loving11ish.epichomes.updatesystem;

import com.tcoded.folialib.FoliaLib;
import me.loving11ish.epichomes.EpicHomes;
import me.loving11ish.epichomes.utils.ColorUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.util.Consumer;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.logging.Logger;

public class UpdateChecker {

    private int resourceId;
    FileConfiguration messagesConfig = EpicHomes.getPlugin().messagesFileManager.getMessagesConfig();
    Logger logger = EpicHomes.getPlugin().getLogger();

    private String prefix = messagesConfig.getString("global-prefix");
    private static final String PREFIX_PLACEHOLDER = "%PREFIX%";

    public UpdateChecker(int resourceId) {
        this.resourceId = resourceId;
    }

    public void getVersion(final Consumer<String> consumer) {
        FoliaLib foliaLib = new FoliaLib(EpicHomes.getPlugin());
        foliaLib.getImpl().runAsync(() -> {
            try (InputStream inputStream = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + this.resourceId).openStream(); Scanner scanner = new Scanner(inputStream)) {
                if (scanner.hasNext()) {
                    consumer.accept(scanner.next());
                }
            } catch (IOException exception) {
                logger.warning(ColorUtils.translateColorCodes(messagesConfig.getString("update-check-failure").replace(PREFIX_PLACEHOLDER, prefix) + exception.getMessage()));
            }
        });
    }
}
