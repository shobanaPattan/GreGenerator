package org.gregenai.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.gregenai.model.HTTPHeaderModel;
import spark.Request;

import static spark.Spark.halt;

public class HTTPConfigUtil {
    public static HTTPHeaderModel getConfigModelFromHTTP(Request request) {
        final HTTPHeaderModel model = HTTPHeaderModel.builder()
                .dataBaseType(getDataBaseType(request))
                .responseType(getResponseType(request))
                .build();
        // TODO: Set more headers
        return model;
    }

    private static String getResponseType(Request req) {
        String responseType = "application/json";
        String acceptHeaders = req.headers("Accept");
        String contentHeaders = req.headers("Content-Type");
//        if (!responseType.equalsIgnoreCase(req.headers("Accept"))) {
//            throw new IllegalArgumentException("Unsupported Header: Accept. Only JSON supported.");
//        }


        if (acceptHeaders == null || !acceptHeaders.toLowerCase().contains(responseType)) {
//            throw new IllegalArgumentException("Unsupported Header: Accept. Only JSON supported.");
            halt(460, "Unsupported Accept header. Only application/json is supported.");
        }

        if (contentHeaders == null || !contentHeaders.toLowerCase().contains(responseType)) {
//            throw new IllegalArgumentException("Unsupported Header: Content-Type. Only application/json is supported.");
            halt(400, "Unsupported Content-Type header. Only application/json is supported.");
        }
        return responseType;
    }

    private static String getDataBaseType(Request request) {
        final Gson gson = new Gson();
        final JsonObject jsonObject = gson.fromJson(request.body(), JsonObject.class);

        // TODO: if not present, default to MySQL / DDB
        if (jsonObject == null || !jsonObject.has("databaseType")) {
            throw new IllegalArgumentException("Missing 'databaseType' field in request.");
        }

        final String dataBaseType = jsonObject.get("databaseType").toString();
        if (dataBaseType == null || dataBaseType.isEmpty()) {
            throw new IllegalArgumentException("DataBaseType cannot be null or empty.");
        }
        return dataBaseType;
    }

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
