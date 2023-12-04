package me.loving11ish.epichomes.menusystem;

import me.loving11ish.epichomes.EpicHomes;
import me.loving11ish.epichomes.utils.ColorUtils;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

public abstract class PaginatedMenu extends Menu {

    private final FileConfiguration config = EpicHomes.getPlugin().getConfig();

    protected int page = 0;
    protected int maxItemsPerPage = 45;
    protected int index = 0;

    public PaginatedMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
    }

    public void addMenuControls(){
        inventory.setItem(48, makeItem(Material.STONE_BUTTON, ColorUtils.translateColorCodes(config.getString("gui-system.menu-controls.previous-page-icon-name"))));
        inventory.setItem(49, makeItem(Material.BARRIER, ColorUtils.translateColorCodes(config.getString("gui-system.menu-controls.close-go-back-icon-name"))));
        inventory.setItem(50, makeItem(Material.STONE_BUTTON, ColorUtils.translateColorCodes(config.getString("gui-system.menu-controls.next-page-icon-name"))));
    }

    public int getMaxItemsPerPage() {
        return maxItemsPerPage;
    }
}
