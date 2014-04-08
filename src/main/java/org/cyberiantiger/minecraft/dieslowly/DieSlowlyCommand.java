package org.cyberiantiger.minecraft.dieslowly;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

/**
 *
 * @author antony
 */
public class DieSlowlyCommand extends Command {
    private final Main plugin;

    public DieSlowlyCommand(Main plugin) {
        super("dieslowly", "dieslowly.dieslowly");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender cs, String[] strings) {
        try {
            plugin.dieSlowly();
            cs.sendMessage("Proxy will shutdown when all users have logged out.");
        } catch (IllegalStateException e) {
            cs.sendMessage(e.getMessage());
        }
    }
}