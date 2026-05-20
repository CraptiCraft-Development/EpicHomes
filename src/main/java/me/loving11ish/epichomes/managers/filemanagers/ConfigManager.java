package me.loving11ish.epichomes.managers.filemanagers;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;
import java.util.Map;

public class ConfigManager {

    private final FileConfiguration config;

    // Booleans
    private boolean debugMode = false;
    private boolean updateNotifications = false;
    private boolean useTieredHomeLimit = false;
    private boolean useDelayBeforeHomeTP = false;
    private boolean useMovementTPCancel = false;
    private boolean useCommandCooldown = false;
    private boolean useDataImporter = false;
    private boolean showAutoSaveConsoleMessage = false;
    private boolean useGUI = false;
    private boolean extraHomePurchaseEnabled = false;

    // Integers
    private int defaultHomeLimit = 3;
    private int teleportDelayTime = 5;
    private int commandCooldownTime = 60;
    private int extraHomePurchaseMinimumExtraHomes = 0;
    private int extraHomePurchaseMaximumExtraHomes = 10;

    // Doubles
    private double extraHomePurchaseBasePrice = 1000.0D;
    private double extraHomePurchasePriceModifier = 1.0D;

    // Strings
    private String dataImportPlugin;
    private String homeListGUITitle;
    private String homeListGUIItemName;
    private String homeListGUIBuyExtraHomeItemName;
    private String homeListDeleteGUITitle;
    private String homeListDeleteGUIItemName;
    private String homeSingleDeleteGUITitle;
    private String homeSingleDeleteGUIHomeItemName;
    private String homeSingleDeleteGUIConfirmItemName;
    private String homeSingleDeleteGUICancelItemName;
    private String buyExtraHomeGUITitle;
    private String buyExtraHomeGUIInfoItemName;
    private String buyExtraHomeGUIConfirmItemName;
    private String buyExtraHomeGUICancelItemName;
    private String menuPreviousPageItemName;
    private String menuNextPageItemName;
    private String menuCloseItemName;

    // Lists
    private List<String> bannedHomeNames;
    private List<String> homeListGUIItemLore;
    private List<String> homeListGUIBuyExtraHomeItemLore;
    private List<String> homeListDeleteGUIItemLore;
    private List<String> homeSingleDeleteGUIHomeItemLore;
    private List<String> homeSingleDeleteGUIConfirmItemLore;
    private List<String> homeSingleDeleteGUICancelItemLore;
    private List<String> buyExtraHomeGUIInfoItemLore;
    private List<String> buyExtraHomeGUIConfirmItemLore;
    private List<String> buyExtraHomeGUICancelItemLore;

    // Maps
    private List<Map<?, ?>> tieredHomesMaxAmountGroups;

    // Materials
    private Material homeListGUIItemMaterial;
    private Material homeListGUIBuyExtraHomeItemMaterial;
    private Material homeListDeleteGUIItemMaterial;
    private Material homeSingleDeleteGUIHomeItemMaterial;
    private Material homeSingleDeleteGUIConfirmItemMaterial;
    private Material homeSingleDeleteGUICancelItemMaterial;
    private Material buyExtraHomeGUIInfoItemMaterial;
    private Material buyExtraHomeGUIConfirmItemMaterial;
    private Material buyExtraHomeGUICancelItemMaterial;


    public ConfigManager(FileConfiguration config) {
        this.config = config;
    }

    public void loadConfigValues() {
        // Booleans
        debugMode = config.getBoolean("general.developer-debug-mode.enabled", false);
        updateNotifications = config.getBoolean("plugin-update-notifications.enabled", true);
        useTieredHomeLimit = config.getBoolean("homes.permission-based-homes-max-amount.enabled", false);
        useDelayBeforeHomeTP = config.getBoolean("homes.teleportation.delay-before-teleport.enabled", true);
        useMovementTPCancel = config.getBoolean("homes.teleportation.delay-before-teleport.cancel-teleport-on-move", true);
        useCommandCooldown = config.getBoolean("general.command-cool-down.enabled", false);
        useDataImporter = config.getBoolean("general.home-data-importer.enabled", false);
        showAutoSaveConsoleMessage = config.getBoolean("general.show-auto-save-task-message.enabled", true);
        useGUI = config.getBoolean("gui-system.use-global-gui.enabled", true);
        extraHomePurchaseEnabled = config.getBoolean("homes.extra-home-purchase.enabled", false);

        // Integers
        defaultHomeLimit = config.getInt("homes.default-max-homes", 3);
        teleportDelayTime = config.getInt("homes.teleportation.delay-before-teleport.time", 5);
        commandCooldownTime = config.getInt("general.command-cool-down.cool-down-time", 60);
        extraHomePurchaseMinimumExtraHomes = Math.max(0, config.getInt("homes.extra-home-purchase.minimum-extra-homes", 0));
        extraHomePurchaseMaximumExtraHomes = Math.max(extraHomePurchaseMinimumExtraHomes, config.getInt("homes.extra-home-purchase.maximum-extra-homes", 10));

        // Doubles
        extraHomePurchaseBasePrice = Math.max(0.0D, config.getDouble("homes.extra-home-purchase.base-price", 1000.0D));
        extraHomePurchasePriceModifier = Math.max(0.2D, Math.min(2.0D, config.getDouble("homes.extra-home-purchase.price-modifier", 1.0D)));

        // Strings
        dataImportPlugin = config.getString("general.home-data-importer.import-from", "EssentialsX");
        homeListGUITitle = config.getString("gui-system.home-list-gui.title", "&d&nYour Homes:");
        homeListGUIItemName = config.getString("gui-system.home-list-gui.icons.display-name", "&6%HOME%");
        homeListGUIBuyExtraHomeItemName = config.getString("gui-system.home-list-gui.icons.buy-extra-home.display-name", "&aBuy Extra Home");
        homeListDeleteGUITitle = config.getString("gui-system.delete-list-gui.title", "&d&nSelect Home To Delete");
        homeListDeleteGUIItemName = config.getString("gui-system.delete-list-gui.icons.display-name", "&6%HOME%");
        homeSingleDeleteGUITitle = config.getString("gui-system.delete-single-home-gui.title", "&d&nDelete %HOME%");
        homeSingleDeleteGUIHomeItemName = config.getString("gui-system.delete-single-home-gui.icons.home-info.display-name", "&6%HOME%");
        homeSingleDeleteGUIConfirmItemName = config.getString("gui-system.delete-single-home-gui.icons.confirm-delete.display-name", "&cConfirm");
        homeSingleDeleteGUICancelItemName = config.getString("gui-system.delete-single-home-gui.icons.cancel-delete.display-name", "&aCancel");
        buyExtraHomeGUITitle = config.getString("gui-system.buy-extra-home-gui.title", "&d&nBuy Extra Home");
        buyExtraHomeGUIInfoItemName = config.getString("gui-system.buy-extra-home-gui.icons.purchase-info.display-name", "&6Extra Home Slot");
        buyExtraHomeGUIConfirmItemName = config.getString("gui-system.buy-extra-home-gui.icons.confirm-purchase.display-name", "&aConfirm");
        buyExtraHomeGUICancelItemName = config.getString("gui-system.buy-extra-home-gui.icons.cancel-purchase.display-name", "&cCancel");
        menuPreviousPageItemName = config.getString("gui-system.menu-controls.previous-page-icon-name", "&2Previous Page");
        menuNextPageItemName = config.getString("gui-system.menu-controls.next-page-icon-name", "&2Next Page");
        menuCloseItemName = config.getString("gui-system.menu-controls.close-go-back-icon-name", "&4Close/Go Back");

        // Lists
        bannedHomeNames = config.getStringList("homes.disallowed-home-names");
        homeListGUIItemLore = config.getStringList("gui-system.home-list-gui.icons.lore");
        homeListGUIBuyExtraHomeItemLore = config.getStringList("gui-system.home-list-gui.icons.buy-extra-home.lore");
        homeListDeleteGUIItemLore = config.getStringList("gui-system.delete-list-gui.icons.lore");
        homeSingleDeleteGUIHomeItemLore = config.getStringList("gui-system.delete-single-home-gui.icons.home-info.lore");
        homeSingleDeleteGUIConfirmItemLore = config.getStringList("gui-system.delete-single-home-gui.icons.confirm-delete.lore");
        homeSingleDeleteGUICancelItemLore = config.getStringList("gui-system.delete-single-home-gui.icons.cancel-delete.lore");
        buyExtraHomeGUIInfoItemLore = config.getStringList("gui-system.buy-extra-home-gui.icons.purchase-info.lore");
        buyExtraHomeGUIConfirmItemLore = config.getStringList("gui-system.buy-extra-home-gui.icons.confirm-purchase.lore");
        buyExtraHomeGUICancelItemLore = config.getStringList("gui-system.buy-extra-home-gui.icons.cancel-purchase.lore");

        // Maps
        tieredHomesMaxAmountGroups = config.getMapList("homes.permission-based-homes-max-amount.permission-group-list");

        // Materials
        homeListGUIItemMaterial = Material.getMaterial(config.getString("gui-system.home-list-gui.icons.home-material", "RED_BED"));
        homeListGUIBuyExtraHomeItemMaterial = getMaterial("gui-system.home-list-gui.icons.buy-extra-home.material", "EMERALD");
        homeListDeleteGUIItemMaterial = Material.getMaterial(config.getString("gui-system.delete-list-gui.icons.home-material", "RED_BED"));
        homeSingleDeleteGUIHomeItemMaterial = Material.getMaterial(config.getString("gui-system.delete-single-home-gui.icons.home-info.home-material", "RED_BED"));
        homeSingleDeleteGUIConfirmItemMaterial = Material.getMaterial(config.getString("gui-system.delete-single-home-gui.icons.confirm-delete.material", "TNT"));
        homeSingleDeleteGUICancelItemMaterial = Material.getMaterial(config.getString("gui-system.delete-single-home-gui.icons.cancel-delete.material", "BARRIER"));
        buyExtraHomeGUIInfoItemMaterial = getMaterial("gui-system.buy-extra-home-gui.icons.purchase-info.material", "EMERALD");
        buyExtraHomeGUIConfirmItemMaterial = getMaterial("gui-system.buy-extra-home-gui.icons.confirm-purchase.material", "EMERALD_BLOCK");
        buyExtraHomeGUICancelItemMaterial = getMaterial("gui-system.buy-extra-home-gui.icons.cancel-purchase.material", "BARRIER");
    }

    // Getters

    private Material getMaterial(String path, String defaultValue) {
        Material material = Material.getMaterial(config.getString(path, defaultValue));
        if (material == null) {
            return Material.getMaterial(defaultValue);
        }
        return material;
    }

    public boolean isDebugMode() {
        return debugMode;
    }

    public boolean isUpdateNotifications() {
        return updateNotifications;
    }

    public boolean isUseTieredHomeLimit() {
        return useTieredHomeLimit;
    }

    public boolean isUseDelayBeforeHomeTP() {
        return useDelayBeforeHomeTP;
    }

    public boolean isUseMovementTPCancel() {
        return useMovementTPCancel;
    }

    public boolean isUseCommandCooldown() {
        return useCommandCooldown;
    }

    public boolean isUseDataImporter() {
        return useDataImporter;
    }

    public boolean isShowAutoSaveConsoleMessage() {
        return showAutoSaveConsoleMessage;
    }

    public boolean isUseGUI() {
        return useGUI;
    }

    public boolean isExtraHomePurchaseEnabled() {
        return extraHomePurchaseEnabled;
    }

    public int getDefaultHomeLimit() {
        return defaultHomeLimit;
    }

    public int getTeleportDelayTime() {
        return teleportDelayTime;
    }

    public int getCommandCooldownTime() {
        return commandCooldownTime;
    }

    public int getExtraHomePurchaseMinimumExtraHomes() {
        return extraHomePurchaseMinimumExtraHomes;
    }

    public int getExtraHomePurchaseMaximumExtraHomes() {
        return extraHomePurchaseMaximumExtraHomes;
    }

    public double getExtraHomePurchaseBasePrice() {
        return extraHomePurchaseBasePrice;
    }

    public double getExtraHomePurchasePriceModifier() {
        return extraHomePurchasePriceModifier;
    }

    public String getDataImportPlugin() {
        return dataImportPlugin;
    }

    public String getHomeListGUITitle() {
        return homeListGUITitle;
    }

    public String getHomeListGUIItemName() {
        return homeListGUIItemName;
    }

    public String getHomeListGUIBuyExtraHomeItemName() {
        return homeListGUIBuyExtraHomeItemName;
    }

    public String getHomeListDeleteGUITitle() {
        return homeListDeleteGUITitle;
    }

    public String getHomeListDeleteGUIItemName() {
        return homeListDeleteGUIItemName;
    }

    public String getHomeSingleDeleteGUITitle() {
        return homeSingleDeleteGUITitle;
    }

    public String getHomeSingleDeleteGUIHomeItemName() {
        return homeSingleDeleteGUIHomeItemName;
    }

    public String getHomeSingleDeleteGUIConfirmItemName() {
        return homeSingleDeleteGUIConfirmItemName;
    }

    public String getHomeSingleDeleteGUICancelItemName() {
        return homeSingleDeleteGUICancelItemName;
    }

    public String getBuyExtraHomeGUITitle() {
        return buyExtraHomeGUITitle;
    }

    public String getBuyExtraHomeGUIInfoItemName() {
        return buyExtraHomeGUIInfoItemName;
    }

    public String getBuyExtraHomeGUIConfirmItemName() {
        return buyExtraHomeGUIConfirmItemName;
    }

    public String getBuyExtraHomeGUICancelItemName() {
        return buyExtraHomeGUICancelItemName;
    }

    public String getMenuPreviousPageItemName() {
        return menuPreviousPageItemName;
    }

    public String getMenuNextPageItemName() {
        return menuNextPageItemName;
    }

    public String getMenuCloseItemName() {
        return menuCloseItemName;
    }

    public List<String> getHomeListGUIItemLore() {
        return homeListGUIItemLore;
    }

    public List<String> getHomeListGUIBuyExtraHomeItemLore() {
        return homeListGUIBuyExtraHomeItemLore;
    }

    public List<String> getBannedHomeNames() {
        return bannedHomeNames;
    }

    public List<String> getHomeListDeleteGUIItemLore() {
        return homeListDeleteGUIItemLore;
    }

    public List<String> getHomeSingleDeleteGUIHomeItemLore() {
        return homeSingleDeleteGUIHomeItemLore;
    }

    public List<String> getHomeSingleDeleteGUIConfirmItemLore() {
        return homeSingleDeleteGUIConfirmItemLore;
    }

    public List<String> getHomeSingleDeleteGUICancelItemLore() {
        return homeSingleDeleteGUICancelItemLore;
    }

    public List<String> getBuyExtraHomeGUIInfoItemLore() {
        return buyExtraHomeGUIInfoItemLore;
    }

    public List<String> getBuyExtraHomeGUIConfirmItemLore() {
        return buyExtraHomeGUIConfirmItemLore;
    }

    public List<String> getBuyExtraHomeGUICancelItemLore() {
        return buyExtraHomeGUICancelItemLore;
    }

    public List<Map<?, ?>> getTieredHomesMaxAmountGroups() {
        return tieredHomesMaxAmountGroups;
    }

    public Material getHomeListGUIItemMaterial() {
        return homeListGUIItemMaterial;
    }

    public Material getHomeListGUIBuyExtraHomeItemMaterial() {
        return homeListGUIBuyExtraHomeItemMaterial;
    }

    public Material getHomeListDeleteGUIItemMaterial() {
        return homeListDeleteGUIItemMaterial;
    }

    public Material getHomeSingleDeleteGUIHomeItemMaterial() {
        return homeSingleDeleteGUIHomeItemMaterial;
    }

    public Material getHomeSingleDeleteGUIConfirmItemMaterial() {
        return homeSingleDeleteGUIConfirmItemMaterial;
    }

    public Material getHomeSingleDeleteGUICancelItemMaterial() {
        return homeSingleDeleteGUICancelItemMaterial;
    }

    public Material getBuyExtraHomeGUIInfoItemMaterial() {
        return buyExtraHomeGUIInfoItemMaterial;
    }

    public Material getBuyExtraHomeGUIConfirmItemMaterial() {
        return buyExtraHomeGUIConfirmItemMaterial;
    }

    public Material getBuyExtraHomeGUICancelItemMaterial() {
        return buyExtraHomeGUICancelItemMaterial;
    }
}
