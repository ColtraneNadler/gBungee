package org.mcgames.gbungee.listeners;

import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.mcgames.gbungee.utils.Color;
import org.mcgames.gbungee.utils.PlayerManager;
import org.mcgames.gbungee.gBungee;

public class ProxyPlayerListener implements Listener {
    private final gBungee plugin;

    public ProxyPlayerListener(gBungee plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onLogin(final LoginEvent event) {
        event.registerIntent(gBungee.getPlugin());

//        plugin.getProxy().getScheduler().runAsync(plugin, new Runnable() {
//            @Override
//            public void run() {
                System.out.println("works1");
                if (PlayerManager.isOnline(event.getConnection().getUniqueId().toString())) {
                    event.setCancelReason(Color.color("&cYou are already connected!"));
                    event.setCancelled(true);
                    event.completeIntent(plugin);
                    return;
                }

                event.completeIntent(plugin);
//            }
//        });

        PlayerManager.createPlayer(event.getConnection().getName(), event.getConnection().getUniqueId().toString());
    }

    @EventHandler
    public void onJoin(PostLoginEvent event) {
        PlayerManager.createPlayer(event.getPlayer().getName(), event.getPlayer().getUniqueId().toString());
    }

    @EventHandler
    public void onLeave(PlayerDisconnectEvent event) {
        PlayerManager.destroyPlayer(event.getPlayer());
    }
}
