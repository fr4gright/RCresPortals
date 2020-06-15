package me.rvt.rcresportals.portals;

import me.rvt.rcresportals.yaml.MaterialReader;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

public class Portal {
    String owner;
    Block root, linkedTo;
    MaterialReader materialReader;

    public Portal(String owner, Block root, Block linkedTo) {
        this.owner = owner;
        this.root = root;
        this.linkedTo = linkedTo;
    }

    public String getOwner() {
        return owner;
    }

    public Block getRoot() {
        return root;
    }

    public Block getLinkedTo() {
        return linkedTo;
    }

    public String getRootXZ() {
        return root.getX() + " " + root.getZ();
    }

    public String getLinkedToXZ() {
        return linkedTo.getX() + " " + linkedTo.getZ();
    }

    public void startMaterialReader() {
        materialReader = new MaterialReader(this);
        materialReader.start();
    }

    public boolean hasTheSame() {
        return materialReader.hasTheSame();
    }

    public void buildPortal(Block linkedTo) {
        this.linkedTo = linkedTo;
        startMaterialReader();

        linkedTo.getRelative(BlockFace.UP).setType(Material.END_GATEWAY);
        linkedTo.getRelative(BlockFace.UP).getRelative(BlockFace.UP).setType(Material.END_GATEWAY);

        root.getRelative(BlockFace.UP).setType(Material.END_GATEWAY);
        root.getRelative(BlockFace.UP).getRelative(BlockFace.UP).setType(Material.END_GATEWAY);

        root.getWorld().spawnParticle(
                Particle.PORTAL, root.getLocation().toCenterLocation(), 1000);
        linkedTo.getWorld().spawnParticle(
                Particle.PORTAL, linkedTo.getLocation().toCenterLocation(), 1000);
    }

    public void teleportPlayer(Player p) {
        p.teleport(linkedTo.getRelative(BlockFace.UP).getLocation().toCenterLocation());

        linkedTo.getWorld().playEffect(linkedTo.getLocation(), Effect.PORTAL_TRAVEL, 0);
        linkedTo.getWorld().playEffect(linkedTo.getLocation(), Effect.ENDER_SIGNAL, 0);
    }

    public void breakPortals() {
        Block[] portalBlocks = {
                root.getRelative(BlockFace.UP),
                root.getRelative(BlockFace.UP).getRelative(BlockFace.UP),
                linkedTo.getRelative(BlockFace.UP),
                linkedTo.getRelative(BlockFace.UP).getRelative(BlockFace.UP)
        };

        root.getWorld().playEffect(root.getLocation().toCenterLocation(), Effect.END_GATEWAY_SPAWN, 3);
        linkedTo.getWorld().playEffect(linkedTo.getLocation().toCenterLocation(), Effect.END_GATEWAY_SPAWN, 3);

        for (Block b: portalBlocks) {
            if (b.getType() == Material.END_GATEWAY)
                b.breakNaturally();
        }
    }
}