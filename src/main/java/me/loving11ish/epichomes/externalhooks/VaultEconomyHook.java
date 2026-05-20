package me.loving11ish.epichomes.externalhooks;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.lang.reflect.Method;
import java.text.DecimalFormat;

public class VaultEconomyHook {

    private Object economyProvider;
    private Class<?> economyClass;

    public boolean isAvailable() {
        return setupEconomy();
    }

    public boolean has(OfflinePlayer player, double amount) {
        if (!setupEconomy()) {
            return false;
        }

        try {
            Method method = economyClass.getMethod("has", OfflinePlayer.class, double.class);
            return (boolean) method.invoke(economyProvider, player, amount);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean withdraw(OfflinePlayer player, double amount) {
        if (!setupEconomy()) {
            return false;
        }

        try {
            Method method = economyClass.getMethod("withdrawPlayer", OfflinePlayer.class, double.class);
            Object response = method.invoke(economyProvider, player, amount);
            return isTransactionSuccessful(response);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean deposit(OfflinePlayer player, double amount) {
        if (!setupEconomy()) {
            return false;
        }

        try {
            Method method = economyClass.getMethod("depositPlayer", OfflinePlayer.class, double.class);
            Object response = method.invoke(economyProvider, player, amount);
            return isTransactionSuccessful(response);
        } catch (Exception e) {
            return false;
        }
    }

    public String format(double amount) {
        if (!setupEconomy()) {
            return new DecimalFormat("#,##0.##").format(amount);
        }

        try {
            Method method = economyClass.getMethod("format", double.class);
            return (String) method.invoke(economyProvider, amount);
        } catch (Exception e) {
            return new DecimalFormat("#,##0.##").format(amount);
        }
    }

    private boolean setupEconomy() {
        if (economyProvider != null && economyClass != null) {
            return true;
        }

        if (!Bukkit.getPluginManager().isPluginEnabled("Vault")) {
            return false;
        }

        try {
            economyClass = Class.forName("net.milkbowl.vault.economy.Economy");
            RegisteredServiceProvider<?> registration = Bukkit.getServicesManager().getRegistration(economyClass);
            if (registration == null) {
                return false;
            }

            economyProvider = registration.getProvider();
            return economyProvider != null;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private boolean isTransactionSuccessful(Object response) throws ReflectiveOperationException {
        Method method = response.getClass().getMethod("transactionSuccess");
        return (boolean) method.invoke(response);
    }
}
