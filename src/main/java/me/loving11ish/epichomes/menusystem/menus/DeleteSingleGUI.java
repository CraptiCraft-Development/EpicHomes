package me.loving11ish.epichomes.menusystem.menus;

import com.tcoded.folialib.FoliaLib;
import me.loving11ish.epichomes.EpicHomes;
import me.loving11ish.epichomes.api.events.AsyncHomeDeleteEvent;
import me.loving11ish.epichomes.menusystem.Menu;
import me.loving11ish.epichomes.menusystem.PlayerMenuUtility;
import me.loving11ish.epichomes.models.User;
import me.loving11ish.epichomes.utils.ColorUtils;
import me.loving11ish.epichomes.utils.MessageUtils;
import me.loving11ish.epichomes.utils.UsermapStorageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DeleteSingleGUI extends Menu {

    private final FoliaLib foliaLib = EpicHomes.getFoliaLib();
    private final UsermapStorageUtil usermapStorageUtil = EpicHomes.getPlugin().getUsermapStorageUtil();

    private static final String HOME_PLACEHOLDER = "%HOME%";
    private static final String HOME_LOCATION_PLACEHOLDER_WORLD = "%LOCATION-WORLD%";
    private static final String HOME_LOCATION_PLACEHOLDER_X = "%LOCATION-X%";
    private static final String HOME_LOCATION_PLACEHOLDER_Y = "%LOCATION-Y%";
    private static final String HOME_LOCATION_PLACEHOLDER_Z = "%LOCATION-Z%";

    public DeleteSingleGUI(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
    }

    @Override
    public String getMenuName() {
        return ColorUtils.translateColorCodes(EpicHomes.getPlugin().getConfigManager().getHomeSingleDeleteGUITitle())
                .replace(HOME_PLACEHOLDER, playerMenuUtility.getHomeName());
    }

    @Override
    public int getSlots() {
        return 9;
    }

    @Override
    public void handleMenu(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        User user = playerMenuUtility.getUser();
        String homeName = playerMenuUtility.getHomeName();

        if (event.getCurrentItem().getType().equals(EpicHomes.getPlugin().getConfigManager().getHomeSingleDeleteGUIConfirmItemMaterial())) {
            if (homeName != null && user != null) {
                List<String> userHomesList = usermapStorageUtil.getHomeNamesListByUser(user);

                for (String home : userHomesList) {
                    if (homeName.equalsIgnoreCase(home)) {
                        try {
                            if (usermapStorageUtil.removeHomeFromUser(user, homeName)) {

                                foliaLib.getImpl().runAsync((task) -> {
                                    fireHomeDeleteEvent(player, user, homeName);
                                    MessageUtils.sendDebugConsole("&aFired AsyncHomeDeleteEvent");
                                });

                                MessageUtils.sendPlayer(player, EpicHomes.getPlugin().getMessagesManager().getHomeDeleteSuccess()
                                        .replace(HOME_PLACEHOLDER, homeName));
                                player.closeInventory();
                            } else {
                                MessageUtils.sendPlayer(player, EpicHomes.getPlugin().getMessagesManager().getHomeDeleteFail()
                                        .replace(HOME_PLACEHOLDER, homeName));
                                player.closeInventory();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        else if (event.getCurrentItem().getType().equals(EpicHomes.getPlugin().getConfigManager().getHomeSingleDeleteGUICancelItemMaterial())) {
            player.closeInventory();
        }
    }

    @Override
    public void setMenuItems() {
        Location homeLocation = playerMenuUtility.getHomeLocation();

        //Home info item ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        ItemStack homeInfo = new ItemStack(EpicHomes.getPlugin().getConfigManager().getHomeSingleDeleteGUIHomeItemMaterial(), 1);
        ItemMeta homeInfoMeta = homeInfo.getItemMeta();
        homeInfoMeta.setDisplayName(ColorUtils.translateColorCodes(EpicHomes.getPlugin().getConfigManager().getHomeSingleDeleteGUIHomeItemName().replace(HOME_PLACEHOLDER, playerMenuUtility.getHomeName())));

        List<String> infoLoreConfigList = EpicHomes.getPlugin().getConfigManager().getHomeSingleDeleteGUIHomeItemLore();
        ArrayList<String> homeInfoLore = new ArrayList<>();
        for (String string : infoLoreConfigList) {
            homeInfoLore.add(ColorUtils.translateColorCodes(string)
                    .replace(HOME_LOCATION_PLACEHOLDER_WORLD, homeLocation.getWorld().getName())
                    .replace(HOME_LOCATION_PLACEHOLDER_X, String.valueOf(Math.round(homeLocation.getX())))
                    .replace(HOME_LOCATION_PLACEHOLDER_Y, String.valueOf(Math.round(homeLocation.getY())))
                    .replace(HOME_LOCATION_PLACEHOLDER_Z, String.valueOf(Math.round(homeLocation.getZ()))));
        }
        homeInfoMeta.setLore(homeInfoLore);
        homeInfo.setItemMeta(homeInfoMeta);

        //Home delete item ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        ItemStack homeDelete = new ItemStack(EpicHomes.getPlugin().getConfigManager().getHomeSingleDeleteGUIConfirmItemMaterial(), 1);
        ItemMeta homeDeleteMeta = homeDelete.getItemMeta();
        homeDeleteMeta.setDisplayName(ColorUtils.translateColorCodes(EpicHomes.getPlugin().getConfigManager().getHomeSingleDeleteGUIConfirmItemName().replace(HOME_PLACEHOLDER, playerMenuUtility.getHomeName())));

        List<String> deleteLoreConfigList = EpicHomes.getPlugin().getConfigManager().getHomeSingleDeleteGUIConfirmItemLore();
        ArrayList<String> homeDeleteLore = new ArrayList<>();
        for (String string : deleteLoreConfigList) {
            homeDeleteLore.add(ColorUtils.translateColorCodes(string));
        }
        homeDeleteMeta.setLore(homeDeleteLore);
        homeDelete.setItemMeta(homeDeleteMeta);

        //Cancel item ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        ItemStack cancelHomeDelete = new ItemStack(EpicHomes.getPlugin().getConfigManager().getHomeSingleDeleteGUICancelItemMaterial(), 1);
        ItemMeta cancelHomeDeleteMeta = cancelHomeDelete.getItemMeta();
        cancelHomeDeleteMeta.setDisplayName(ColorUtils.translateColorCodes(EpicHomes.getPlugin().getConfigManager().getHomeSingleDeleteGUICancelItemName().replace(HOME_PLACEHOLDER, playerMenuUtility.getHomeName())));

        List<String> cancelLoreConfigList = EpicHomes.getPlugin().getConfigManager().getHomeSingleDeleteGUICancelItemLore();
        ArrayList<String> cancelHomeDeleteLore = new ArrayList<>();
        for (String string : cancelLoreConfigList) {
            cancelHomeDeleteLore.add(ColorUtils.translateColorCodes(string));
        }
        cancelHomeDeleteMeta.setLore(cancelHomeDeleteLore);
        cancelHomeDelete.setItemMeta(cancelHomeDeleteMeta);

        inventory.setItem(1, homeDelete);
        inventory.setItem(4, homeInfo);
        inventory.setItem(7, cancelHomeDelete);
    }

    private static void fireHomeDeleteEvent(Player player, User user, String homeName) {
        AsyncHomeDeleteEvent asyncHomeDeleteEvent = new AsyncHomeDeleteEvent(player, user, homeName);
        Bukkit.getPluginManager().callEvent(asyncHomeDeleteEvent);
    }
}
