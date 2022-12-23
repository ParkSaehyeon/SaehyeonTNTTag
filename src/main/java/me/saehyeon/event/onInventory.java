package me.saehyeon.event;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class onInventory implements Listener {
    @EventHandler
    void onInventoryClick(InventoryClickEvent e) {
        if(e.getCurrentItem().getType() == Material.TNT)
            e.setCancelled(true);

    }
}
