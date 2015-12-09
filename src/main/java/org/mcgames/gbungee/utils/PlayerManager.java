package org.mcgames.gbungee.utils;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.mcgames.gbungee.gBungee;
import redis.clients.jedis.Jedis;

import java.util.Set;


public class PlayerManager {
    /**
     * Creates new player in redis from a PendingConnection object
     * @param username
     * @param uuid
     */
    public static void createPlayer(String username, String uuid) {
        Jedis rs = gBungee.getJedis();
        rs.sadd(gBungee.PROXY_PLAYERS + gBungee.PROXYID, uuid);
        rs.hset(gBungee.UUID_REF, username, uuid);

        System.out.println(Color.color("&d" + username + " has LOGGED IN!"));
    }

    /**
     * Removes player from redis
     * @param player
     */
    public static void destroyPlayer(ProxiedPlayer player) {
        Jedis rs = gBungee.getJedis();

        rs.srem(gBungee.PROXY_PLAYERS + gBungee.PROXYID, player.getUniqueId().toString());

        System.out.println(Color.color("&d" + player.getName() + " has LOGGED OUT!"));
    }

    /**
     * Finds if a player is online, returns a boolean
     * @param uuid
     * @return
     */
    public static boolean isOnline(String uuid) {
        Jedis rs = gBungee.getJedis();

        System.out.println("works2");
        Set<String> proxies = rs.smembers(gBungee.PROXIES);

        System.out.println("works3");
        for(String p : proxies)
            if(rs.sismember(gBungee.PROXY_PLAYERS + p, uuid))
                return true;

        System.out.println("works4");

        return false;

    }

    /**
     * Sends player to specified server
     * @param username
     * @param server
     */
    public static void sendPlayer(String username, String server) {
        gBungee plugin = gBungee.getPlugin().getPlugin();

        ProxiedPlayer player = plugin.getProxy().getPlayer(username);
        ServerInfo sv = plugin.getProxy().getServerInfo(server);

        player.connect(sv);

    }

    /**
     * Gets player count on network, returns int
     * @return
     */
    public static int getCount() {
        Jedis rs = gBungee.getJedis();
        Set<String> proxies = rs.smembers(gBungee.PROXIES);

        int count = 0;

        for(String p : proxies) {
            count += rs.scard(gBungee.PROXY_PLAYERS + p);
        }

        return count;
    }

}
