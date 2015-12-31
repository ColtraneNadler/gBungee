package org.mcgames.gbungee;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import org.mcgames.gbungee.commands.Alert;
import org.mcgames.gbungee.commands.GList;
import org.mcgames.gbungee.commands.Send;
import org.mcgames.gbungee.listeners.ProxyPingListener;
import org.mcgames.gbungee.listeners.ProxyPlayerListener;
import org.mcgames.gbungee.listeners.PubSub;
import org.mcgames.gbungee.utils.Color;
import org.mcgames.gbungee.utils.Utils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

public class gBungee extends Plugin {
    private PluginManager pm;
    private PubSub pubSub;
    private static gBungee plugin;

    public static String PROXY_PLAYERS = "gBungee.proxyPlayers:";
    public static String UUID_REF = "gBungee.uuidRef";
    public static String SERVER_REF = "gBungee.serverRef";
    public static String PROXIES = "gBungee.proxies";
    public static String MOTD = "gBungee.motd";
    public static String PROXYID;
    public static JedisPool pool;


    @Override
    public void onEnable() {
        this.PROXYID = Utils.randomStr(13);
        this.plugin = this;

        FutureTask<JedisPool> task = new FutureTask<JedisPool>(new Callable<JedisPool>() {
            @Override
            public JedisPool call() throws Exception {
                try {
                    pool = new JedisPool("127.0.0.1", 6379);

                    pubSub = new PubSub();

                    getProxy().getScheduler().runAsync(getPlugin(), new Runnable() {
                        @Override
                        public void run() {
                            getJedis().subscribe(pubSub, "allservers");
                        }
                    });

                    long time = getRedisTime(getJedis().time());
                    getJedis().hset(PROXIES, PROXYID, time + "");

                    startHeartbeat();

                } catch (Exception e) {
                    System.out.println("SHIT FUCKED UP!");
                    e.printStackTrace();
                }
                return null;
            }
        });

        getProxy().getScheduler().runAsync(this, task);

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
    }

    @Override
    public void onDisable() {
        Jedis rs = this.getJedis();

        rs.srem(PROXIES, PROXYID);
        rs.del(gBungee.PROXY_PLAYERS + PROXYID);

    }

    public void startHeartbeat() {

        /**
         * Heartbeats init
         */
        getProxy().getScheduler().schedule(this, new Runnable() {
            @Override
            public void run() {
                long time = getRedisTime(getJedis().time());
                getJedis().hset(PROXIES, PROXYID, time+"");
            }
        }, 15, TimeUnit.SECONDS);

        System.out.println("[Enabled] Loaded gBungee | " + PROXYID);

    }

    public static long getRedisTime(List<String> redisTime) {
        return Long.parseLong(redisTime.get(0));
    }

    public static Set<String> getServers() {
        Jedis jedis = getJedis();
        Map<String, String> heartbeats = jedis.hgetAll(PROXIES);
        Set<String> servers = new HashSet<String>();

        for(Map.Entry<String, String> proxy : heartbeats.entrySet()) {
            long time = getRedisTime(jedis.time());
            if(time <= Long.parseLong(proxy.getValue()) + 30 && time >= Long.parseLong(proxy.getValue()) - 30)
                servers.add(proxy.getKey());
        }

        return servers;
    }

    public static Jedis getJedis() {
        return pool.getResource();
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
}
