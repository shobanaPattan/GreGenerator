package org.gregenai.dependency.db;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.gregenai.Iinterface.CacheServices;
import org.gregenai.model.GreRequest;
import org.gregenai.util.AbstractDataBaseConnector;
import org.gregenai.util.JSONUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.params.ScanParams;
import redis.clients.jedis.resps.ScanResult;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DynamoDBConnector extends AbstractDataBaseConnector implements CacheServices {

    static DynamoDbClient dynamoDbClient;
    private static final long REDIS_CACHE_TTL_SECONDS = 3600;

    private static final JedisPool jedisPool = new JedisPool("localhost", 6379);
    private final ObjectMapper mapper = new ObjectMapper();

    static {
        try {
            //Creating connection
            System.out.println(" ********* Connecting to AWS DynamoDB ********* ");
            dynamoDbClient = DynamoDbClient.builder().
                    region(Region.US_EAST_1).
                    credentialsProvider(EnvironmentVariableCredentialsProvider.create()).build();

            //Validating connection
            if (dynamoDbClient == null) {
                System.err.println("Failed to create Dynamo DB connection, shutting down the application.");
                spark.Spark.stop();
            }
        } catch (DynamoDbException e) {
            System.err.println("Dynamo error : " + e.awsErrorDetails().errorMessage());
            e.printStackTrace();
        } catch (SdkClientException e) {
            System.err.println("SDK client error : " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Failed to connect to AWS DynamoDB: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public String readRecords() {
        System.out.println("Executing Redis cache.........");
        Map<String, Object> redisCached = getAllRecordsFromCache();
        if (redisCached != null) {
            System.out.println("Returned from Redis cache.");
            return JSONUtil.generateJsonStringFromRedisObject(redisCached);
        }
        System.out.println("Redis cache is empty, now try retrieving from Dynamo DB");
        List<Map<String, Object>> resultList = new ArrayList<>();
        try {
            //Create a ScanRequest
            ScanRequest scanRequest = ScanRequest.builder().tableName("GRE_GENAI").build();
            //Execute the scan
            ScanResponse scanResponse = dynamoDbClient.scan(scanRequest);
            System.out.println("ScanResponse : " + scanResponse);

            //Displaying all items
            for (Map<String, AttributeValue> item : scanResponse.items()) {
                Map<String, Object> resultMap = convertItemToMap(item);

                resultMap.forEach((Key, Value) -> System.out.println(Key + " : " + Value));
                resultList.add(resultMap);

                //Saved to Redis cache
                boolean saveMultipleRecords = saveToCache(resultMap, REDIS_CACHE_TTL_SECONDS);
                System.out.println(saveMultipleRecords ? "Successfully saved multiple records into Redis cache." : "Failed to save multiple records into Redis cache.");
                System.out.println("Saving key(s) to Redis: " + resultMap.keySet());

                System.out.println("Successfully retrieved table from Dynamo DB.");
            }
        } catch (DynamoDbException e) {
            System.err.println("Failed to retrieve values from Dynamo DB " + e.getMessage());
        }
        return JSONUtil.generateJsonStringFromObject(resultList);
    }

    public String createRecords(GreRequest greRequest) {
        try {
            Map<String, AttributeValue> resultSet = new HashMap<>();
            resultSet.put("Training_English_Word", AttributeValue.builder().s(greRequest.getName()).build());
            resultSet.put("Explanation", AttributeValue.builder().s(greRequest.getDefinition()).build());

            PutItemRequest request = PutItemRequest.builder().tableName("GRE_GENAI").item(resultSet).build();

            dynamoDbClient.putItem(request);
            System.out.println("Values inserted successfully into GRE_GENAI");

            //Save to Redis
            String redisKey = "GRE : " + greRequest.getName();
            boolean savedResult = saveToCache(redisKey, REDIS_CACHE_TTL_SECONDS, greRequest.getDefinition());
            System.out.println(savedResult ? "Saved to Redis cache" : "Failed to save the GRE details into cache.");

            return JSONUtil.generateJsonStringFromObject("New Gre word and definition updated : " + greRequest.getName());
        } catch (DynamoDbException e) {
            System.err.println("DynamoDB insertion failed: " + e.getMessage());
            return JSONUtil.generateErrorJsonStringFromObject("0");
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
            return JSONUtil.generateErrorJsonStringFromObject("0");
        }
    }

    public String deleteRecords(GreRequest greRequest) {
        try {
            Map<String, AttributeValue> item = new HashMap<>();
            item.put("Training_English_Word", AttributeValue.builder().s(greRequest.getName()).build());

            DeleteItemRequest deleteItemRequest = DeleteItemRequest.builder().tableName("GRE_GENAI").key(item).returnValues(ReturnValue.ALL_OLD).build();
            DeleteItemResponse deleteItemResponse = dynamoDbClient.deleteItem(deleteItemRequest);

            //Check if the name exists and deleted
            Map<String, AttributeValue> deletedItem = deleteItemResponse.attributes();
            if (deletedItem == null || deletedItem.isEmpty()) {
                return JSONUtil.generateErrorJsonStringFromObject("Gre word not found : " + greRequest.getName());
            }

            //Deleted GRE word from Redis cache
            String cacheKey = "GRE : " + greRequest.getName();
            boolean deletedKey = deleteFromCache(cacheKey);
            System.out.println(deletedKey ? "Key deleted From cache." : "Key not found in Redis.");

            return JSONUtil.generateJsonStringFromObject("Successfully deleted Gre word : " + greRequest.getName());
        } catch (DynamoDbException e) {
            System.err.println("DynamoDB deletion failed: " + e.getMessage());
            return JSONUtil.generateErrorJsonStringFromObject("DynamoDB error during delete");
        } catch (Exception e) {
            System.err.println("Failed to delete items from Dynamo DB table " + e.getMessage());
            return JSONUtil.generateErrorJsonStringFromObject("Unexpected error during delete");
        }
    }

    @Override
    public String readViewsCount(GreRequest greRequest) {
        List<Map<String, Object>> resultList = new ArrayList<>();
        try {
            //Create a ScanRequest
            ScanRequest scanRequest = ScanRequest.builder().tableName("GRE_GENAI").
                    projectionExpression("#nm ,#v").
                    expressionAttributeNames(Map.of("#nm", "Training_English_Word", "#v", "Views")).build();
            //Execute the scan
            ScanResponse scanResponse = dynamoDbClient.scan(scanRequest);
            System.out.println("ScanResponse : " + scanResponse);

            //Displaying all items
            for (Map<String, AttributeValue> item : scanResponse.items()) {
                Map<String, Object> resultMap = convertItemToMap(item);

                resultMap.forEach((Key, Value) -> System.out.println(Key + " : " + Value));
                resultList.add(resultMap);

                System.out.println("Successfully retrieved Name and Views count from Dynamo DB.");
            }
        } catch (DynamoDbException e) {
            System.err.println("Failed to retrieve values from Dynamo DB " + e.getMessage());
        }
        return JSONUtil.generateJsonStringFromObject(resultList);

    }

    //Method to get GRE word details by name
    public String readRecordsByName(GreRequest greRequest) {
        updateViewsCountDynamoDb(greRequest);
        String redisKey = "GRE : " + greRequest.getName();
        String cachedValue = getWordDetailsFromCache(redisKey);

        if (cachedValue != null) {
            System.out.println("Returned from Redis cached.");
            System.out.println(cachedValue);
            return JSONUtil.generateJsonStringFromRedisObject(cachedValue);
        }
        try {
            Map<String, AttributeValue> itemKey = new HashMap<>();
            itemKey.put("Training_English_Word", AttributeValue.builder().s(greRequest.getName()).build());

            GetItemRequest getItemRequest = GetItemRequest.builder().tableName("GRE_GENAI").key(itemKey).build();
            GetItemResponse itemResponse = dynamoDbClient.getItem(getItemRequest);
            Map<String, AttributeValue> item = itemResponse.item();


            if (item != null && !item.isEmpty()) {

                System.out.println("Converting into readable Map result");
                Map<String, Object> readableResult = convertItemToMap(item);

                readableResult.forEach((Key, Value) -> System.out.println(Key + " : " + Value));

                String jsonString = JSONUtil.generateJsonStringFromObject(readableResult);

                //Save to Cache for future
                boolean savedResult = saveToCache(redisKey, REDIS_CACHE_TTL_SECONDS, jsonString);
                System.out.println(savedResult ? "Saved to Redis cache" : "Failed to save the GRE details into cache.");

                return (jsonString + " Returned from DynamoDb Table.");
            } else {
                System.out.println("Item not found for key: " + getItemRequest.tableName());
                return JSONUtil.generateErrorJsonStringFromObject("Not found.");
            }
        } catch (DynamoDbException e) {
            System.err.println("Failed to get items from DynamoDB : " + e.getMessage());
            throw new RuntimeException(e);
        } catch (Exception e) {
            System.err.println("Failed to get items from Dynamo DB table " + e.getMessage());
            return null;
        }
    }

    @Override
    public String updateRecords(GreRequest greRequest) {
        try {
            Map<String, AttributeValue> itemKey = new HashMap<>();
            itemKey.put("Training_English_Word", AttributeValue.builder().s(greRequest.getName()).build());

            UpdateItemRequest updateItemRequest = UpdateItemRequest.builder().tableName("GRE_GENAI").key(itemKey).updateExpression("SET #d = :newdef").
                    expressionAttributeNames(Map.of("#d", "Explanation")).
                    expressionAttributeValues(Map.of(":newdef", AttributeValue.builder().s(greRequest.getDefinition()).build())).returnValues(ReturnValue.ALL_NEW).build();

            UpdateItemResponse updateItemResponse = dynamoDbClient.updateItem(updateItemRequest);
            Map<String, AttributeValue> updatedItem = updateItemResponse.attributes();

            //Update the Redis cache
            String redisKey = "GRE : " + greRequest.getName();
            boolean updatedCache = updateRedisCache(redisKey, REDIS_CACHE_TTL_SECONDS, greRequest.getDefinition());
            System.out.println(updatedCache ? "Updated Redis cache for Gre word " + redisKey + " with " + greRequest.getDefinition() : " Failed to update the Redis cache.");

            return JSONUtil.generateJsonStringFromObject("Updated name : " + updatedItem.get("Training_English_Word").s() + ",  with new definition : " + updatedItem.get("Explanation").s());
        } catch (DynamoDbException e) {
            System.err.println("Dynamo DB updating Gre word definition failed " + e.getMessage());
            return JSONUtil.generateErrorJsonStringFromObject("Failed to update Gre word definition " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Failed to update the Gre word definition in Dynamo DB");
            return null;
        }
    }

    //Method to update views count by name
    public static void updateViewsCountDynamoDb(GreRequest greRequest) {
        try {
            Map<String, AttributeValue> itemKey = new HashMap<>();
            itemKey.put("Training_English_Word", AttributeValue.builder().s(greRequest.getName()).build());

            UpdateItemRequest updateItemRequest = UpdateItemRequest.builder().tableName("GRE_GENAI").key(itemKey).
                    updateExpression("SET #v = if_not_exists(#v, :start) + :inc").
                    expressionAttributeNames(Map.of("#v", "Views")).
                    expressionAttributeValues(Map.of(":start", AttributeValue.builder().n("0").build(),
                            ":inc", AttributeValue.builder().n("1").build())).build();

            dynamoDbClient.updateItem(updateItemRequest);
            System.out.println("Successfully incremented the view count for : " + greRequest.getName());
        } catch (DynamoDbException e) {
            System.err.println("Dynamo DB updating views count failed " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Failed to update the Views count in Dynamo DB");
        }
    }

    // Convert AttributeValue map to readable Map<String, Object>
    public static Map<String, Object> convertItemToMap(Map<String, AttributeValue> item) {
        Map<String, Object> readableMap = new HashMap<>();

        for (Map.Entry<String, AttributeValue> entry : item.entrySet()) {
            String key = entry.getKey();
            AttributeValue value = entry.getValue();

            if (value.s() != null) {
                readableMap.put(key, value.s());
            } else if (value.n() != null) {
                readableMap.put(key, Double.parseDouble(value.n())); // Can cast to int if known
            } else if (value.bool() != null) {
                readableMap.put(key, value.bool());
            } else if (value.n() != null) {
                readableMap.put(entry.getKey(), Integer.parseInt(value.n()));
            } else {
                readableMap.put(key, value.toString()); // fallback for list/map/etc.
            }
        }
        return readableMap;
    }

    @Override
    public boolean deleteFromCache(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            Long result = jedis.del(key);
            return result != null && result > 0;
        }
    }

    @Override
    public boolean saveToCache(String key, long TTLSeconds, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            String savedResult = jedis.setex(key, REDIS_CACHE_TTL_SECONDS, value);
            return "OK".equals(savedResult);
        } catch (Exception e) {
            System.err.println("Redis error while saving to cache : " + e.getMessage());
            return false;
        }
    }

    @Override
    public String getWordDetailsFromCache(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.get(key);
        }
    }

    @Override
    public Map<String, Object> getAllRecordsFromCache() {
        Map<String, Object> resultMap = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        try (Jedis jedis = jedisPool.getResource()) {
            ScanParams scanParams = new ScanParams().match("GRE :*").count(200);
            String cursor = ScanParams.SCAN_POINTER_START;

            do {
                ScanResult<String> scanResult = jedis.scan(cursor, scanParams);
                List<String> keys = scanResult.getResult();

                for (String key : keys) {
                    String jsonValue = jedis.get(key);
                    if (jsonValue != null) {
                        try {
                            System.out.println("Deserializing value for key " + key + " : " + jsonValue);
                            Object valueObj = objectMapper.readValue(jsonValue, new TypeReference<Map<String, Object>>() {
                            });
                            resultMap.put(key, valueObj);
                        } catch (JsonProcessingException e) {
                            resultMap.put(key, Map.of("value", jsonValue));
                        }
                    }

                }
                cursor = scanResult.getCursor();
            } while (!cursor.equals(ScanParams.SCAN_POINTER_START));
        }
        //Return null if cache is empty
        return resultMap.isEmpty() ? null : resultMap;
    }

    @Override
    public boolean updateRedisCache(String key, long TTlSeconds, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            String updateCache = jedis.setex(key, REDIS_CACHE_TTL_SECONDS, value);
            return "OK".equals(updateCache);
        } catch (Exception e) {
            System.err.println("Failed to update the redis cache." + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean saveToCache(Map<String, Object> cacheEntries, long TTlSeconds) {
        ObjectMapper objectMapper = new ObjectMapper();
        try (Jedis jedis = jedisPool.getResource()) {
            String recordKey = "GRE : " + cacheEntries.get("Training_English_Word");
            String jsonValue = objectMapper.writeValueAsString(cacheEntries);
            String result = jedis.setex(recordKey, TTlSeconds, jsonValue);

//            for (Map.Entry<String, Object> entry : cacheEntries.entrySet()) {
//                String jsonValue = objectMapper.writeValueAsString(entry.getValue());
//                String result = jedis.setex(entry.getKey(), REDIS_CACHE_TTL_SECONDS, jsonValue);
            if (!"OK".equals(result)) {
                System.err.println("Failed to save multiple records into Redis cache.");
                return false;
            }
            System.out.println("Saved to Redis: " + recordKey + " -> " + jsonValue);
        } catch (JsonProcessingException ex) {
            System.err.println("Redis error caching.." + ex.getMessage());
            throw new RuntimeException(ex);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
