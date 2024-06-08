package me.loving11ish.epichomes.menusystem;

import me.loving11ish.epichomes.EpicHomes;
import me.loving11ish.epichomes.utils.ColorUtils;
import org.bukkit.Material;

public abstract class PaginatedMenu extends Menu {

    protected int page = 0;
    protected int maxItemsPerPage = 44;
    protected int index = 0;

    public PaginatedMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
    }

    public void addMenuControls(){
        inventory.setItem(48, makeItem(Material.STONE_BUTTON, ColorUtils.translateColorCodes(EpicHomes.getPlugin().getConfigManager().getMenuPreviousPageItemName())));
        inventory.setItem(49, makeItem(Material.BARRIER, ColorUtils.translateColorCodes(EpicHomes.getPlugin().getConfigManager().getMenuCloseItemName())));
        inventory.setItem(50, makeItem(Material.STONE_BUTTON, ColorUtils.translateColorCodes(EpicHomes.getPlugin().getConfigManager().getMenuNextPageItemName())));
    }

    public int getMaxItemsPerPage() {
        return maxItemsPerPage;
    }
}
