package org.gregenai.util;

import org.gregenai.model.GreRequest;
import org.gregenai.model.RequestData;
import spark.Request;

import static org.gregenai.validators.InputValidator.validateAndReturnRequestBody;

public class DataBaseTypeRequest {

    public static RequestData dataBaseTypeRequest(Request req) {
        // Validate and extract request body using improved method
        GreRequest greRequest = validateAndReturnRequestBody(req);

        String dataBaseType = greRequest.getDatabaseType();
        if (dataBaseType == null || dataBaseType.isEmpty()) {
            throw new IllegalArgumentException();
        }
        return new RequestData(greRequest.getDatabaseType(), greRequest);
    }
}
