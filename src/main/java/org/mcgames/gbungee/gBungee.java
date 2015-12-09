package org.mcgames.gbungee;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import org.apache.commons.pool2.impl.EvictionPolicy;
import org.mcgames.gbungee.commands.Alert;
import org.mcgames.gbungee.commands.GList;
import org.mcgames.gbungee.commands.Send;
import org.mcgames.gbungee.listeners.ProxyPingListener;
import org.mcgames.gbungee.listeners.ProxyPlayerListener;
import org.mcgames.gbungee.listeners.PubSub;
import org.mcgames.gbungee.redis.DatabaseManager;
import org.mcgames.gbungee.utils.Color;
import org.mcgames.gbungee.utils.Utils;
import redis.clients.jedis.Jedis;

public class gBungee extends Plugin {
    private static DatabaseManager databaseManager;
    private PluginManager pm;
    private PubSub pubSub;
    private static gBungee plugin;

    public static String PROXY_PLAYERS = "gBungee.proxyPlayers:";
    public static String UUID_REF = "gBungee.uuidRef";
    public static String SERVER_REF = "gBungee.serverRef";
    public static String PROXIES = "gBungee.proxies";
    public static String MOTD = "gBungee.motd";
    public static String PROXYID;

    @Override
    public void onEnable() {
        this.PROXYID = Utils.randomStr(13);
        this.plugin = this;

        try {
            databaseManager = new DatabaseManager("127.0.0.1", 6379);
        } catch(Exception e) {
            System.out.println(e.toString());
        }

        pm = getProxy().getPluginManager();

        /**
         * Registering listeners
         */
        pm.registerListener(this, new ProxyPlayerListener(this));
        pm.registerListener(this, new ProxyPingListener(this));

        /**
         * Registering commands
         */
        pm.registerCommand(this, new Send(this));
        pm.registerCommand(this, new GList(this));
        pm.registerCommand(this, new Alert(this));

        pubSub = new PubSub();

        getProxy().getScheduler().runAsync(this, new Runnable() {
            @Override
            public void run() {
                try {
                    getJedis().subscribe(pubSub, "allservers", PROXYID);
                } catch (Exception e) {
                    System.out.println(e.toString());
                }
            }
        });


        getJedis().sadd(PROXIES, PROXYID);
        System.out.println("[Enabled] Loaded gBungee | " + PROXYID);
    }

    @Override
    public void onDisable() {
        Jedis rs = this.getJedis();

        rs.srem(PROXIES, PROXYID);
        rs.del(gBungee.PROXY_PLAYERS + PROXYID);

    }

    public static Jedis getJedis() {
        return databaseManager.getResource();
    }

    public static gBungee getPlugin() {
        return plugin;
    }

    public static String getMotd() {
        return Color.color(getJedis().get(MOTD));
    }

    public static void alert(String msg) {
        plugin.getProxy().broadcast(Color.color("&d[&5Alert&d] &5" + msg));
    }

    public static void getConns() {
        System.out.println("[REDIS] " + DatabaseManager.getPool().size() + " open connections.");

        try {
            Thread.sleep(5000);
        } catch(Exception e) {
            System.out.println(e.toString());
        }

        getConns();
    }
}
