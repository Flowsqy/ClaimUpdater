package fr.flowsqy.claimupdater;

import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class UpdateCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length != 1) {
            sender.sendMessage("/update <world>");
            return true;
        }
        final World world = Bukkit.getWorld(args[0]);

        if (world == null) {
            sender.sendMessage("Le monde '" + args[0] + "' n'existe pas");
            return false;
        }

        final RegionManager manager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(new BukkitWorld(world));
        if (manager == null) {
            sender.sendMessage("Le monde n'est pas supporté");
            return false;
        }
        ClaimUpdater.update(manager, sender);
        return true;
    }
}
