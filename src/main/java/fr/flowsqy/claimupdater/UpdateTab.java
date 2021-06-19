package fr.flowsqy.claimupdater;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UpdateTab implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length != 1) {
            return Collections.emptyList();
        }
        final String arg = args[0].toLowerCase(Locale.ROOT);
        final Stream<String> worlds = Bukkit.getWorlds().stream().map(World::getName);
        if (arg.isEmpty()) {
            return worlds.collect(Collectors.toList());
        }
        return worlds.filter(worldName -> worldName.startsWith(arg)).collect(Collectors.toList());
    }
}
