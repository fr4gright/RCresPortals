package me.rvt.rcresportals;

import com.bekvon.bukkit.residence.containers.Flags;
import me.rvt.rcresportals.cooldowns.TeleportCooldown;
import me.rvt.rcresportals.portals.Portal;
import me.rvt.rcresportals.residence.GetRes;
import me.rvt.rcresportals.yaml.ConfigInit;
import me.rvt.rcresportals.yaml.MaterialCleaner;
import me.rvt.rcresportals.yaml.PortalReadDelete;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class RCresPortals extends JavaPlugin implements Listener {
    FileConfiguration config;
    List < Portal > unlinked = new ArrayList < > ();
    List < TeleportCooldown > teleported = new ArrayList < > ();
    List < String > worlds = new ArrayList < > ();

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        this.getCommand("portal").setExecutor(this);
        config = new ConfigInit(this).getConfig();
        worlds = config.getStringList("portal.worlds");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player) || sender.isOp()) {
            if (args.length > 1) {
                try {
                    ItemStack portalItem = config.getItemStack("portal.item");
                    portalItem.setAmount(Integer.parseInt(args[1]));

                    getServer().getPlayer(args[0]).getInventory().addItem(
                            portalItem);
                } catch (NullPointerException ex) {
                    sender.sendMessage(config.getString("message.prefix") +
                            ChatColor.RED + "Error: Player not found!");
                }
            }
        }
        return true;
    }

    @EventHandler
    private void onPortalActivation(PlayerInteractEvent e) {
        if (worlds.contains(e.getPlayer().getWorld().getName()) &&
                e.getHand() != null && e.getHand().equals(EquipmentSlot.HAND)) {

            Player p = e.getPlayer();
            Block b = e.getClickedBlock();

            if (p.getInventory().getItemInMainHand().getItemMeta() != null &&
                    p.getInventory().getItemInMainHand().getItemMeta().getLore() != null &&
                    p.getInventory().getItemInMainHand().getItemMeta().getLore().equals(
                            config.getItemStack("portal.item").getItemMeta().getLore())) {

                if (b != null && b.getType().isBlock() && !b.getType().hasGravity() &&
                        b.getRelative(BlockFace.UP).getType() != Material.END_GATEWAY) {

                    if (p.hasPermission("resportals.use") || p.isOp()) {
                        GetRes check = new GetRes();

                        for (Portal portal: unlinked) {
                            if (p.getName().equals(portal.getOwner())) {
                                if (portal.getRoot().getType() == b.getType() &&
                                        !portal.getRoot().equals(b)) {

                                    if (!portal.hasTheSame()) {
                                        if (check.hasResPerm(p, Flags.place)) {

                                            portal.buildPortal(b);

                                            PortalReadDelete portalReader =
                                                    new PortalReadDelete(portal, false);
                                            portalReader.start();

                                            p.sendMessage(config.getString("message.prefix") +
                                                    String.format(config.getString("message.created"),
                                                            portal.getRootXZ(),
                                                            check.getResName(portal.getRoot().getLocation()),
                                                            portal.getLinkedToXZ(),
                                                            check.getResName(portal.getLinkedTo().getLocation())));

                                            if (!p.isOp())
                                                p.getInventory().getItemInMainHand().setAmount(
                                                        p.getInventory().getItemInMainHand().getAmount() - 1);
                                        } else {
                                            p.sendMessage(config.getString("message.prefix") +
                                                    config.getString("message.permission.place"));
                                        }
                                    } else {
                                        p.sendMessage(config.getString("message.prefix") +
                                                String.format(config.getString("message.exists"),
                                                        noUnderScore(b.getType().toString())));
                                    }

                                    unlinked.removeIf(junk -> junk.getOwner().equals(p.getName()));
                                    return;
                                }
                            }
                        }
                        unlinked.removeIf(junk -> junk.getOwner().equals(p.getName()));

                        Portal portal = new Portal(p.getName(), b, null);
                        portal.startMaterialReader();
                        unlinked.add(portal);

                        p.sendMessage(config.getString("message.prefix") +
                                String.format(config.getString("message.selected"),
                                        noUnderScore(b.getType().toString())));
                    } else {
                        p.sendMessage(config.getString("message.prefix") +
                                config.getString("message.permission.voter"));
                    }
                } else {
                    p.sendMessage(config.getString("message.prefix") +
                            config.getString("message.noblock"));
                }
            }
        }
    }


    @EventHandler
    private void onPortalEnter(PlayerMoveEvent e) {
        if (worlds.contains(e.getPlayer().getWorld().getName()) &&
                e.getPlayer().getLocation().getBlock().getType() == Material.END_GATEWAY &&
                e.getPlayer().getLocation().getBlock().getRelative(BlockFace.UP).getType() == Material.END_GATEWAY) {

            Player p = e.getPlayer();
            PortalReadDelete portalCheck = new PortalReadDelete(null, false);
            FileConfiguration portal = portalCheck.loadPortal(p.getLocation().getBlock());

            if (portal != null) {
                for (TeleportCooldown tpc: teleported) {
                    if (tpc.getPlayer().equals(p.getName()))
                        if (tpc.isReady()) {
                            teleported.remove(tpc);
                            break;
                        }
                        else
                            return;
                }

                new Portal(
                        p.getName(),
                        null,
                        portal.getLocation("portal.linkedTo").getBlock()
                ).teleportPlayer(p);

                teleported.add(new TeleportCooldown(p, config.getInt("portal.cooldown")));
            }
        }
    }

    @EventHandler
    private void onPortalBreak(BlockBreakEvent e) {

        if (worlds.contains(e.getPlayer().getWorld().getName()) &&
                e.getBlock().getRelative(BlockFace.UP).getType() == Material.END_GATEWAY &&
                e.getBlock().getRelative(BlockFace.UP).getRelative(BlockFace.UP).getType() == Material.END_GATEWAY) {

            GetRes check = new GetRes();

            if ((e.getPlayer().hasPermission("resportals.use") &&
                    check.hasResPerm(e.getPlayer(), Flags.destroy)) || e.getPlayer().isOp()) {
                destroyPortal(e.getBlock());
            } else
                e.getPlayer().sendMessage(config.getString("message.prefix") +
                        config.getString("message.permission.destroy"));
        }
    }

    @EventHandler
    private void onPistonMove(BlockPistonExtendEvent e) {
        for (Block b: e.getBlocks()) {
            if (b.getRelative(BlockFace.UP).getType() == Material.END_GATEWAY &&
                    b.getRelative(BlockFace.UP).getRelative(BlockFace.UP).getType() == Material.END_GATEWAY) {
                destroyPortal(b);
                break;
            }
        }
    }

    @EventHandler
    private void noWallDamage(EntityDamageEvent e){
        if(config.getBoolean("disable-wall-damage") &&
                e.getEntity() instanceof Player && e.getCause()
                == EntityDamageEvent.DamageCause.SUFFOCATION){

            e.setCancelled(true);
        }
    }

    private void destroyPortal(Block b) {
        PortalReadDelete portalCheck = new PortalReadDelete(null, false);
        FileConfiguration portalFromFile = portalCheck.loadPortal(b);

        if (portalFromFile != null) {
            Portal portal = new Portal(
                    portalFromFile.getString("portal.owner"),
                    portalFromFile.getLocation("portal.root").getBlock(),
                    portalFromFile.getLocation("portal.linkedTo").getBlock()
            );

            new MaterialCleaner(portal.getOwner(), b.getType());

            portalCheck = new PortalReadDelete(portal, true);
            portalCheck.start();

            portal.breakPortals();

            ItemStack portalItem = config.getItemStack("portal.item");
            portalItem.setAmount(1);

            b.getWorld().dropItemNaturally(
                    b.getRelative(BlockFace.UP).getLocation().toCenterLocation(),
                    portalItem);
        }
    }

    private String noUnderScore(String input) {
        if (input.contains("_"))
            input = input.replace('_', ' ');
        return input;
    }
}