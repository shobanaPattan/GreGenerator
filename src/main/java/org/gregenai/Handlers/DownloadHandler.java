package org.gregenai.Handlers;

import com.google.gson.Gson;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import javax.print.attribute.Attribute;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DownloadHandler {

    //CSV download method
    public static String formatAsCSV(List<Map<String, AttributeValue>> items) {
        StringBuilder csv = new StringBuilder("Word,Definition,Example\n");
        for (Map<String, AttributeValue> item : items) {
            String word = item.getOrDefault("Training_English_Word", AttributeValue.fromS("")).s();
            String definition = item.getOrDefault("Explanation", AttributeValue.fromS("")).s();
            String example = item.getOrDefault("Example", AttributeValue.fromS("")).s();

            csv.append(word).append(",")
                    .append(definition).append(",")
                    .append(example).append("\n");
        }
        return csv.toString();
    }


    public static String userDetailsFormatAsCSV(List<Map<String, AttributeValue>> items) {
        StringBuilder csv = new StringBuilder("UserName,Address,Email,First Name,Last Name\n");
        for (Map<String, AttributeValue> item : items) {
            String userName = item.getOrDefault("UserName", AttributeValue.fromS("")).s();
            String address = item.getOrDefault("Address", AttributeValue.fromS("")).s();
            String email = item.getOrDefault("email", AttributeValue.fromS("")).s();
            String firstname = item.getOrDefault("First Name", AttributeValue.fromS("")).s();
            String lastName = item.getOrDefault("Last Name", AttributeValue.fromS("")).s();

            csv.append(userName).append(",")
                    .append(address).append(",")
                    .append(email).append(",")
                    .append(firstname).append(",")
                    .append(lastName).append("\n");
        }
        return csv.toString();
    }


    //JSON download method
    public static String formatAsJson(List<Map<String, AttributeValue>> items) {
        Gson gson = new Gson();
        List<Map<String, String>> outputList = new ArrayList<>();

        for (Map<String, AttributeValue> item : items) {
            Map<String, String> flatItem = new HashMap<>();
            flatItem.put("Training_English_Word", item.getOrDefault("Training_English_Word", AttributeValue.fromS("")).s());
            flatItem.put("Explanation", item.getOrDefault("Explanation", AttributeValue.fromS("")).s());
            flatItem.put("Example", item.getOrDefault("Example", AttributeValue.fromS("")).s());
            outputList.add(flatItem);
        }
        return gson.toJson(outputList);
    }
}
