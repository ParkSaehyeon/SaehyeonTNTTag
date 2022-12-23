package me.saehyeon.main;

import me.saehyeon.event.onCommand;
import me.saehyeon.event.onDamage;
import me.saehyeon.event.onInventory;
import me.saehyeon.event.onJoin;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class TNTTag extends JavaPlugin {

    public static TNTTag instance;

    @Override
    public void onEnable() {

        instance = this;

        Bukkit.getPluginCommand("tnt").setExecutor(new onCommand());
        Bukkit.getPluginManager().registerEvents(new onDamage(), this);
        Bukkit.getPluginManager().registerEvents(new onJoin(), this);
        Bukkit.getPluginManager().registerEvents(new onInventory(), this);
    }

    @Override
    public void onDisable() {
    }
}
