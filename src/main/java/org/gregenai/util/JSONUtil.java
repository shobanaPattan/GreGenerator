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

}
