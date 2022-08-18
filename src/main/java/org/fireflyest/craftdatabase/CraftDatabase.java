package org.fireflyest.craftdatabase;

import org.bukkit.plugin.java.JavaPlugin;

public final class CraftDatabase extends JavaPlugin {

    private static CraftDatabase plugin;

    public static CraftDatabase getPlugin() {
        return plugin;
    }

    @Override
    public void onEnable() {
        plugin = this;

        // Plugin startup logic
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
