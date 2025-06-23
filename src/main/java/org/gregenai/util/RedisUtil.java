package org.gregenai.util;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisUtil {
    private static final JedisPool jedisPool = new JedisPool("localhost", 6379);

    public static Jedis getJedis() {
        return jedisPool.getResource();
    }

    public static void close(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }

    public static String getValue(String key) {
        try (Jedis jedis = getJedis()) {
            return jedis.get(key);
        }
    }

    public static void setValue(String key, String value, int ttlsecond) {
        try (Jedis jedis = getJedis()) {
            jedis.psetex(key, ttlsecond, value);
        }
    }

    public static void deleteKey(String key) {
        try (Jedis jedis = getJedis()) {
            jedis.del(key);
        }
    }

    public static boolean exists(String key){
        return true;
    }
    public static void shutDown() {
        jedisPool.close();
    }
}
