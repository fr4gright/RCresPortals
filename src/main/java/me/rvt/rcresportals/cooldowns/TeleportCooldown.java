package me.rvt.rcresportals.cooldowns;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

public class TeleportCooldown extends Thread {
    BossBar bar;
    Player p;
    boolean isReady = false;
    int cooldown;

    public TeleportCooldown(Player p, int cooldown) {
        bar = Bukkit.createBossBar((ChatColor.DARK_PURPLE + "Portal Cooldown"),
                BarColor.PURPLE, BarStyle.SEGMENTED_20);
        this.p = p;
        this.cooldown = cooldown;

        this.start();
    }

    public String getPlayer() {
        return p.getName();
    }
    public boolean isReady() {
        return isReady;
    }

    public void run() {
        bar.addPlayer(p);

        for (double i = 1.00; i > 0; i -= 0.01) {
            bar.setProgress(i);

            try {
                Thread.sleep(cooldown*10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        isReady = true;
        bar.removePlayer(p);
    }
}