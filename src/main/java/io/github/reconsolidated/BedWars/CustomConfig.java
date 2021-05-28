package io.github.reconsolidated.BedWars;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class CustomConfig {
    public static YamlConfiguration loadCustomConfig(String name){
        YamlConfiguration customConfig = new YamlConfiguration();
        BedWars plugin = (BedWars) Bukkit.getPluginManager().getPlugin("BedWars");
        File customConfigFile = new File(plugin.getDataFolder(), name + ".yml");
        if (!customConfigFile.exists()) {
            try{
                customConfigFile.createNewFile();
            }
            catch (IOException e){
                Bukkit.broadcastMessage("Nie udało się wczytać pliku konfiguracyjnego: " + name);
            }
        }
        customConfig = new YamlConfiguration();
        try {
            customConfig.load(customConfigFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        return customConfig;
    }

    public static void saveCustomConfig(String name, YamlConfiguration config){
        BedWars plugin = (BedWars) Bukkit.getPluginManager().getPlugin("BedWars");
        File customConfigFile = new File(plugin.getDataFolder(), name + ".yml");
        try {
            config.save(customConfigFile);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
