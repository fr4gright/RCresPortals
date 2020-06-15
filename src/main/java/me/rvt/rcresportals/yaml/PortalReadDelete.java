package me.rvt.rcresportals.yaml;

import me.rvt.rcresportals.portals.Portal;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

public class PortalReadDelete extends Thread {
    Plugin plugin;
    Portal portal;
    boolean delete;

    public PortalReadDelete(Portal portal, boolean delete) {
        this.portal = portal;
        this.delete = delete;
        this.plugin = Bukkit.getPluginManager().getPlugin("RCresPortals");
    }

    public FileConfiguration loadPortal(Block rootBlock) {
        File file = new File(plugin.getDataFolder(),
                "portals/" + rootBlock.getX() + " " + rootBlock.getZ() + ".yml");

        if (file.exists())
            return YamlConfiguration.loadConfiguration(file);
        else return null;
    }

    public void run() {
        File[] file = {
                new File(plugin.getDataFolder(), "portals/" + portal.getRootXZ() + ".yml"),
                new File(plugin.getDataFolder(), "portals/" + portal.getLinkedToXZ() + ".yml")
        };

        if(delete){
            file[0].delete();
            file[1].delete();

            return;
        }

        FileConfiguration[] portalData = {
                YamlConfiguration.loadConfiguration(file[0]),
                YamlConfiguration.loadConfiguration(file[1])
        };

        portalData[0].set("portal.root", portal.getRoot().getLocation());
        portalData[0].set("portal.linkedTo", portal.getLinkedTo().getLocation());
        portalData[1].set("portal.root", portal.getLinkedTo().getLocation());
        portalData[1].set("portal.linkedTo", portal.getRoot().getLocation());

        portalData[0].set("portal.owner", portal.getOwner());
        portalData[1].set("portal.owner", portal.getOwner());

        try {
            portalData[0].save(file[0]);
            portalData[1].save(file[1]);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}