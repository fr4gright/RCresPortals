package me.rvt.rcresportals.yaml;

import me.rvt.rcresportals.portals.Portal;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

public class MaterialReader extends Thread{
    Plugin plugin;
    Portal portal;
    boolean ownsTheSame;

    public MaterialReader(Portal portal){
        this.portal = portal;
        this.plugin = Bukkit.getPluginManager().getPlugin("RCresPortals");
    }

    public void run(){
        File playerFile = new File(plugin.getDataFolder(),  "materials/" + portal.getOwner() + ".yml");
        FileConfiguration yamlReader = YamlConfiguration.loadConfiguration(playerFile);

        String material = portal.getRoot().getType().toString();

        if(portal.getLinkedTo() == null){
            if(yamlReader.contains(material)){
                ownsTheSame = true;
            }
        }
        else{
            yamlReader.set(material, portal.getRootXZ());

            try {
                yamlReader.save(playerFile);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public boolean hasTheSame(){
        return ownsTheSame;
    }
}
