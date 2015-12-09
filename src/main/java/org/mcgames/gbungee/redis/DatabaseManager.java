package org.mcgames.gbungee.redis;

import lombok.Getter;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.*;

/**
 * Created by coltrane on 12/7/15.
 */
public class DatabaseManager {
    @Getter private static List<Jedis> pool = new ArrayList<Jedis>();
    private static JedisPool jedisPool;
    private static Jedis jedis;
    private static String host;
    private static int port;

    /**
     * DatabaseManager creates new connection to Redis
     * @param host
     * @param port
     */
    public DatabaseManager(String host, int port) {
        this.host = host;
        this.port = port;
        try {
//            pool.add(NewJedis());
//            jedis = NewJedis();
            jedisPool = new JedisPool(host, port);
            System.out.println("[Redis] CONNECTED TO REDIS");
            return;
        } catch(Exception e) {
            System.out.println("[Redis] <ERROR> CONNECTING TO REDIS");
            System.out.println(e.toString());
        }
    }

    /**
     * Returns new Jedis connection
     * @return
     */
    public static Jedis NewJedis() {
        return new Jedis(host, port);
    }

    /**
     * Returns available redis connection
     * @return
     */
    public static Jedis getResource() {
//        Iterator<Jedis> poolItr = pool.iterator();
//
//        while(poolItr.hasNext()) {
//            try {
//                Jedis j = poolItr.next();
//                j.get("gBungee.motd");
//
//                // if no exception return j
//                return j;
//            } catch(Exception e) {
//                // Already being used...
//                continue;
//            }
//        }
//
//        Jedis j = NewJedis();
//        pool.add(j);

        return jedisPool.getResource();
    }

}
