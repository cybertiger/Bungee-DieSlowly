/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.minecraft.dieslowly;

import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

/**
 *
 * @author antony
 */
public class Main extends Plugin implements Listener {
    private boolean dieSlowly = false;

    @Override
    public void onLoad() {
    }

    @Override
    public void onEnable() {
        getProxy().getPluginManager().registerCommand(this, new DieSlowlyCommand(this));
        getProxy().getPluginManager().registerListener(this, this);
    }

    @Override
    public void onDisable() {
    }

    private void shutdown() {
        getLogger().info("Shutting down, no remaining players connected");
        getProxy().stop();
    }

    public void dieSlowly() {
        synchronized(this) {
            if (dieSlowly)
                throw new IllegalStateException("Already dying slowly!");
            dieSlowly = true;
        }
        if (getProxy().getOnlineCount() == 0) {
            shutdown();
        }
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerDisconnectEvent e) {
        synchronized(this) {
            if (!dieSlowly) {
                return;
            }
        }
        // This event is fired during disconnect whilst the player
        // still counts towards the online count.
        if (getProxy().getOnlineCount() <= 1) {
            shutdown();
        }
    }
}