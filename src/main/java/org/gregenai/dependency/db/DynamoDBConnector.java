package org.gregenai.dependency.db;

import org.gregenai.model.GreRequest;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DynamoDBConnector {
    public static DynamoDbClient getClient() {
        try {
            System.out.println(" ********* Connecting to AWS DynamoDB ********* ");
            return DynamoDbClient.builder().
                    region(Region.US_EAST_1).
                    credentialsProvider(EnvironmentVariableCredentialsProvider.create()).build();

        } catch (DynamoDbException e) {
            System.err.println("Dynamo error : " + e.awsErrorDetails().errorMessage());
        } catch (SdkClientException e) {
            System.err.println("SDK client error : " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Failed to connect to AWS DynamoDB: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public static List<Map<String, Object>> selectDynamoDbTable(DynamoDbClient dynamoDbClient) {
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

                System.out.println("Successfully retrieved table from Dynamo DB.");
            }
        } catch (DynamoDbException e) {
            System.err.println("Failed to retrieve values from Dynamo DB " + e.getMessage());
        }
        return resultList;
    }

    public static int insertGreWordIntoDynamoDb(DynamoDbClient dynamoDbClient, GreRequest greRequest) {
        try {
            Map<String, AttributeValue> resultSet = new HashMap<>();
            resultSet.put("Training_English_Word", AttributeValue.builder().s(greRequest.getName()).build());
            resultSet.put("Explanation", AttributeValue.builder().s(greRequest.getDefinition()).build());

            PutItemRequest request = PutItemRequest.builder().tableName("GRE_GENAI").item(resultSet).build();

            dynamoDbClient.putItem(request);
            System.out.println("Values inserted successfully into GRE_GENAI");
            return 1;
        } catch (DynamoDbException e) {
            System.err.println("DynamoDB insertion failed: " + e.getMessage());
            return 0;
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }

    public static int deleteGreWordDetailsFromDynamoDb(DynamoDbClient dynamoDbClient, GreRequest greRequest) {
        try {
            Map<String, AttributeValue> item = new HashMap<>();
            item.put("Training_English_Word", AttributeValue.builder().s(greRequest.getName()).build());

            DeleteItemRequest deleteItemRequest = DeleteItemRequest.builder().tableName("GRE_GENAI").key(item).build();
            dynamoDbClient.deleteItem(deleteItemRequest);
            return 1;
        } catch (DynamoDbException e) {
            System.err.println("DynamoDB deletion failed: " + e.getMessage());
            return 0;
        } catch (Exception e) {
            System.err.println("Failed to delete items from Dynamo DB table " + e.getMessage());
            return 0;
        }
    }

    //Method to get GRE word details by name
    public static Map<String, Object> getGreWordDetailsFromDynamoDb(DynamoDbClient dynamoDbClient, GreRequest greRequest) {
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
                return readableResult;
            } else {
                System.out.println("Item not found for key: " + getItemRequest.tableName());
                return null;
            }
        } catch (DynamoDbException e) {
            System.err.println("Failed to get items from DynamoDB : " + e.getMessage());
            throw new RuntimeException(e);
        } catch (Exception e) {
            System.err.println("Failed to get items from Dynamo DB table " + e.getMessage());
            return null;
        }
    }

    //Method to update views count by name
    public static void updateViewsCountDynamoDb(DynamoDbClient dynamoDbClient, GreRequest greRequest) {
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
            } else {
                readableMap.put(key, value.toString()); // fallback for list/map/etc.
            }
        }
        return readableMap;
    }

}
