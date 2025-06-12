package org.gregenai.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.gregenai.dependency.db.DataBaseConnectionAPI;
import org.gregenai.model.GreRequest;
import org.gregenai.model.QueryType;
import spark.Request;
import spark.Response;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

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

        Connection conn = DataBaseConnectionAPI.createConnection();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        if (conn == null) {
            System.err.println("Failed to create DataBase connection, shutting down the application.");
            spark.Spark.stop();
        }

        //Get table API
        get("/getAllGreWords", (req, res) -> {
            try {
                //Set JSON response
                setResponseType(req, res);

                //Parse JSON body to GreRequest object
                // TODO : Validate and sanitize all the inputs to make sure the request is valid
//                GreRequest greRequest = gson.fromJson(req.body(), GreRequest.class);

                //Converting String QueryType to enum QueryType
                // TODO : You decide query type, do not expect this from the caller
//                QueryType queryType = QueryType.valueOf(greRequest.getQueryType());

                // TODO: Pass the whole GreRequest object instead of name and definition separately
                // If this list grows, you have to keep changing this line
//                DataBaseConnectionAPI.executeDBQueries(conn, greRequest.getName(), greRequest.getDefinition(), queryType);
                List<Map<String, String>> result = DataBaseConnectionAPI.executeSelectAllSQLQuery(conn);

                // Return a JSON success message
                return gson.toJson(Map.of("status", "success", "message", "Query executed successfully", "result", result));

            } catch (Exception e) {
                System.err.println("Failed to find the table");
                e.printStackTrace();
                res.status();
                return gson.toJson(Map.of("status", "error", "message", "Failed to retrieve values from database."));
            }
        });

        //Get gre word definition API
        get("/getGreWord-Definition/:queryWord", (req, res) -> {
            try {
                //Set JSON response
                setResponseType(req, res);

                Map<String, Object> data = DataBaseConnectionAPI.executeSelectByNameSQLQuery(conn, req.params(":queryWord"));

                // Return a JSON success message and result
                return gson.toJson(Map.of("status", "success", "Result", data));
            } catch (Exception e) {
                System.err.println("Failed to find a definition");
                e.printStackTrace();
                res.status();
                return gson.toJson(Map.of("status", "error", "message", "Failed to retrieve values from database."));
            }
        });

        //POST word and definition API
        post("/postGreWord/:queryWord/:explanation", (req, res) -> {
            try {
                //Set JSON response
                setResponseType(req, res);

                int rowsAffected = DataBaseConnectionAPI.executeInsertSQLQuery(conn, req.params(":queryWord"), req.params(":explanation"));
                return gson.toJson(Map.of("status", "success", "RowsAffected", rowsAffected));
            } catch (Exception e) {
                System.err.println("Failed to add new row to the database");
                e.printStackTrace();
                res.status();
                return gson.toJson(Map.of("status", "error", "message", "Failed to insert values to database."));
            }
        });

        //DELETE row API
        delete("/deleteGreWord/:queryWord", (req, res) -> {
            try {
                //Set JSON response
                setResponseType(req, res);

                int rowsAffected = DataBaseConnectionAPI.executeDeleteSQLQuery(conn, req.params(":queryWord"));
                return gson.toJson(Map.of("status", "success", "RowsAffected", rowsAffected));
            } catch (Exception e) {
                System.err.println("Failed to delete the value from database!");
                e.printStackTrace();
                res.status();
                return gson.toJson(Map.of("status", "error", "message", "Failed to delete values from database."));
            }
        });

        get("/getGreWord-Views/:queryWord", (req, res) -> {
            try {
                //Set JSON response
                setResponseType(req, res);

                Map<String, Object> resultData = DataBaseConnectionAPI.executeGetViewsSqlQuery(conn, req.params(":queryWord"));
                return gson.toJson(Map.of("status", "success", "Result", resultData));
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
