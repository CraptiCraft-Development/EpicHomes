package me.loving11ish.epichomes.menusystem.paginatedmenus;

import me.loving11ish.epichomes.EpicHomes;
import me.loving11ish.epichomes.menusystem.PaginatedMenu;
import me.loving11ish.epichomes.menusystem.PlayerMenuUtility;
import me.loving11ish.epichomes.menusystem.menus.DeleteSingleGUI;
import me.loving11ish.epichomes.models.User;
import me.loving11ish.epichomes.utils.ColorUtils;
import me.loving11ish.epichomes.utils.UsermapStorageUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class DeleteHomesListGUI extends PaginatedMenu {

    private final FileConfiguration config = EpicHomes.getPlugin().getConfig();
    private final FileConfiguration messagesConfig = EpicHomes.getPlugin().messagesFileManager.getMessagesConfig();

    private static final String PREFIX_PLACEHOLDER = "%PREFIX%";
    private static final String HOME_NAME_PLACEHOLDER = "%HOME%";
    private static final String HOME_LOCATION_PLACEHOLDER_WORLD = "%LOCATION-WORLD%";
    private static final String HOME_LOCATION_PLACEHOLDER_X = "%LOCATION-X%";
    private static final String HOME_LOCATION_PLACEHOLDER_Y = "%LOCATION-Y%";
    private static final String HOME_LOCATION_PLACEHOLDER_Z = "%LOCATION-Z%";

    private final String prefix = messagesConfig.getString("global-prefix", "&f[&6Epic&bHomes&f]&r");
    private final UsermapStorageUtil usermapStorageUtil = EpicHomes.getPlugin().usermapStorageUtil;


    public DeleteHomesListGUI(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
    }

    @Override
    public String getMenuName() {
        return ColorUtils.translateColorCodes(config.getString("gui-system.delete-list-gui.title")
                .replace(PREFIX_PLACEHOLDER, prefix));
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

        if (event.isLeftClick()){
            if (event.getCurrentItem().getType().equals(Material.valueOf(config.getString("gui-system.delete-list-gui.icons.home-material")))){
                String homeName = event.getCurrentItem().getItemMeta().getPersistentDataContainer().get(new NamespacedKey(EpicHomes.getPlugin(), "homeName"), PersistentDataType.STRING);
                if (homeName != null){
                    playerMenuUtility.setHomeName(homeName);
                    playerMenuUtility.setHomeLocation(usermapStorageUtil.getHomeLocationByHomeName(user, homeName));
                    new DeleteSingleGUI(playerMenuUtility).open();
                }
            }else if (event.getCurrentItem().getType().equals(Material.BARRIER)) {
                player.closeInventory();
            }

            else if(event.getCurrentItem().getType().equals(Material.STONE_BUTTON)){
                if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ColorUtils.translateColorCodes(config.getString("gui-system.menu-controls.previous-page-icon-name")))){
                    if (page == 0){
                        player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("GUI-first-page").replace(PREFIX_PLACEHOLDER, prefix)));
                    }else{
                        page = page - 1;
                        super.open();
                    }
                }else if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ColorUtils.translateColorCodes(config.getString("gui-system.menu-controls.next-page-icon-name")))){
                    if (!((index + 1) >= userHomesList.size())){
                        page = page + 1;
                        super.open();
                    }else{
                        player.sendMessage(ColorUtils.translateColorCodes(messagesConfig.getString("GUI-last-page").replace(PREFIX_PLACEHOLDER, prefix)));
                    }
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

                    ItemStack homeItem = new ItemStack(Material.valueOf(config.getString("gui-system.delete-list-gui.icons.home-material")), 1);
                    ItemMeta itemMeta = homeItem.getItemMeta();
                    itemMeta.setDisplayName(ColorUtils.translateColorCodes(config.getString("gui-system.delete-list-gui.icons.display-name")
                            .replace(HOME_NAME_PLACEHOLDER, homeName).replace(PREFIX_PLACEHOLDER, prefix)));

                    List<String> loreConfigList = config.getStringList("gui-system.delete-list-gui.icons.lore");
                    ArrayList<String> homeLore = new ArrayList<>();
                    for (String string : loreConfigList){
                        homeLore.add(ColorUtils.translateColorCodes(string)
                                .replace(HOME_LOCATION_PLACEHOLDER_WORLD, homeLocation.getWorld().getName())
                                .replace(HOME_LOCATION_PLACEHOLDER_X, String.valueOf(Math.round(homeLocation.getX())))
                                .replace(HOME_LOCATION_PLACEHOLDER_Y, String.valueOf(Math.round(homeLocation.getY())))
                                .replace(HOME_LOCATION_PLACEHOLDER_Z, String.valueOf(Math.round(homeLocation.getZ())))
                                .replace(PREFIX_PLACEHOLDER, prefix));
                    }
                    itemMeta.setLore(homeLore);
                    itemMeta.getPersistentDataContainer().set(new NamespacedKey(EpicHomes.getPlugin(), "homeName"), PersistentDataType.STRING, homeName);
                    homeItem.setItemMeta(itemMeta);

                    inventory.addItem(homeItem);
                }
            }
        }
    }
}
