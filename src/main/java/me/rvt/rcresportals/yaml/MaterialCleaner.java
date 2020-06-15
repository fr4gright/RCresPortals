package me.rvt.rcresportals.yaml;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;

public class MaterialCleaner extends Thread {
    String owner;
    Material material;
    Plugin plugin;

    public MaterialCleaner(String owner, Material material) {
        this.owner = owner;
        this.material = material;
        this.plugin = Bukkit.getPluginManager().getPlugin("RCresPortals");

        this.start();
    }

    public void run() {
        File playerFile = new File(plugin.getDataFolder(),  "materials/" + owner + ".yml");

        if (playerFile.exists()) {

                FileConfiguration yamlFile = YamlConfiguration.loadConfiguration(playerFile);

                yamlFile.set(material.toString(), null);

                try {
                    yamlFile.save(playerFile);

                    if (!(playerFile.length() > 0))
                        playerFile.delete();

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
}