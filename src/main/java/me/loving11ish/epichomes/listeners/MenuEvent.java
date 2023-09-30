package me.loving11ish.epichomes.listeners;

import me.loving11ish.epichomes.menusystem.Menu;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;

public class MenuEvent implements Listener {

    @EventHandler
    public void onMenuClick(InventoryClickEvent event){

        InventoryHolder holder = event.getInventory().getHolder();
        if (holder instanceof Menu) {
            Menu menu = (Menu) holder;
            event.setCancelled(true);
            if (event.getCurrentItem() == null) {
                return;
            }
            menu.handleMenu(event);
        }
    }
}
