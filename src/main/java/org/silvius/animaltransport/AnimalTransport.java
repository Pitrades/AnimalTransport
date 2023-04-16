package org.silvius.animaltransport;

import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
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
        saveDefaultConfig();
        getCommand("animaltransport").setExecutor(new AnimalEggCommand());
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new AnimalEggCommand(), this);
        AnimalEggCommand.initialize();


    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
