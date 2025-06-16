package org.gregenai.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.gregenai.dependency.db.DynamoDBConnector;
import org.gregenai.dependency.db.MySQLDBConnector;
import org.gregenai.factory.DataBaseConnectorFactory;
import org.gregenai.model.GreRequest;
import org.gregenai.model.HTTPHeaderModel;
import org.gregenai.util.AbstractDataBaseConnector;
import org.gregenai.util.HTTPConfigGenerator;
import org.gregenai.util.JSONUtil;

import java.sql.SQLException;
import java.util.Map;

import static org.gregenai.validators.InputValidator.validateAndReturnRequestBody;
import static spark.Spark.*;

//http://localhost:4567/getGreWord executes the following program on port 4567
public class GREWordTrainerAPI {
//    static String responseType = "application/json";

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        System.out.println("Loading API's");

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        //Get API
        get("/getAllGreWords", (req, res) -> {
            try {
                HTTPHeaderModel httpConfigModel = HTTPConfigGenerator.getConfigModelFromHTTP(req);
                //Set JSON response
                res.type(httpConfigModel.getResType());
                AbstractDataBaseConnector db = DataBaseConnectorFactory.getDataBaseConnector(httpConfigModel.getDataBaseType());
                return db.readRecords();
            } catch (Exception e) {
                System.err.println("Failed to find the table");
                e.printStackTrace();
                res.status();
                return JSONUtil.generateErrorJsonStringFromObject("Failed to retrieve values from database.");
            }
        });

        //Get Gre Word Details
        get("/getGreWordDetailsByName", (req, res) -> {
            try {
                HTTPHeaderModel httpConfigModel = HTTPConfigGenerator.getConfigModelFromHTTP(req);
                //Set JSON response
                res.type(httpConfigModel.getResType());

                GreRequest greRequest = validateAndReturnRequestBody(req);
                Map<String, Object> resultData;

                if (HttpParams.getDataBaseType(req).equals("mysql")) {
                    resultData = MySQLDBConnector.getGreWordDetailsFromMySQL(greRequest);
                } else if (HttpParams.getDataBaseType(req).equals("dynamodb")) {
                    resultData = DynamoDBConnector.getGreWordDetailsFromDynamoDb(greRequest);
                } else {
                    throw new IllegalArgumentException();
                }

                // Return a JSON success message and result
                return gson.toJson(Map.of("status", "success", "Result", resultData));
            } catch (IllegalArgumentException exception) {
                return gson.toJson(Map.of("status", "error", "message", "Input is invalid."));
            } catch (Exception e) {
                System.err.println("Failed to find a definition");
                e.printStackTrace();
                res.status();
                return gson.toJson(Map.of("status", "error", "message", "Failed to retrieve values from database."));
            }
        });

//        //POST word and definition API
//        post("/postGreWord", (req, res) -> {
//            try {
//                //Set JSON response
//                HTTPHeaderModel httpConfigModel = HTTPConfigGenerator.getConfigModelFromHTTP(req);
//                //Set JSON response
//                res.type(httpConfigModel.getResType());
//
//                GreRequest greRequest = validateAndReturnRequestBody(req);
//
//                int result;
//
//                if (HttpParams.getDataBaseType(req).equals("mysql")) {
//                    result = MySQLDBConnector.createRecords(greRequest);
//                } else if (HttpParams.getDataBaseType(req).equals("dynamodb")) {
//                    result = DynamoDBConnector.insertGreWordIntoDynamoDb(greRequest);
//                } else {
//                    throw new IllegalArgumentException("Unsupported database type: " + HttpParams.getDataBaseType(req));
//                }
//
//                return gson.toJson(Map.of("status", "success", "RowsAffected", result));
//            } catch (IllegalArgumentException exception) {
//                return gson.toJson(Map.of("status", "error", "message", "Input is invalid."));
//            } catch (Exception e) {
//                System.err.println("Failed to add new row to the database");
//                e.printStackTrace();
//                res.status();
//                return gson.toJson(Map.of("status", "error", "message", "Failed to insert values to database."));
//            }
//        });

        //DELETE API
//        delete("/deleteItemByName", (req, res) -> {
//            try {
//                //Set JSON response
//                HTTPHeaderModel httpConfigModel = HTTPConfigGenerator.getConfigModelFromHTTP(req);
//                //Set JSON response
//                res.type(httpConfigModel.getResType());
//
//                GreRequest greRequest = validateAndReturnRequestBody(req);
//
//                int result;
//
//                if (HttpParams.getDataBaseType(req).equals("mysql")) {
//                    result = MySQLDBConnector.deleteRecords(greRequest);
//                } else if (HttpParams.getDataBaseType(req).equals("dynamodb")) {
//                    result = DynamoDBConnector.deleteGreWordDetailsFromDynamoDb(greRequest);
//                } else {
//                    throw new IllegalArgumentException();
//                }
//                return gson.toJson(Map.of("status", "success", "RowsAffected", result, "message", "Successfully deleted items from database."));
//
//            } catch (IllegalArgumentException exception) {
//                return gson.toJson(Map.of("status", "error", "message", "Input is invalid."));
//            } catch (Exception e) {
//                System.err.println("Failed to delete the value from database!");
//                e.printStackTrace();
//                res.status();
//                return gson.toJson(Map.of("status", "error", "message", "Failed to delete values from database."));
//            }
//        });

//        put("/updateGreWordViewCountByName", (req, res) -> {
//            try {
//                //Set JSON response
//                HTTPHeaderModel httpConfigModel = HTTPConfigGenerator.getConfigModelFromHTTP(req);
//                //Set JSON response
//                res.type(httpConfigModel.getResType());
//
//                GreRequest greRequest = validateAndReturnRequestBody(req);
////
////                if (HttpParams.getDataBaseType(req).equals("mysql")) {
////                    MySQLDBConnector.updateRecords(greRequest);
////                } else if (HttpParams.getDataBaseType(req).equals("dynamodb")) {
////                    DynamoDBConnector.updateViewsCountDynamoDb(greRequest);
////                } else {
////                    throw new IllegalArgumentException();
////                }
////                return gson.toJson(Map.of("status", "success", "message", "Successfully updated the view count of " + greRequest.getName()));
//
//            } catch (IllegalArgumentException exception) {
//                return gson.toJson(Map.of("status", "error", "message", "Input is invalid."));
//            } catch (Exception e) {
//                System.err.println("Failed to update the view count into database");
//                e.printStackTrace();
//                res.status();
//                return gson.toJson(Map.of("status", "error", "message", "Failed to update view count into database."));
//            }
//        });

        get("/getGreWordViewsCountByName", (req, res) -> {
            try {
                //Set JSON response
                HTTPHeaderModel httpConfigModel = HTTPConfigGenerator.getConfigModelFromHTTP(req);
                //Set JSON response
                res.type(httpConfigModel.getResType());

                GreRequest greRequest = validateAndReturnRequestBody(req);

                Map<String, Object> resultData;

                if (HttpParams.getDataBaseType(req).equals("mysql")) {
                    resultData = MySQLDBConnector.getViewsCountSql(greRequest);
                } else if (HttpParams.getDataBaseType(req).equals("dynamodb")) {
                    resultData = DynamoDBConnector.getGreWordDetailsFromDynamoDb(greRequest);
                } else {
                    throw new IllegalArgumentException();
                }
                return gson.toJson(Map.of("status", "success", "result", resultData));

            } catch (IllegalArgumentException exception) {
                return gson.toJson(Map.of("status", "error", "message", "Input is invalid."));
            } catch (Exception e) {
                System.err.println("Failed to retrieve values from database");
                e.printStackTrace();
                res.status();
                return gson.toJson(Map.of("status", "error", "message", "Failed to retrieve values from database."));
            }
        });


        awaitInitialization(); // make sure server is ready

        System.out.println("Server started on port 4567");

        System.out.println("API loaded");
    }


}
