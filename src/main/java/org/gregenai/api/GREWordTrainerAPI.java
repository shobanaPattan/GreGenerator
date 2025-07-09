package org.gregenai.api;

import org.apache.hadoop.shaded.com.google.gson.JsonObject;
import org.gregenai.factory.DataBaseConnectorFactory;
import org.gregenai.model.GreRequest;
import org.gregenai.model.HTTPHeaderModel;
import org.gregenai.dependency.db.AbstractDataBaseConnector;
import org.gregenai.util.HTTPConfigUtil;
import org.gregenai.util.JSONUtil;

import static org.gregenai.dependency.db.DynamoDBConnector.checkUserNameOrEmailExists;
import static org.gregenai.htmlload.LoadHTMLFile.loadHtmlFile;
import static org.gregenai.validators.InputValidator.validateAndReturnRequestBody;
import static spark.Spark.*;

//http://localhost:4567/getGreWord executes the following program on port 4567
// TODO: Add java doc for this class and every API
public class GREWordTrainerAPI {
    public static void main(String[] args) {
        System.out.println("Loading API's");

        staticFiles.location("/public");

        before((req, res) -> {
            System.out.println("Incoming request: " + req.requestMethod() + " " + req.pathInfo());
        });

        //Get all gre word records
        post("/getAllRecords", (req, res) -> {
            try {
                // build http header model
                HTTPHeaderModel httpConfigModel = HTTPConfigUtil.getConfigModelFromHTTP(req);
                //Set JSON response
                res.type(httpConfigModel.getResponseType());

                AbstractDataBaseConnector db = DataBaseConnectorFactory.getDataBaseConnector(httpConfigModel.getDataBaseType());

                return db.readRecords();

            } catch (Exception e) {
                System.err.println("Failed to find the table");
                e.printStackTrace();
                res.status();
                return JSONUtil.generateErrorJsonStringFromObject("Failed to retrieve values from database.");
            }
        });

        //Get Gre Word Details by name
        post("/getGreWordDetailsByName", (req, res) -> {
            System.out.println("Received API call.");

            try {
                HTTPHeaderModel httpConfigModel = HTTPConfigUtil.getConfigModelFromHTTP(req);
                //Set JSON response
                res.type(httpConfigModel.getResponseType());

                GreRequest greRequest = validateAndReturnRequestBody(req);

                AbstractDataBaseConnector db = DataBaseConnectorFactory.getDataBaseConnector(httpConfigModel.getDataBaseType());

                return db.readRecordsByName(greRequest);

            } catch (IllegalArgumentException exception) {
                return JSONUtil.generateErrorJsonStringFromObject("Input is invalid.");
            } catch (Exception e) {
                System.err.println("Failed to find a definition");
                e.printStackTrace();
                res.status();
                return JSONUtil.generateErrorJsonStringFromObject("Failed to retrieve values from database.");
            }
        });

        //POST Gre word and definition
        post("/postGreWord", (req, res) -> {
            try {
                HTTPHeaderModel httpConfigModel = HTTPConfigUtil.getConfigModelFromHTTP(req);
                //Set JSON response
                res.type(httpConfigModel.getResponseType());

                GreRequest greRequest = validateAndReturnRequestBody(req);

                AbstractDataBaseConnector db = DataBaseConnectorFactory.getDataBaseConnector(httpConfigModel.getDataBaseType());

                return db.createRecords(greRequest);

            } catch (IllegalArgumentException exception) {
                return JSONUtil.generateErrorJsonStringFromObject("Input is invalid.");
            } catch (Exception e) {
                System.err.println("Failed to add new row to the database");
                e.printStackTrace();
                res.status();
                return JSONUtil.generateErrorJsonStringFromObject("Failed to insert values to database.");
            }
        });

        // DELETE Gre word by name
        delete("/deleteItemByName", (req, res) -> {
            try {

                HTTPHeaderModel httpConfigModel = HTTPConfigUtil.getConfigModelFromHTTP(req);
                //Set JSON response
                res.type(httpConfigModel.getResponseType());

                GreRequest greRequest = validateAndReturnRequestBody(req);

                AbstractDataBaseConnector db = DataBaseConnectorFactory.getDataBaseConnector(httpConfigModel.getDataBaseType());

                return db.deleteRecords(greRequest);

            } catch (IllegalArgumentException exception) {
                return JSONUtil.generateErrorJsonStringFromObject("Input is invalid.");
            } catch (Exception e) {
                System.err.println("Failed to delete the value from database!");
                e.printStackTrace();
                res.status();
                return JSONUtil.generateErrorJsonStringFromObject("Failed to delete values from database.");
            }
        });

        //Update existing Gre word definition
        put("/updateGreDefinitionByName", (req, res) -> {
            try {
                HTTPHeaderModel httpConfigModel = HTTPConfigUtil.getConfigModelFromHTTP(req);
                //Set JSON response
                res.type(httpConfigModel.getResponseType());

                GreRequest greRequest = validateAndReturnRequestBody(req);

                AbstractDataBaseConnector db = DataBaseConnectorFactory.getDataBaseConnector(httpConfigModel.getDataBaseType());

                return db.updateRecords(greRequest);

            } catch (IllegalArgumentException exception) {
                return JSONUtil.generateErrorJsonStringFromObject("Input is invalid.");
            } catch (Exception e) {
                System.err.println("Failed to update the Gre word definition into database");
                e.printStackTrace();
                res.status();
                return JSONUtil.generateErrorJsonStringFromObject("Failed to update Gre word definition into database.");
            }
        });

        //Get Gre word views count by name
        get("/getGreWordViewsCountByName", (req, res) -> {
            try {
                //Set JSON response
                HTTPHeaderModel httpConfigModel = HTTPConfigUtil.getConfigModelFromHTTP(req);
                //Set JSON response
                res.type(httpConfigModel.getResponseType());

                GreRequest greRequest = validateAndReturnRequestBody(req);

                AbstractDataBaseConnector db = DataBaseConnectorFactory.getDataBaseConnector(httpConfigModel.getDataBaseType());

                return db.readViewsCount(greRequest);

            } catch (IllegalArgumentException exception) {
                return JSONUtil.generateErrorJsonStringFromObject("Input is invalid.");
            } catch (Exception e) {
                System.err.println("Failed to retrieve values from database");
                e.printStackTrace();
                res.status();
                return JSONUtil.generateErrorJsonStringFromObject("Failed to retrieve name and views count from database.");
            }
        });

        //Get Gre Word details with the least views count
        post("/getGreWordDetailsByViewCount", (req, res) -> {
            try {
                HTTPHeaderModel httpConfigModel = HTTPConfigUtil.getConfigModelFromHTTP(req);
                //Set JSON response
                res.type(httpConfigModel.getResponseType());

                AbstractDataBaseConnector db = DataBaseConnectorFactory.getDataBaseConnector(httpConfigModel.getDataBaseType());

                return db.readNameByViewsCount();

            } catch (IllegalArgumentException exception) {
                return JSONUtil.generateErrorJsonStringFromObject("Input is invalid.");
            } catch (Exception e) {
                System.err.println("Failed to find a definition");
                e.printStackTrace();
                res.status();
                return JSONUtil.generateErrorJsonStringFromObject("Failed to retrieve values from database.");
            }
        });


        get("/getGreApp", (req, res) -> {
            try {
                res.type("text/html");
                return loadHtmlFile("index.html");
            } catch (Exception e) {
                e.printStackTrace();
                return JSONUtil.generateErrorJsonStringFromObject("Failed to execute HTML file.");
            }
        });


        post("/checkUserNameOrEmail", (req, res) -> {
            try {
                HTTPHeaderModel httpConfigModel = HTTPConfigUtil.getConfigModelFromHTTP(req);
                //Set JSON response
                res.type(httpConfigModel.getResponseType());

                boolean exists = checkUserNameOrEmailExists(httpConfigModel.getUserNameType());
                JsonObject result = new JsonObject();
                result.addProperty("exists", exists);
                return result.toString();
            } catch (IllegalArgumentException e) {
                return JSONUtil.generateErrorJsonStringFromObject(e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                return JSONUtil.generateErrorJsonStringFromObject("Internal server error.");
            }
        });






        awaitInitialization(); // make sure server is ready

        System.out.println("Server started on port 4567");

        System.out.println("API loaded");
    }


}
