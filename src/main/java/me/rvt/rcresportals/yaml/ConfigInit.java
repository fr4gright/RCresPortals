package me.rvt.rcresportals.yaml;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConfigInit {
    private FileConfiguration config;

    public ConfigInit(Plugin plugin)
    {
        loadConfig(plugin);
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public void loadConfig(Plugin plugin) {
        File conf = new File(plugin.getDataFolder(), "config.yml");

        config = YamlConfiguration.loadConfiguration(conf);

        if (!config.contains("portal")) {

            init();

            try {
                config.save(conf);
            } catch (IOException var3) {
                System.out.println("[RCresPortals] Unable to save config!");
            }
        }
    }

    private void init() {
        ItemStack portalItem = new ItemStack(Material.COMPASS, 1);
        ItemMeta portalMeta = portalItem.getItemMeta();

        portalMeta.setDisplayName(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Residence Portal");
        portalMeta.setLore(
                new ArrayList<>(
                        Arrays.asList(
                                " ",
                               ChatColor.translateAlternateColorCodes('&',
                                       "&lUse it to open a portal in your &e&lResidence&5&l."),
                                " ",
                                ChatColor.translateAlternateColorCodes('&',
                                        "&rSelect (&2Right Click&r) two blocks of the same type"),
                                ChatColor.translateAlternateColorCodes('&',
                                "&rto open a &5&lPortal &rbetween them."),
                                " ",
                                ChatColor.translateAlternateColorCodes('&',
                                        "&cRequires &b&lVoter &crank!")
                        )
                )
        );
        portalMeta.addEnchant(Enchantment.VANISHING_CURSE, 1, false);
        portalItem.setItemMeta(portalMeta);

        List<String> worlds = new ArrayList<>();

        worlds.add("world");

        config.set("portal.item", portalItem);
        config.set("portal.cooldown", 5);
        config.set("portal.worlds", worlds);
        config.set("disable-wall-damage", true);
        config.set("message.prefix", ChatColor.translateAlternateColorCodes('&',
                "&f&l[&b&lRC&f&l][&5&lPortals&f&l] &r"));
        config.set("message.permission.voter", ChatColor.translateAlternateColorCodes('&',
                "You need to be a &b&lVoter &rto use this!"));
        config.set("message.permission.place", ChatColor.translateAlternateColorCodes('&',
                "&cYou need to be in a &c&lResidence&c."));
        config.set("message.noblock", ChatColor.translateAlternateColorCodes('&',
                "&cThis block is not allowed!"));
        config.set("message.selected", ChatColor.translateAlternateColorCodes('&',
                "First block selected &r(&e%s&r)."));
        config.set("message.created", ChatColor.translateAlternateColorCodes('&',
                "Portals created successfully! (x) &5&l%s &r(z) (&e%s&r) &rlinked to (x) &5&l%s &r(z) (&e%s&r)"));
        config.set("message.exists", ChatColor.translateAlternateColorCodes('&',
                "You already own a portal of type &e%s&r!"));
        config.set("message.permission.destroy", ChatColor.translateAlternateColorCodes('&',
                "You don't have &c&lpermission &rto destroy this!"));
    }
}