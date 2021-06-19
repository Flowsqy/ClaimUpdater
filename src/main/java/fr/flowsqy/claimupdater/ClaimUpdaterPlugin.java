package fr.flowsqy.claimupdater;

import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class ClaimUpdaterPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        final PluginCommand command = getCommand("udapte");
        assert command != null;
        command.setExecutor(new UpdateCommand());
        command.setTabCompleter(new UpdateTab());
    }

}