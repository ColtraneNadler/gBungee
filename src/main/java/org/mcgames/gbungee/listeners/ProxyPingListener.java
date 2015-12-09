package org.mcgames.gbungee.listeners;

import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.mcgames.gbungee.utils.PlayerManager;
import org.mcgames.gbungee.gBungee;

/**
 * Created by coltrane on 12/7/15.
 */
public class ProxyPingListener implements Listener {
    private final gBungee plugin;

    public ProxyPingListener(gBungee plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPing(final ProxyPingEvent event) {
        // Swag
        event.registerIntent(gBungee.getPlugin());

        gBungee.getPlugin().getProxy().getScheduler().runAsync(gBungee.getPlugin(), new Runnable() {
            @Override
            public void run() {
                ServerPing old = event.getResponse();
                ServerPing response = new ServerPing();

                response.setPlayers(new ServerPing.Players(PlayerManager.getCount() + 1, PlayerManager.getCount(), old.getPlayers().getSample()));
                response.setDescription(gBungee.getMotd());
                response.setFavicon(old.getFaviconObject());
                response.setVersion(old.getVersion());

                event.setResponse(response);
                // Event completed
                event.completeIntent(gBungee.getPlugin());
            }
        });
    }
}
