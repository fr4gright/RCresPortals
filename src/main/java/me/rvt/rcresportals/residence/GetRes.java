package me.rvt.rcresportals.residence;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.containers.Flags;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import static org.bukkit.Bukkit.getServer;

public class GetRes {
    Residence res = (Residence) getServer().getPluginManager().getPlugin("Residence");

    public boolean hasResPerm(Player p, Flags flag){
        ClaimedResidence currentRes = res.getResidenceManager().getByLoc(p.getLocation());

                if(currentRes != null){
            return currentRes.getPermissions().playerHas(p, flag, true);
        }
        return false;
    }

    public String getResName(Location loc){
        return res.getResidenceManager().getByLoc(loc).getName();
    }
}
