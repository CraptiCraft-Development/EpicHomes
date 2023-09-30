package me.loving11ish.epichomes.menusystem.menus;

import me.loving11ish.epichomes.EpicHomes;
import me.loving11ish.epichomes.api.HomeDeleteEvent;
import me.loving11ish.epichomes.menusystem.Menu;
import me.loving11ish.epichomes.menusystem.PlayerMenuUtility;
import me.loving11ish.epichomes.models.User;
import me.loving11ish.epichomes.utils.ColorUtils;
import me.loving11ish.epichomes.utils.UsermapStorageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DeleteSingleGUI extends Menu {

    ConsoleCommandSender console = Bukkit.getConsoleSender();

    FileConfiguration config = EpicHomes.getPlugin().getConfig();
    FileConfiguration messagesConfig = EpicHomes.getPlugin().messagesFileManager.getMessagesConfig();

    private UsermapStorageUtil usermapStorageUtil = EpicHomes.getPlugin().usermapStorageUtil;

    private String prefix = messagesConfig.getString("global-prefix");
    private static final String PREFIX_PLACEHOLDER = "%PREFIX%";
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
        return ColorUtils.translateColorCodes(config.getString("gui-system.delete-single-home-gui.title")
                .replace(PREFIX_PLACEHOLDER, prefix).replace(HOME_PLACEHOLDER, playerMenuUtility.getHomeName()));
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
        if (event.getCurrentItem().getType().equals(Material.valueOf(config.getString("gui-system.delete-single-home-gui.icons.confirm-delete.material")))){
            if (homeName != null && user != null){
                List<String> userHomesList = usermapStorageUtil.getHomeNamesListByUser(user);
                for (String home : userHomesList){
                    if (homeName.equalsIgnoreCase(home)){
                        try {
                            if (usermapStorageUtil.removeHomeFromUser(user, homeName)){
                                fireHomeDeleteEvent(player, user, homeName);
                                if (config.getBoolean("general.developer-debug-mode.enabled")){
                                    console.sendMessage(ColorUtils.translateColorCodes("&6EpicHomes-Debug: &aFired HomeDeleteEvent"));
                                }
                                player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("home-delete-successful")
                                        .replace(PREFIX_PLACEHOLDER, prefix)
                                        .replace(HOME_PLACEHOLDER, homeName)));
                                player.closeInventory();
                            }else {
                                player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("home-delete-failed")
                                        .replace(PREFIX_PLACEHOLDER, prefix)
                                        .replace(HOME_PLACEHOLDER, homeName)));
                                player.closeInventory();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }else if (event.getCurrentItem().getType().equals(Material.valueOf(config.getString("gui-system.delete-single-home-gui.icons.cancel-delete.material")))){
            player.closeInventory();
        }
    }

    @Override
    public void setMenuItems() {
        Location homeLocation = playerMenuUtility.getHomeLocation();

        //Home info item ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        ItemStack homeInfo = new ItemStack(Material.valueOf(config.getString("gui-system.delete-single-home-gui.icons.home-info.home-material")), 1);
        ItemMeta homeInfoMeta = homeInfo.getItemMeta();
        homeInfoMeta.setDisplayName(ColorUtils.translateColorCodes(config.getString("gui-system.delete-single-home-gui.icons.home-info.display-name").replace(HOME_PLACEHOLDER, playerMenuUtility.getHomeName())));
        List<String> infoLoreConfigList = config.getStringList("gui-system.delete-single-home-gui.icons.home-info.lore");
        ArrayList<String> homeInfoLore = new ArrayList<>();
        for (String string : infoLoreConfigList){
            homeInfoLore.add(ColorUtils.translateColorCodes(string)
                    .replace(HOME_LOCATION_PLACEHOLDER_WORLD, homeLocation.getWorld().getName())
                    .replace(HOME_LOCATION_PLACEHOLDER_X, String.valueOf(Math.round(homeLocation.getX())))
                    .replace(HOME_LOCATION_PLACEHOLDER_Y, String.valueOf(Math.round(homeLocation.getY())))
                    .replace(HOME_LOCATION_PLACEHOLDER_Z, String.valueOf(Math.round(homeLocation.getZ())))
                    .replace(PREFIX_PLACEHOLDER, prefix));
        }
        homeInfoMeta.setLore(homeInfoLore);
        homeInfo.setItemMeta(homeInfoMeta);

        //Home delete item ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        ItemStack homeDelete = new ItemStack(Material.valueOf(config.getString("gui-system.delete-single-home-gui.icons.confirm-delete.material")), 1);
        ItemMeta homeDeleteMeta = homeDelete.getItemMeta();
        homeDeleteMeta.setDisplayName(ColorUtils.translateColorCodes(config.getString("gui-system.delete-single-home-gui.icons.confirm-delete.display-name").replace(HOME_PLACEHOLDER, playerMenuUtility.getHomeName())));
        List<String> deleteLoreConfigList = config.getStringList("gui-system.delete-single-home-gui.icons.confirm-delete.lore");
        ArrayList<String> homeDeleteLore = new ArrayList<>();
        for (String string : deleteLoreConfigList){
            homeDeleteLore.add(ColorUtils.translateColorCodes(string)
                    .replace(PREFIX_PLACEHOLDER, prefix));
        }
        homeDeleteMeta.setLore(homeDeleteLore);
        homeDelete.setItemMeta(homeDeleteMeta);

        //Cancel item ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        ItemStack cancelHomeDelete = new ItemStack(Material.valueOf(config.getString("gui-system.delete-single-home-gui.icons.cancel-delete.material")), 1);
        ItemMeta cancelHomeDeleteMeta = cancelHomeDelete.getItemMeta();
        cancelHomeDeleteMeta.setDisplayName(ColorUtils.translateColorCodes(config.getString("gui-system.delete-single-home-gui.icons.cancel-delete.display-name").replace(HOME_PLACEHOLDER, playerMenuUtility.getHomeName())));
        List<String> cancelLoreConfigList = config.getStringList("gui-system.delete-single-home-gui.icons.cancel-delete.lore");
        ArrayList<String> cancelHomeDeleteLore = new ArrayList<>();
        for (String string : cancelLoreConfigList){
            cancelHomeDeleteLore.add(ColorUtils.translateColorCodes(string)
                    .replace(PREFIX_PLACEHOLDER, prefix));
        }
        cancelHomeDeleteMeta.setLore(cancelHomeDeleteLore);
        cancelHomeDelete.setItemMeta(cancelHomeDeleteMeta);

        inventory.setItem(1, homeDelete);
        inventory.setItem(4, homeInfo);
        inventory.setItem(7, cancelHomeDelete);
    }

    private static void fireHomeDeleteEvent(Player player, User user, String homeName) {
        HomeDeleteEvent homeDeleteEvent = new HomeDeleteEvent(player, user, homeName);
        Bukkit.getPluginManager().callEvent(homeDeleteEvent);
    }
}
