package fr.flowsqy.claimupdater;

import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class ClaimUpdater {

    private static final Set<RegionManager> actives = new HashSet<>();

    public static void update(RegionManager manager, CommandSender sender) {
        if (actives.contains(manager)) {
            return;
        }
        actives.add(manager);
        final boolean isNotConsole = !(sender instanceof ConsoleCommandSender);
        new BukkitRunnable() {

            final Iterator<Map.Entry<String, ProtectedRegion>> entryIterator;

            {
                entryIterator = manager.getRegions().entrySet().iterator();
            }

            @Override
            public void run() {
                for (int i = 0; i < 50; i++) {
                    if (!entryIterator.hasNext()) {
                        cancel();
                        return;
                    }
                    final Map.Entry<String, ProtectedRegion> regionEntry = entryIterator.next();
                    if (!updateRegion(manager, regionEntry.getValue())) {
                        System.out.println("La region '" + regionEntry.getKey() + "' n'a pas pu être update");
                        if (isNotConsole) {
                            sender.sendMessage("La region '" + regionEntry.getKey() + "' n'a pas pu être update");
                        }
                    }
                }
            }
        }.runTaskTimerAsynchronously(JavaPlugin.getPlugin(ClaimUpdaterPlugin.class), 0L, 1L);
    }

    public static boolean updateRegion(RegionManager manager, ProtectedRegion region) {
        Objects.requireNonNull(region);
        final String regionId = region.getId();
        final Set<String> owners = region.getOwners().getPlayers();
        if (!(region instanceof ProtectedCuboidRegion)) {
            return false;
        }
        if (owners.size() != 1) {
            return false;
        }
        if (!regionId.equalsIgnoreCase(owners.stream().findFirst().get())) {
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
        for (String player : domain.getPlayers()) {
            domain.addPlayer(Bukkit.getOfflinePlayer(player).getUniqueId());
        }
        domain.getPlayers().clear();
    }

}
