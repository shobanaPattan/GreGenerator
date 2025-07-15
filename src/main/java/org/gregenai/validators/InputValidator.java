package org.gregenai.validators;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import org.gregenai.model.GreRequest;
import spark.Request;

public class InputValidator {
    static Gson gson = new Gson();

    public static GreRequest validateAndReturnRequestBody(Request requestBody) {
        GreRequest greRequest;
        try {
            greRequest = gson.fromJson(requestBody.body(), GreRequest.class);
            if (greRequest == null) {
                throw new IllegalArgumentException("Request body is empty.");
            }
//            if (StringUtils.isNotEmpty(greRequest.getName()) && StringUtils.isNotEmpty(greRequest.getDefinition())) {
            return greRequest;
//            }
        } catch (JsonSyntaxException e) {
            throw new IllegalArgumentException("Invalid JSON format.", e);
        } catch (Exception e) {
            throw new IllegalArgumentException("Input is not valid.");
        }
//        return null;
    }

    private static String getUserName(Request request) {
        final JsonObject jsonObject = gson.fromJson(request.body(), JsonObject.class);
        System.out.println("Executing getUserName...");
        if (jsonObject == null || !jsonObject.has("userName")) {
            throw new IllegalArgumentException("Missing user name input or email.");
        }
        String userName = jsonObject.get("userName").getAsString().trim();
        if (userName.isEmpty()) {
            throw new IllegalArgumentException("User Name or Email cannot be empty.");
        }
        System.out.println(userName);
        return userName;
    }

}
