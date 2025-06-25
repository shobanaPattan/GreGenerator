package org.gregenai.interfaces;

import java.util.Map;

public interface CacheServices {
    boolean deleteFromCache(String key);

    boolean saveToCache(String key, String value, long TTlSeconds);

    boolean saveToCache(Map<String, Object> cacheEntries, long TTlSeconds);

    String getWordDetailsFromCache(String key);

    Map<String, Object> getAllRecordsFromCache();

    boolean updateRedisCache(String key, String value, long TTlSeconds);
}
