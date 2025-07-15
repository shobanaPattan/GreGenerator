package org.gregenai.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.gregenai.model.HTTPHeaderModel;
import spark.Request;

import static spark.Spark.halt;

public class HTTPConfigUtil {
    final static Gson gson = new Gson();
    public static HTTPHeaderModel getConfigModelFromHTTP(Request request) {
        final HTTPHeaderModel model = HTTPHeaderModel.builder()
                .dataBaseType(getDataBaseType(request))
                .responseType(getResponseType(request))
//                .userNameType(getUserName(request))
                .build();
        // TODO: Set more headers
        return model;
    }

    private static String getResponseType(Request req) {
        String responseType = "application/json";
        String acceptHeaders = req.headers("Accept");
        String contentHeaders = req.headers("Content-Type");

        System.out.println("Executing getResponseType...");

        if (acceptHeaders == null || !acceptHeaders.toLowerCase().contains(responseType)) {
            halt(460, "Unsupported Accept header. Only application/json is supported.");
        }

        if (contentHeaders == null || !contentHeaders.toLowerCase().contains(responseType)) {
            halt(400, "Unsupported Content-Type header. Only application/json is supported.");
        }

        System.err.println("Not Executing getResponseType...");

        System.out.println(responseType);
        return responseType;
    }

    private static String getDataBaseType(Request request) {
//        final Gson gson = new Gson();
        final JsonObject jsonObject = gson.fromJson(request.body(), JsonObject.class);

        System.out.println("Executing getDataBaseType...");
        // TODO: if not present, default to MySQL / DDB
        if (jsonObject == null || !jsonObject.has("databaseType")) {
            throw new IllegalArgumentException("Missing 'databaseType' field in request.");
        }

        final String dataBaseType = jsonObject.get("databaseType").getAsString();
        if (dataBaseType == null || dataBaseType.isEmpty()) {
            throw new IllegalArgumentException("DataBaseType cannot be null or empty.");
        }
        System.err.println("Not Executing getDataBaseType...");

        System.out.println(dataBaseType);
        return dataBaseType;
    }

//    private static String getUserName(Request request) {
//        final JsonObject jsonObject = gson.fromJson(request.body(), JsonObject.class);
//        System.out.println("Executing getUserName...");
//        if (jsonObject == null || !jsonObject.has("userName")) {
//            throw new IllegalArgumentException("Missing user name input or email.");
//        }
//        String userName = jsonObject.get("userName").getAsString().trim();
//        if (userName.isEmpty()) {
//            throw new IllegalArgumentException("User Name or Email cannot be empty.");
//        }
//        System.out.println(userName);
//        return userName;
//    }





//    private static GreRequest validateAndReturnRequestBody(Request request) {
//        GreRequest greRequest;
//        try {
//            greRequest = gson.fromJson(request.body(), GreRequest.class);
//
//            if (greRequest == null) {
//                throw new IllegalArgumentException("Request body is empty, cannot be null.");
//            }
////            if (StringUtils.isNotEmpty(greRequest.getName()) && StringUtils.isNotEmpty(greRequest.getDefinition())) {
//            return greRequest;
////            }
//        } catch (JsonSyntaxException e) {
//            throw new IllegalArgumentException("Invalid JSON format.", e);
//        } catch (Exception e) {
//            throw new IllegalArgumentException("Input is not valid.");
//        }
////        return null;
//    }
}
