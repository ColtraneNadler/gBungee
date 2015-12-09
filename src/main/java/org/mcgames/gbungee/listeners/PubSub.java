package org.mcgames.gbungee.listeners;

import net.md_5.bungee.event.EventHandler;
import org.mcgames.gbungee.redis.DatabaseManager;
import org.mcgames.gbungee.utils.Color;
import org.mcgames.gbungee.utils.PlayerManager;
import org.mcgames.gbungee.gBungee;
import redis.clients.jedis.JedisPubSub;

public class PubSub extends JedisPubSub {
    @EventHandler
    public void onMessage(String channel, String msg) {
        if(!(channel.equals("allservers") || channel.equals(gBungee.PROXYID)))
            return;

        String[] args = msg.split(":");

        System.out.println(DatabaseManager.getPool().size() + " open redis connections!");

        switch(args[0]) {
            case "alert":
                gBungee.alert(args[1]);
                break;
            case "send":
                PlayerManager.sendPlayer(args[1], args[2]);
                break;
            default:
                // some bullshit
                break;
        }
    }
}
