package org.gregenai.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.gregenai.dependency.db.DynamoDBConnector;
import org.gregenai.dependency.db.MySQLDataBaseConnectionAPI;
import org.gregenai.model.GreRequest;
import org.gregenai.model.RequestData;
import org.gregenai.util.DataBaseTypeRequest;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import spark.Request;
import spark.Response;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static org.gregenai.validators.InputValidator.validateAndReturnRequestBody;
import static spark.Spark.*;

//http://localhost:4567/getGreWord executes the following program on port 4567
public class GREWordTrainerAPI {
    static String responseType = "application/json";

    public static void setResponseType(Request req, Response res) {
        String responseType = "application/json";
        if ("text/html".equalsIgnoreCase(req.headers("Accept"))) {
            responseType = "text/html";
        }
        //Set JSON response
        res.type(responseType);
    }

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        System.out.println("Loading API's");

        Connection conn = MySQLDataBaseConnectionAPI.createConnection();
        DynamoDbClient databaseClient = DynamoDBConnector.getClient();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        if (conn == null) {
            System.err.println("Failed to create DataBase connection, shutting down the application.");
            spark.Spark.stop();
        }

        //Get API
        get("/getAllGreWords", (req, res) -> {
            try {
                //Set JSON response
                setResponseType(req, res);

                RequestData requestData = DataBaseTypeRequest.dataBaseTypeRequest(req);
                Object result;

                if (requestData.getDatabaseType().equals("mysql")) {
                    result = MySQLDataBaseConnectionAPI.selectSQLTable(conn);
                } else if (requestData.getDatabaseType().equals("dynamodb")) {
                    result = DynamoDBConnector.selectDynamoDbTable(databaseClient);
                } else {
                    throw new IllegalArgumentException("Unsupported database type: " + requestData.getDatabaseType());
                }
                // Return a JSON success message
                return gson.toJson(Map.of("status", "success", "message", "Query executed successfully", "databasetype", requestData.getDatabaseType(), "result", result));

            } catch (Exception e) {
                System.err.println("Failed to find the table");
                e.printStackTrace();
                res.status();
                return gson.toJson(Map.of("status", "error", "message", "Failed to retrieve values from database."));
            }
        });

        //Get Gre Word Details
        get("/getGreWordDetails", (req, res) -> {
            try {
                //Set JSON response
                setResponseType(req, res);

                RequestData requestData = DataBaseTypeRequest.dataBaseTypeRequest(req);
                Map<String, Object> resultData;

                if (requestData.getDatabaseType().equals("mysql")) {
                    resultData = MySQLDataBaseConnectionAPI.getGreWordDetailsFromMySQL(conn, requestData.getGreRequest());
                } else if (requestData.getDatabaseType().equals("dynamodb")) {
                    resultData = DynamoDBConnector.getGreWordDetailsFromDynamoDb(databaseClient, requestData.getGreRequest());
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

        //POST word and definition API
        post("/postGreWord", (req, res) -> {
            try {
                //Set JSON response
                setResponseType(req, res);

                RequestData requestData = DataBaseTypeRequest.dataBaseTypeRequest(req);
                int result;

                if (requestData.getDatabaseType().equals("mysql")) {
                    result = MySQLDataBaseConnectionAPI.insertGreWordIntoSQL(conn, requestData.getGreRequest());
                } else if (requestData.getDatabaseType().equals("dynamodb")) {
                    result = DynamoDBConnector.insertGreWordIntoDynamoDb(databaseClient, requestData.getGreRequest());
                } else {
                    throw new IllegalArgumentException("Unsupported database type: " + requestData.getDatabaseType());
                }

                return gson.toJson(Map.of("status", "success", "RowsAffected", result));
            } catch (IllegalArgumentException exception) {
                return gson.toJson(Map.of("status", "error", "message", "Input is invalid."));
            } catch (Exception e) {
                System.err.println("Failed to add new row to the database");
                e.printStackTrace();
                res.status();
                return gson.toJson(Map.of("status", "error", "message", "Failed to insert values to database."));
            }
        });

        //DELETE API
        delete("/deleteItem", (req, res) -> {
            try {
                //Set JSON response
                setResponseType(req, res);
                RequestData requestData = DataBaseTypeRequest.dataBaseTypeRequest(req);
                int result;

                if (requestData.getDatabaseType().equals("mysql")) {
                    result = MySQLDataBaseConnectionAPI.deleteGreWordDetailsFromSQL(conn, requestData.getGreRequest());
                } else if (requestData.getDatabaseType().equals("dynamodb")) {
                    result = DynamoDBConnector.deleteGreWordDetailsFromDynamoDb(databaseClient, requestData.getGreRequest());
                } else {
                    throw new IllegalArgumentException();
                }
                return gson.toJson(Map.of("status", "success", "RowsAffected", result, "message", "Successfully deleted items from database."));

            } catch (IllegalArgumentException exception) {
                return gson.toJson(Map.of("status", "error", "message", "Input is invalid."));
            } catch (Exception e) {
                System.err.println("Failed to delete the value from database!");
                e.printStackTrace();
                res.status();
                return gson.toJson(Map.of("status", "error", "message", "Failed to delete values from database."));
            }
        });

        put("/updateGreWordViewCount", (req, res) -> {
            try {
                //Set JSON response
                setResponseType(req, res);
                RequestData requestData = DataBaseTypeRequest.dataBaseTypeRequest(req);

                if (requestData.getDatabaseType().equals("mysql")) {
                    MySQLDataBaseConnectionAPI.updateViewsCountSqlQuery(conn, requestData.getGreRequest());
                } else if (requestData.getDatabaseType().equals("dynamodb")) {
                    DynamoDBConnector.updateViewsCountDynamoDb(databaseClient, requestData.getGreRequest());
                } else {
                    throw new IllegalArgumentException();
                }
                return gson.toJson(Map.of("status", "success", "message", "Successfully updated the view count of " + requestData.getGreRequest().getName()));

            } catch (IllegalArgumentException exception) {
                return gson.toJson(Map.of("status", "error", "message", "Input is invalid."));
            } catch (Exception e) {
                System.err.println("Failed to update the view count into database");
                e.printStackTrace();
                res.status();
                return gson.toJson(Map.of("status", "error", "message", "Failed to update view count into database."));
            }
        });

        get("/getGreWordViewsCount", (req, res) -> {
            try {
                //Set JSON response
                setResponseType(req, res);

                RequestData requestData = DataBaseTypeRequest.dataBaseTypeRequest(req);
                Map<String, Object> resultData;

                if (requestData.getDatabaseType().equals("mysql")) {
                    resultData = MySQLDataBaseConnectionAPI.getViewsCountSql(conn, requestData.getGreRequest());
                } else if (requestData.getDatabaseType().equals("dynamodb")) {
                    resultData = DynamoDBConnector.getGreWordDetailsFromDynamoDb(databaseClient, requestData.getGreRequest());
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
