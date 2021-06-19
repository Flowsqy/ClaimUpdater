package fr.flowsqy.claimupdater;

import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.Objects;
import java.util.Set;

public class ClaimUpdater {

    public static boolean updateRegion(RegionManager manager, ProtectedRegion region){
        Objects.requireNonNull(region);
        final String regionId = region.getId();
        final Set<String> owners = region.getOwners().getPlayers();
        if(!(region instanceof ProtectedCuboidRegion)){
            return false;
        }
        if(owners.size() != 1){
            return false;
        }
        if(!regionId.equalsIgnoreCase(owners.stream().findFirst().get())){
            return false;
        }
        final OfflinePlayer newOwner = Bukkit.getOfflinePlayer(regionId);
        updateDomain(region.getOwners());
        updateDomain(region.getMembers());
        final ProtectedCuboidRegion newRegion = new ProtectedCuboidRegion("stelyclaim_player_" + newOwner.getUniqueId(), region.getMaximumPoint(), region.getMinimumPoint());
        newRegion.copyFrom(region);
        manager.removeRegion(regionId);
        manager.addRegion(newRegion);
        return true;
    }

    private static void updateDomain(DefaultDomain domain) {
        for(String player : domain.getPlayers()){
            domain.addPlayer(Bukkit.getOfflinePlayer(player).getUniqueId());
        }
        domain.getPlayers().clear();
    }

}
