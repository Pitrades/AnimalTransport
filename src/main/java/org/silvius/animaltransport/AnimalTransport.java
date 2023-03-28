package org.silvius.animaltransport;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class AnimalTransport extends JavaPlugin {
    private static AnimalTransport plugin;

    public static AnimalTransport getPlugin() {
        return plugin;
    }

    @Override
    public void onEnable() {
        plugin = this;
        getCommand("transportei").setExecutor(new AnimalEggCommand());
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new AnimalEggCommand(), this);



    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
