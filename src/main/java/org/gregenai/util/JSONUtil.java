package org.gregenai.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Map;

public class JSONUtil {
    static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static String generateJsonStringFromObject(Object resultObject) {
        try {
            return gson.toJson(Map.of("status", "success", "Result", resultObject));
        } catch (Exception e) {
            System.err.println("Failed to convert Object into Json");
            e.printStackTrace();
            return gson.toJson(Map.of("status", "error", "message", "Failed to convert into Json."));
        }
    }

    public static String generateErrorJsonStringFromObject(Object resultObject) {
        try {
            return gson.toJson(Map.of("status", "error", "Result", resultObject));
        } catch (Exception e) {
            System.err.println("Failed to convert Object into Json");
            e.printStackTrace();
            return gson.toJson(Map.of("status", "error", "message", "Failed to convert into Json."));
        }
    }

    public static String generateJsonStringFromRedisObject(Object resultObject) {
        try {
            //Parse the String into Map
            Map<String, Object> parsed = new Gson().fromJson((String) resultObject, Map.class);

            //Extracting the actual Result field if it's wrapped
            Object innerResult = parsed.get("Result");

            return gson.toJson(Map.of("status", "success", "Result", innerResult != null ? innerResult : parsed, "source", "Redis"));
        } catch (Exception e) {
            System.err.println("Failed to convert Object into Json");
            e.printStackTrace();
            return gson.toJson(Map.of("status", "error", "message", "Failed to convert into Json."));
        }
    }

}
