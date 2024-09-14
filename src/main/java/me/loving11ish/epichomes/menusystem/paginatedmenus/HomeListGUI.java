package me.loving11ish.epichomes.menusystem.paginatedmenus;

import com.tcoded.folialib.FoliaLib;
import me.loving11ish.epichomes.EpicHomes;
import me.loving11ish.epichomes.api.events.AsyncHomePreTeleportEvent;
import me.loving11ish.epichomes.menusystem.PaginatedMenu;
import me.loving11ish.epichomes.menusystem.PlayerMenuUtility;
import me.loving11ish.epichomes.menusystem.menus.DeleteSingleGUI;
import me.loving11ish.epichomes.models.User;
import me.loving11ish.epichomes.utils.ColorUtils;
import me.loving11ish.epichomes.utils.MessageUtils;
import me.loving11ish.epichomes.utils.TeleportationUtils;
import me.loving11ish.epichomes.utils.UsermapStorageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class HomeListGUI extends PaginatedMenu {

    private final FoliaLib foliaLib = EpicHomes.getFoliaLib();

    private static final String HOME_NAME_PLACEHOLDER = "%HOME%";
    private static final String HOME_LOCATION_PLACEHOLDER_WORLD = "%LOCATION-WORLD%";
    private static final String HOME_LOCATION_PLACEHOLDER_X = "%LOCATION-X%";
    private static final String HOME_LOCATION_PLACEHOLDER_Y = "%LOCATION-Y%";
    private static final String HOME_LOCATION_PLACEHOLDER_Z = "%LOCATION-Z%";

    private final UsermapStorageUtil usermapStorageUtil = EpicHomes.getPlugin().getUsermapStorageUtil();

    public HomeListGUI(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
    }

    @Override
    public String getMenuName() {
        return ColorUtils.translateColorCodes(EpicHomes.getPlugin().getConfigManager().getHomeListGUITitle());
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void handleMenu(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        User user = usermapStorageUtil.getUserByOnlinePlayer(player);
        List<String> userHomesList = usermapStorageUtil.getHomeNamesListByUser(user);
        playerMenuUtility.setUser(user);

        //Check & run left click options
        if (event.isLeftClick()) {
            if (event.getCurrentItem().getType().equals(EpicHomes.getPlugin().getConfigManager().getHomeListGUIItemMaterial())) {
                String homeName = event.getCurrentItem().getItemMeta().getPersistentDataContainer().get(new NamespacedKey(EpicHomes.getPlugin(), "homeName"), PersistentDataType.STRING);

                if (homeName != null) {
                    Location homeLocation = usermapStorageUtil.getHomeLocationByHomeName(user, homeName);
                    playerMenuUtility.setHomeName(homeName);
                    playerMenuUtility.setHomeLocation(homeLocation);

                    if (EpicHomes.getPlugin().getConfigManager().isUseDelayBeforeHomeTP()) {
                        TeleportationUtils teleportationUtils = new TeleportationUtils();

                        foliaLib.getScheduler().runAsync((task -> {
                            fireHomePreTeleportEvent(player, user, homeName, homeLocation, player.getLocation());
                            MessageUtils.sendDebugConsole("&aFired AsyncHomePreTeleportEvent");
                        }));

                        teleportationUtils.teleportPlayerAsyncTimed(player, homeLocation, homeName);
                        player.closeInventory();
                    } else {
                        TeleportationUtils teleportationUtils = new TeleportationUtils();

                        foliaLib.getScheduler().runAsync((task -> {
                            fireHomePreTeleportEvent(player, user, homeName, homeLocation, player.getLocation());
                            MessageUtils.sendDebugConsole("&aFired AsyncHomePreTeleportEvent");
                        }));

                        teleportationUtils.teleportPlayerAsync(player, homeLocation, homeName);
                        player.closeInventory();
                    }
                }
            }

            else if (event.getCurrentItem().getType().equals(Material.BARRIER)) {
                player.closeInventory();
            }

            else if (event.getCurrentItem().getType().equals(Material.STONE_BUTTON)) {
                if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ColorUtils.translateColorCodes(EpicHomes.getPlugin().getConfigManager().getMenuPreviousPageItemName()))) {

                    if (page == 0) {
                        MessageUtils.sendPlayer(player, EpicHomes.getPlugin().getMessagesManager().getGuiFirstPage());
                    } else {
                        page = page - 1;
                        super.open();
                    }
                } else if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ColorUtils.translateColorCodes(EpicHomes.getPlugin().getConfigManager().getMenuNextPageItemName()))) {

                    if (!((index + 1) >= userHomesList.size())) {
                        page = page + 1;
                        super.open();
                    } else {
                        MessageUtils.sendPlayer(player, EpicHomes.getPlugin().getMessagesManager().getGuiLastPage());
                    }
                }
            }
        }

        //Check & run right click options
        else if (event.isRightClick()) {
            if (event.getCurrentItem().getType().equals(EpicHomes.getPlugin().getConfigManager().getHomeListGUIItemMaterial())) {
                String homeName = event.getCurrentItem().getItemMeta().getPersistentDataContainer().get(new NamespacedKey(EpicHomes.getPlugin(), "homeName"), PersistentDataType.STRING);

                if (homeName != null) {
                    playerMenuUtility.setHomeName(homeName);
                    playerMenuUtility.setHomeLocation(usermapStorageUtil.getHomeLocationByHomeName(user, homeName));
                    new DeleteSingleGUI(playerMenuUtility).open();
                }
            }
        }
    }

    @Override
    public void setMenuItems() {
        addMenuControls();
        User user = usermapStorageUtil.getUserByOnlinePlayer(playerMenuUtility.getOwner());
        List<String> userHomesList = usermapStorageUtil.getHomeNamesListByUser(user);

        //Pagination loop template
        if (userHomesList != null && !userHomesList.isEmpty()) {
            for (int i = 0; i < getMaxItemsPerPage(); i++) {
                index = getMaxItemsPerPage() * page + i;

                if (index >= userHomesList.size()) break;

                if (userHomesList.get(index) != null) {
                    String homeName = userHomesList.get(index);
                    Location homeLocation = usermapStorageUtil.getHomeLocationByHomeName(user, homeName);

                    ItemStack homeItem = new ItemStack(EpicHomes.getPlugin().getConfigManager().getHomeListGUIItemMaterial(), 1);
                    ItemMeta itemMeta = homeItem.getItemMeta();
                    itemMeta.setDisplayName(ColorUtils.translateColorCodes(EpicHomes.getPlugin().getConfigManager().getHomeListGUIItemName())
                            .replace(HOME_NAME_PLACEHOLDER, homeName));

                    List<String> loreConfigList = EpicHomes.getPlugin().getConfigManager().getHomeListGUIItemLore();
                    ArrayList<String> homeLore = new ArrayList<>();

                    for (String string : loreConfigList) {
                        homeLore.add(ColorUtils.translateColorCodes(string)
                                .replace(HOME_LOCATION_PLACEHOLDER_WORLD, homeLocation.getWorld().getName())
                                .replace(HOME_LOCATION_PLACEHOLDER_X, String.valueOf(Math.round(homeLocation.getX())))
                                .replace(HOME_LOCATION_PLACEHOLDER_Y, String.valueOf(Math.round(homeLocation.getY())))
                                .replace(HOME_LOCATION_PLACEHOLDER_Z, String.valueOf(Math.round(homeLocation.getZ()))));
                    }

                    itemMeta.setLore(homeLore);

                    itemMeta.getPersistentDataContainer().set(new NamespacedKey(EpicHomes.getPlugin(), "homeName"), PersistentDataType.STRING, homeName);

                    homeItem.setItemMeta(itemMeta);

                    inventory.addItem(homeItem);
                }
            }
        }
    }

    private static void fireHomePreTeleportEvent(Player player, User user, String homeName, Location homeLocation, Location oldLocation) {
        AsyncHomePreTeleportEvent asyncHomePreTeleportEvent = new AsyncHomePreTeleportEvent(player, user, homeName, homeLocation, oldLocation);
        Bukkit.getPluginManager().callEvent(asyncHomePreTeleportEvent);
    }
}
