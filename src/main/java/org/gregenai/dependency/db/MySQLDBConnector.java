package org.gregenai.dependency.db;

import org.gregenai.model.GreRequest;
import org.gregenai.util.AbstractDataBaseConnector;
import org.gregenai.util.JSONUtil;
import org.gregenai.util.MySQLUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.gregenai.constant.DBConstants.*;
//import static org.gregenai.validators.InputValidator.gson;

public class MySQLDBConnector extends AbstractDataBaseConnector {

    private static Connection connection;

    static {
        try {
            //Load the JDBC
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println(" ********* Driver Loaded !  *********");

            //Creating connection
            System.out.println(" ********* Connecting to MySQL ********* ");
            connection = DriverManager.getConnection(MYSQL_URL, USERNAME, PASSWORD);

            //Validating connection
            if (connection == null) {
                System.err.println("Failed to create MySQL DataBase connection, shutting down the application.");
                spark.Spark.stop();
            }
        } catch (SQLException e) {
            System.err.println("Loading the connection object failed.");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Failed to create connection to MySQL" + e.getMessage());
            e.printStackTrace();
        }

    }
//
//    public static Connection createAndValidateConnection() {
//        try {
//            //Load the JDBC
//            Class.forName("com.mysql.cj.jdbc.Driver");
//            System.out.println(" ********* Driver Loaded !  *********");
//
//            //Creating connection
//            System.out.println(" ********* Connecting to MySQL ********* ");
//            Connection connection = DriverManager.getConnection(MYSQL_URL, USERNAME, PASSWORD);
//
//            //Validating connection
//            if (connection == null) {
//                System.err.println("Failed to create DataBase connection, shutting down the application.");
//                spark.Spark.stop();
//                return null;
//            } else {
//                System.out.println(" ********* Connection made to MySQL ********* ");
//                return connection;
//            }
//        } catch (SQLException e) {
//            System.err.println("Loading the connection object failed.");
//            e.printStackTrace();
//            return null;
//        } catch (Exception e) {
//            System.err.println("Failed to create connection to MySQL" + e.getMessage());
//            e.printStackTrace();
//            return null;
//        }
//    }

//    public Connection checkConnectionNull() {
//        try {
//            Connection conn = createConnection();
//            System.out.println("Connection created to MySQL");
//
//            if (conn == null) {
//                System.err.println("Failed to create DataBase connection, shutting down the application.");
//                spark.Spark.stop();
//                return null;
//            } else {
//                return conn;
//            }
//        } catch (ClassNotFoundException e) {
//            System.err.println("Failed to create connection to MySQL" + e.getMessage());
//            return null;
//        }
//    }

    @Override
    public int createRecords(GreRequest greRequest) {
        try {
            //SQL Query
            String sqlQuery = MySQLUtil.getSQLQuery("insert.definition");
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setString(1, greRequest.getName());
            preparedStatement.setString(2, greRequest.getDefinition());

            int rowsAffected = preparedStatement.executeUpdate();
            System.out.println(rowsAffected + " rows updated");

            return rowsAffected;
        } catch (SQLException e) {
            System.err.println("Failed to execute SQL query");
            throw new RuntimeException(e);
        }
    }

    //Method to display the table
    @Override
    public String readRecords() {
        List<Map<String, String>> rows = new ArrayList<>();
        try {
//            updateViewsCountSqlQuery(connection,);

            //SQl Query
            Statement statement = connection.createStatement();
            // TODO: What if your code does not find select.all in properties file?
            // Hint: FileNotfoundException / ResourceNotFoundException
            ResultSet result = statement.executeQuery(MySQLUtil.getSQLQuery("select.all"));
            System.out.println("Statement : " + statement);

            ResultSetMetaData resultMetaData = result.getMetaData();
            int columnCount = resultMetaData.getColumnCount();

            while (result.next()) {
                Map<String, String> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = resultMetaData.getColumnName(i);
                    String columnValue = result.getString(i);
                    row.put(columnName, columnValue);
                    System.out.print(columnName + " : " + columnValue + " | ");
                }
                rows.add(row);
            }
            System.out.println("Table Found!");
        } catch (SQLException e) {
            System.err.println("Failed to display the SQL table");
        }
        return JSONUtil.generateJsonStringFromObject(rows);

    }

    //Method to display the definition of a given word
    public static Map<String, Object> getGreWordDetailsFromMySQL(GreRequest greRequest) {
        Map<String, Object> resultData = new HashMap<>();
        try {
            //SQL Query to select by name
            String sqlQuery = MySQLUtil.getSQLQuery("select.by.name");
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setString(1, greRequest.getName());

//            updateRecords(greRequest);

            System.out.println("Prepared to execute the select by name SQL query");
            ResultSet result = preparedStatement.executeQuery();

            if (result.next()) {
                ResultSetMetaData resultSetMetaData = result.getMetaData();
                int columnCount = resultSetMetaData.getColumnCount();

                for (int i = 1; i <= columnCount; i++) {
                    String columnName = resultSetMetaData.getColumnName(i);
                    String columnValue = result.getString(i);
                    resultData.put(columnName, columnValue);
                }
            } else {
                System.out.println("Definition not found for " + greRequest.getName() + " ");
                return null;
            }
        } catch (SQLException e) {
            System.err.println("Failed to execute SQL with WHERE clause query");
            throw new RuntimeException(e);
        }
        return resultData;
    }

    @Override
    public int deleteRecords(GreRequest greRequest) {
        try {
            //SQL Query
            String sqlQuery = MySQLUtil.getSQLQuery("delete.by.name");
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setString(1, greRequest.getName());
            System.out.println("Prepared to execute the delete SQL query");
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected == 0) {
                System.out.println("Failed to delete the given word");
            } else {
                System.out.println("Successfully deleted the word");
            }
            return rowsAffected;
        } catch (SQLException e) {
            System.err.println("Failed to execute the delete SQL query");
            throw new RuntimeException(e);
        }
    }

    public static Map<String, Object> getViewsCountSql(GreRequest greRequest) {
        Map<String, Object> resultData = new HashMap<>();
        try {
            //SQL Query
            String salQuery = MySQLUtil.getSQLQuery("select.views.by.name");
            PreparedStatement preparedStatement = connection.prepareStatement(salQuery);
            preparedStatement.setString(1, greRequest.getName());
            System.out.println("Prepared to execute select views SQL query");
            ResultSet result = preparedStatement.executeQuery();

            if (result.next()) {
                ResultSetMetaData resultSetMetaData = result.getMetaData();
                int columnCount = resultSetMetaData.getColumnCount();

                for (int i = 1; i <= columnCount; i++) {
                    String columnName = resultSetMetaData.getColumnName(i);
                    String columnValue = result.getString(i);
                    resultData.put(columnName, columnValue);
                }
            } else {
                System.out.println("Views for " + greRequest.getName() + " do not exist.");
                return null;
            }
        } catch (SQLException e) {
            System.err.println("Failed to execute the select views SQL query");
            throw new RuntimeException(e);
        }
        return resultData;
    }

    @Override
    public void updateRecords(GreRequest greRequest) {
        //SQL Query to update the view count
        String sqlQuery = (greRequest.getName() == null) ? MySQLUtil.getSQLQuery("update.views") : MySQLUtil.getSQLQuery("update.views.by.name");
        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {
            if (greRequest.getName() != null) {
                preparedStatement.setString(1, greRequest.getName());
            }
            int rowsCount = preparedStatement.executeUpdate();
            System.out.println(rowsCount + "row(s) updated");
            System.out.println("Successfully updated the views count!");
        } catch (SQLException e) {
            System.err.println("Failed to update the views count.");
            throw new RuntimeException(e);
        }
    }




}
