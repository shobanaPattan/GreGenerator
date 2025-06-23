package org.gregenai.Iinterface;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Map;

public interface CacheServices {
    boolean deleteFromCache(String key);

    boolean saveToCache(String key, long TTlSeconds, String value);

    boolean saveToCache(Map<String, Object> cacheEntries,long TTlSeconds);

    String getWordDetailsFromCache(String key);

    Map<String, Object> getAllRecordsFromCache();

}
