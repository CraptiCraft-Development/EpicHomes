package me.loving11ish.epichomes.menusystem.menus;

import me.loving11ish.epichomes.EpicHomes;
import me.loving11ish.epichomes.menusystem.Menu;
import me.loving11ish.epichomes.menusystem.PlayerMenuUtility;
import me.loving11ish.epichomes.models.HomePurchaseContext;
import me.loving11ish.epichomes.utils.ColorUtils;
import me.loving11ish.epichomes.utils.HomePurchaseUtil;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class BuyExtraHomeGUI extends Menu {

    private static final String CONFIRM_ACTION = "confirm";
    private static final String CANCEL_ACTION = "cancel";

    private final HomePurchaseUtil homePurchaseUtil = new HomePurchaseUtil();

    public BuyExtraHomeGUI(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
    }

    @Override
    public String getMenuName() {
        return ColorUtils.translateColorCodes(EpicHomes.getPlugin().getConfigManager().getBuyExtraHomeGUITitle());
    }

    @Override
    public int getSlots() {
        return 9;
    }

    @Override
    public void handleMenu(InventoryClickEvent event) {
        if (event.getCurrentItem() == null || !event.getCurrentItem().hasItemMeta()) {
            return;
        }

        ItemMeta itemMeta = event.getCurrentItem().getItemMeta();
        String action = itemMeta.getPersistentDataContainer().get(getActionKey(), PersistentDataType.STRING);
        Player player = (Player) event.getWhoClicked();

        if (CONFIRM_ACTION.equals(action)) {
            homePurchaseUtil.purchaseExtraHome(player);
            player.closeInventory();
        } else if (CANCEL_ACTION.equals(action)) {
            player.closeInventory();
        }
    }

    @Override
    public void setMenuItems() {
        Player player = playerMenuUtility.getOwner();
        HomePurchaseContext context = homePurchaseUtil.getPurchaseContext(player);

        ItemStack purchaseInfo = makePurchaseItem(
                EpicHomes.getPlugin().getConfigManager().getBuyExtraHomeGUIInfoItemMaterial(),
                EpicHomes.getPlugin().getConfigManager().getBuyExtraHomeGUIInfoItemName(),
                EpicHomes.getPlugin().getConfigManager().getBuyExtraHomeGUIInfoItemLore(),
                null,
                context
        );

        ItemStack confirmPurchase = makePurchaseItem(
                EpicHomes.getPlugin().getConfigManager().getBuyExtraHomeGUIConfirmItemMaterial(),
                EpicHomes.getPlugin().getConfigManager().getBuyExtraHomeGUIConfirmItemName(),
                EpicHomes.getPlugin().getConfigManager().getBuyExtraHomeGUIConfirmItemLore(),
                CONFIRM_ACTION,
                context
        );

        ItemStack cancelPurchase = makePurchaseItem(
                EpicHomes.getPlugin().getConfigManager().getBuyExtraHomeGUICancelItemMaterial(),
                EpicHomes.getPlugin().getConfigManager().getBuyExtraHomeGUICancelItemName(),
                EpicHomes.getPlugin().getConfigManager().getBuyExtraHomeGUICancelItemLore(),
                CANCEL_ACTION,
                context
        );

        inventory.setItem(1, confirmPurchase);
        inventory.setItem(4, purchaseInfo);
        inventory.setItem(7, cancelPurchase);
    }

    private ItemStack makePurchaseItem(Material material, String displayName, List<String> loreConfigList,
                                       String action, HomePurchaseContext context) {
        ItemStack itemStack = new ItemStack(material, 1);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ColorUtils.translateColorCodes(homePurchaseUtil.applyPlaceholders(displayName, context)));

        ArrayList<String> lore = new ArrayList<>();
        for (String string : loreConfigList) {
            lore.add(ColorUtils.translateColorCodes(homePurchaseUtil.applyPlaceholders(string, context)));
        }
        itemMeta.setLore(lore);

        if (action != null) {
            itemMeta.getPersistentDataContainer().set(getActionKey(), PersistentDataType.STRING, action);
        }

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    private NamespacedKey getActionKey() {
        return new NamespacedKey(EpicHomes.getPlugin(), "extraHomePurchaseAction");
    }
}
