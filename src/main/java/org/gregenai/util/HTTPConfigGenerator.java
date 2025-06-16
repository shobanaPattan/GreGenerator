package org.gregenai.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.gregenai.model.HTTPHeaderModel;
import spark.Request;

public class HTTPConfigGenerator {
    public static HTTPHeaderModel getConfigModelFromHTTP(Request request) {
        HTTPHeaderModel model = new HTTPHeaderModel();
        model.setResponseType(getResponseType(request));
        model.setDataBaseType(getDataBaseType(request));
        // TODO: Set more headers
        return model;
    }

    private static String getResponseType(Request req) {
        String responseType = "application/json";
        if (!responseType.equalsIgnoreCase(req.headers("Accept"))) {
            throw new IllegalArgumentException("Unsupported Header: Accept. Only JSON supported.");
        }
        return responseType;
    }

    private static String getDataBaseType(Request request) {
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(request.body(), JsonObject.class);

        if (jsonObject == null || !jsonObject.has("databaseType")) {
            throw new IllegalArgumentException("Missing 'databaseType' field in request.");
        }
        String dataBaseType = jsonObject.get("databaseType").getAsString();
        if (dataBaseType == null || dataBaseType.isEmpty()) {
            throw new IllegalArgumentException("DataBaseType cannot be null or empty.");
        }
        return dataBaseType;
    }
}
