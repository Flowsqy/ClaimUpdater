package fr.flowsqy.claimupdater;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.*;
import java.util.function.Function;

public class OfflinePlayerFinder {

    private final static Map<String, List<OfflinePlayer>> playerCache;

    static {
        playerCache = new HashMap<>();
        for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
            final String name = player.getName();
            if (name == null)
                continue;
            final List<OfflinePlayer> players = playerCache.computeIfAbsent(name.toLowerCase(Locale.ROOT), k -> new ArrayList<>());
            players.add(player);
        }
    }

    public static OfflinePlayer getPlayer(String offline, Function<List<OfflinePlayer>, OfflinePlayer> ambiguousNameFunction) {
        final List<OfflinePlayer> players = playerCache.get(offline);
        if (players == null)
            return null;
        if (players.size() == 1) {
            return players.get(0);
        }
        return ambiguousNameFunction.apply(players);
    }

}
