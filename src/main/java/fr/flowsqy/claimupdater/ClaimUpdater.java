package fr.flowsqy.claimupdater;

import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
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
                        actives.remove(manager);
                        return;
                    }
                    final Map.Entry<String, ProtectedRegion> regionEntry = entryIterator.next();
                    if (!updateRegion(sender, isNotConsole, manager, regionEntry.getValue())) {
                        System.out.println("La region '" + regionEntry.getKey() + "' n'a pas pu être update");
                        if (isNotConsole) {
                            sender.sendMessage("La region '" + regionEntry.getKey() + "' n'a pas pu être update");
                        }
                    }
                }
            }
        }.runTaskTimerAsynchronously(JavaPlugin.getPlugin(ClaimUpdaterPlugin.class), 0L, 1L);
    }

    public static boolean updateRegion(CommandSender sender, boolean isNotConsole, RegionManager manager, ProtectedRegion region) {
        Objects.requireNonNull(region);
        final String regionId = region.getId();
        final Set<String> owners = region.getOwners().getPlayers();
        if (!(region instanceof ProtectedCuboidRegion)) {
            return false;
        }
        if (owners.size() != 1) {
            return false;
        }
        if (!regionId.equals(owners.stream().findFirst().get())) {
            return false;
        }
        final DefaultDomain ownerDomain = region.getOwners();
        final OfflinePlayer player = OfflinePlayerFinder.getPlayer(regionId, offlinePlayers -> {
            final StringBuilder builder = new StringBuilder();
            for (OfflinePlayer p : offlinePlayers) {
                if (!builder.isEmpty()) {
                    builder.append(", ");
                }
                builder.append(p.getName());
            }
            System.out.println("Noms ambigus: " + builder);
            if (isNotConsole)
                sender.sendMessage("Noms ambigus: " + builder);

            return null;
        });
        if (player == null)
            return false;
        final UUID owner = player.getUniqueId();
        ownerDomain.addPlayer(owner);
        ownerDomain.removePlayer(regionId);
        updateDomain(ownerDomain);
        updateDomain(region.getMembers());
        final ProtectedCuboidRegion newRegion = new ProtectedCuboidRegion("stelyclaim_player_" + owner, region.getMaximumPoint(), region.getMinimumPoint());
        newRegion.copyFrom(region);
        manager.removeRegion(regionId);
        manager.addRegion(newRegion);
        return true;
    }

    private static void updateDomain(DefaultDomain domain) {
        for (String player : domain.getPlayers()) {
            final OfflinePlayer p = OfflinePlayerFinder.getPlayer(player, o -> null);
            if (p == null)
                continue;
            domain.addPlayer(p.getUniqueId());
        }
        final List<String> names = new ArrayList<>(domain.getPlayers());
        for (String player : names) {
            domain.removePlayer(player);
        }
    }

}
