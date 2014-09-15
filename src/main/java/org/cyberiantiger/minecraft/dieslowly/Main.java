/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.minecraft.dieslowly;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

import java.lang.reflect.Field;

/**
 *
 * @author antony
 */
public class Main extends Plugin implements Listener {
    private boolean dieSlowly = false;
    private ProxyServer oldInstance;

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
        this.oldInstance.stop();
        this.oldInstance = null;
        dieSlowly = false;
    }

    public void dieSlowly() {
        synchronized(this) {
            if (dieSlowly)
                throw new IllegalStateException("Already dying slowly!");
            dieSlowly = true;
        }
        this.oldInstance = ProxyServer.getInstance();
        try{
            BungeeCord bungeeCord = new BungeeCord();
            ProxyServer.setInstance(bungeeCord);
            bungeeCord.start();
            for(Plugin plugin : this.oldInstance.getPluginManager().getPlugins()){
                Field f = plugin.getClass().getDeclaredField("proxy");
                f.setAccessible(true);
                f.set(plugin, ProxyServer.getInstance());
            }
        }catch (Exception e){

        }
        if (this.oldInstance.getOnlineCount() == 0) {
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
        if(this.oldInstance == null) return;
        if (this.oldInstance.getOnlineCount() <= 1) {
            shutdown();
        }
    }

    @EventHandler
    public void onPing(ProxyPingEvent e){
        if(this.oldInstance == null) return;
        ServerPing.Players p = e.getResponse().getPlayers();
        p.setOnline(oldInstance.getOnlineCount()+ProxyServer.getInstance().getOnlineCount());
        e.getResponse().setPlayers(p);
    }
}