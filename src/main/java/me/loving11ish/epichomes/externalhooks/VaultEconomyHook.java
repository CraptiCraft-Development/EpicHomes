package me.loving11ish.epichomes.externalhooks;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.text.DecimalFormat;

public class VaultEconomyHook {

    private Economy economyProvider;

    public boolean isAvailable() {
        return setupEconomy();
    }

    public boolean has(OfflinePlayer player, double amount) {
        if (!setupEconomy()) {
            return false;
        }

        return economyProvider.has(player, amount);
    }

    public boolean withdraw(OfflinePlayer player, double amount) {
        if (!setupEconomy()) {
            return false;
        }

        EconomyResponse response = economyProvider.withdrawPlayer(player, amount);
        return response.transactionSuccess();
    }

    public boolean deposit(OfflinePlayer player, double amount) {
        if (!setupEconomy()) {
            return false;
        }

        EconomyResponse response = economyProvider.depositPlayer(player, amount);
        return response.transactionSuccess();
    }

    public String format(double amount) {
        if (!setupEconomy()) {
            return new DecimalFormat("#,##0.##").format(amount);
        }

        return economyProvider.format(amount);
    }

    private boolean setupEconomy() {
        if (economyProvider != null) {
            return true;
        }

        if (!Bukkit.getPluginManager().isPluginEnabled("Vault")) {
            return false;
        }

        RegisteredServiceProvider<Economy> registration = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (registration == null) {
            return false;
        }

        economyProvider = registration.getProvider();
        return economyProvider != null;
    }
}
