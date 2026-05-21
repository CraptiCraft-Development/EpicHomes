package me.loving11ish.epichomes.utils;

import me.loving11ish.epichomes.EpicHomes;
import me.loving11ish.epichomes.externalhooks.VaultEconomyHook;
import me.loving11ish.epichomes.models.User;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.Map;

public class HomePurchaseUtil {

    public static final String PRICE_PLACEHOLDER = "%PRICE%";
    public static final String CURRENT_LIMIT_PLACEHOLDER = "%CURRENT-LIMIT%";
    public static final String NEXT_LIMIT_PLACEHOLDER = "%NEXT-LIMIT%";
    public static final String EXTRA_HOMES_PLACEHOLDER = "%EXTRA-HOMES%";
    public static final String NEXT_EXTRA_HOMES_PLACEHOLDER = "%NEXT-EXTRA-HOMES%";
    public static final String MAX_EXTRA_HOMES_PLACEHOLDER = "%MAX-EXTRA-HOMES%";
    public static final String MIN_EXTRA_HOMES_PLACEHOLDER = "%MIN-EXTRA-HOMES%";

    private final UsermapStorageUtil usermapStorageUtil = EpicHomes.getPlugin().getUsermapStorageUtil();
    private final VaultEconomyHook economyHook = new VaultEconomyHook();

    public PurchaseContext getPurchaseContext(Player player) {
        User user = usermapStorageUtil.getUserByOnlinePlayer(player);
        int baseHomeLimit = getBaseHomeLimit(player);
        int extraHomes = getEffectiveExtraHomes(user);
        int maximumExtraHomes = EpicHomes.getPlugin().getConfigManager().getExtraHomePurchaseMaximumExtraHomes();
        int minimumExtraHomes = EpicHomes.getPlugin().getConfigManager().getExtraHomePurchaseMinimumExtraHomes();
        int nextExtraHomes = extraHomes >= maximumExtraHomes ? extraHomes : extraHomes + 1;
        double price = getNextHomePrice(extraHomes);

        return new PurchaseContext(user, price, baseHomeLimit, extraHomes, nextExtraHomes, minimumExtraHomes, maximumExtraHomes);
    }

    public boolean sendPurchaseDisclaimer(Player player) {
        PurchaseContext context = getPurchaseContext(player);
        if (!validatePurchasePreconditions(player, context)) {
            return true;
        }

        MessageUtils.sendPlayer(player, applyPlaceholders(EpicHomes.getPlugin().getMessagesManager().getHomeBuyPriceDisclaimer(), context));
        return true;
    }

    public boolean purchaseExtraHome(Player player) {
        PurchaseContext context = getPurchaseContext(player);
        if (!validatePurchasePreconditions(player, context)) {
            return true;
        }

        if (!economyHook.has(player, context.getPrice())) {
            MessageUtils.sendPlayer(player, applyPlaceholders(EpicHomes.getPlugin().getMessagesManager().getHomeBuyInsufficientFunds(), context));
            return true;
        }

        if (!economyHook.withdraw(player, context.getPrice())) {
            MessageUtils.sendPlayer(player, EpicHomes.getPlugin().getMessagesManager().getHomeBuyFailed());
            return true;
        }

        int previousExtraHomes = context.getUser().getPurchasedExtraHomes();
        usermapStorageUtil.setPurchasedExtraHomes(context.getUser(), context.getNextExtraHomes());

        try {
            usermapStorageUtil.saveUsermap();
        } catch (IOException e) {
            usermapStorageUtil.setPurchasedExtraHomes(context.getUser(), previousExtraHomes);
            economyHook.deposit(player, context.getPrice());
            MessageUtils.sendPlayer(player, EpicHomes.getPlugin().getMessagesManager().getHomeBuyFailed());
            e.printStackTrace();
            return true;
        }

        PurchaseContext updatedContext = getPurchaseContext(player);
        MessageUtils.sendPlayer(player, applyPlaceholders(EpicHomes.getPlugin().getMessagesManager().getHomeBuySuccess(), updatedContext));
        return true;
    }

    public int getPlayerHomeLimit(Player player) {
        User user = usermapStorageUtil.getUserByOnlinePlayer(player);
        return getBaseHomeLimit(player) + getEffectiveExtraHomes(user);
    }

    public int getBaseHomeLimit(Player player) {
        if (EpicHomes.getPlugin().getConfigManager().isUseTieredHomeLimit()) {
            for (Map<?, ?> groupMap : EpicHomes.getPlugin().getConfigManager().getTieredHomesMaxAmountGroups()) {
                String group = (String) groupMap.get("group");
                int maxAmount = (Integer) groupMap.get("maxAmount");

                if (player.hasPermission(group)) {
                    return maxAmount;
                }
            }
        }

        return EpicHomes.getPlugin().getConfigManager().getDefaultHomeLimit();
    }

    public int getEffectiveExtraHomes(User user) {
        if (user == null) {
            return EpicHomes.getPlugin().getConfigManager().getExtraHomePurchaseMinimumExtraHomes();
        }

        return Math.max(user.getPurchasedExtraHomes(), EpicHomes.getPlugin().getConfigManager().getExtraHomePurchaseMinimumExtraHomes());
    }

    public boolean isEconomyAvailable() {
        return economyHook.isAvailable();
    }

    public String applyPlaceholders(String text, Player player) {
        return applyPlaceholders(text, getPurchaseContext(player));
    }

    public String applyPlaceholders(String text, PurchaseContext context) {
        return text.replace(PRICE_PLACEHOLDER, economyHook.format(context.getPrice()))
                .replace(CURRENT_LIMIT_PLACEHOLDER, String.valueOf(context.getCurrentHomeLimit()))
                .replace(NEXT_LIMIT_PLACEHOLDER, String.valueOf(context.getNextHomeLimit()))
                .replace(EXTRA_HOMES_PLACEHOLDER, String.valueOf(context.getExtraHomes()))
                .replace(NEXT_EXTRA_HOMES_PLACEHOLDER, String.valueOf(context.getNextExtraHomes()))
                .replace(MAX_EXTRA_HOMES_PLACEHOLDER, String.valueOf(context.getMaximumExtraHomes()))
                .replace(MIN_EXTRA_HOMES_PLACEHOLDER, String.valueOf(context.getMinimumExtraHomes()));
    }

    private boolean validatePurchasePreconditions(Player player, PurchaseContext context) {
        if (!EpicHomes.getPlugin().getConfigManager().isExtraHomePurchaseEnabled()) {
            MessageUtils.sendPlayer(player, EpicHomes.getPlugin().getMessagesManager().getHomeBuyDisabled());
            return false;
        }

        if (context.getUser() == null) {
            MessageUtils.sendPlayer(player, EpicHomes.getPlugin().getMessagesManager().getHomeBuyFailed());
            return false;
        }

        if (context.getExtraHomes() >= context.getMaximumExtraHomes()) {
            MessageUtils.sendPlayer(player, applyPlaceholders(EpicHomes.getPlugin().getMessagesManager().getHomeBuyMaxReached(), context));
            return false;
        }

        if (!economyHook.isAvailable()) {
            MessageUtils.sendPlayer(player, EpicHomes.getPlugin().getMessagesManager().getHomeBuyEconomyUnavailable());
            return false;
        }

        return true;
    }

    private double getNextHomePrice(int currentExtraHomes) {
        int minimumExtraHomes = EpicHomes.getPlugin().getConfigManager().getExtraHomePurchaseMinimumExtraHomes();
        int paidExtraHomes = Math.max(0, currentExtraHomes - minimumExtraHomes);
        double basePrice = EpicHomes.getPlugin().getConfigManager().getExtraHomePurchaseBasePrice();
        double priceModifier = EpicHomes.getPlugin().getConfigManager().getExtraHomePurchasePriceModifier();
        return basePrice * Math.pow(priceModifier, paidExtraHomes);
    }

    public static class PurchaseContext {

        private final User user;
        private final double price;
        private final int baseHomeLimit;
        private final int extraHomes;
        private final int nextExtraHomes;
        private final int minimumExtraHomes;
        private final int maximumExtraHomes;

        public PurchaseContext(User user, double price, int baseHomeLimit, int extraHomes, int nextExtraHomes,
                               int minimumExtraHomes, int maximumExtraHomes) {
            this.user = user;
            this.price = price;
            this.baseHomeLimit = baseHomeLimit;
            this.extraHomes = extraHomes;
            this.nextExtraHomes = nextExtraHomes;
            this.minimumExtraHomes = minimumExtraHomes;
            this.maximumExtraHomes = maximumExtraHomes;
        }

        public User getUser() {
            return user;
        }

        public double getPrice() {
            return price;
        }

        public int getCurrentHomeLimit() {
            return baseHomeLimit + extraHomes;
        }

        public int getNextHomeLimit() {
            return baseHomeLimit + nextExtraHomes;
        }

        public int getExtraHomes() {
            return extraHomes;
        }

        public int getNextExtraHomes() {
            return nextExtraHomes;
        }

        public int getMinimumExtraHomes() {
            return minimumExtraHomes;
        }

        public int getMaximumExtraHomes() {
            return maximumExtraHomes;
        }
    }
}
