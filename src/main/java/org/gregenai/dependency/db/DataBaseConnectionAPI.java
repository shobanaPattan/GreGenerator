package org.gregenai.dependency.db;
import org.gregenai.util.SQLQueryLoader;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.gregenai.constant.DBConstants.*;

public class DataBaseConnectionAPI {

    public static Connection createConnection() throws ClassNotFoundException {
        try {
            //Load the JDBC
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println(" ********* Driver Loaded !  *********");

            //Creating connection
            System.out.println(" ********* Connecting to MySQL ********* ");
            return DriverManager.getConnection(MYSQL_URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            System.err.println("Loading the connection object failed.");
            return null;
        }
    }

    public static int executeInsertSQLQuery(Connection connection, String name, String definition) {
        try {
            //SQL Query
            String sqlQuery = SQLQueryLoader.get("insert.definition");
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, definition);

            int rowsAffected = preparedStatement.executeUpdate();
            System.out.println(rowsAffected + " rows updated");

            return rowsAffected;
        } catch (SQLException e) {
            System.err.println("Failed to execute SQL query");
            throw new RuntimeException(e);
        }
    }

    //Method to display the table
    public static List<Map<String, String>> executeSelectAllSQLQuery(Connection connection) {
        List<Map<String, String>> rows = new ArrayList<>();
        try {
            updateViewsCountSqlQuery(connection, null);

            //SQl Query
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(SQLQueryLoader.get("select.all"));
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
//                System.out.println();
            }
            System.out.println("Table Found!");
        } catch (SQLException e) {
            System.err.println("Failed to display the SQL table");
        }
        return rows;
    }

    //Method to display the definition of a given word
    public static Map<String, Object> executeSelectByNameSQLQuery(Connection connection, String name) {
        Map<String, Object> resultData = new HashMap<>();
        try {
            //SQL Query to select by name
            String sqlQuery = SQLQueryLoader.get("select.by.name");
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setString(1, name);

            updateViewsCountSqlQuery(connection, name);

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
                System.out.println("Definition not found for " + name + " ");
                return null;
            }
        } catch (SQLException e) {
            System.err.println("Failed to execute SQL with WHERE clause query");
            throw new RuntimeException(e);
        }
        return resultData;
    }

    public static int executeDeleteSQLQuery(Connection connection, String name) {
        try {
            //SQL Query
            String sqlQuery = SQLQueryLoader.get("delete.by.name");
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setString(1, name);
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

    public static Map<String, Object> executeGetViewsSqlQuery(Connection connection, String name) {
        Map<String, Object> resultData = new HashMap<>();
        try {
            //SQL Query
            String salQuery = SQLQueryLoader.get("select.views.by.name");
            PreparedStatement preparedStatement = connection.prepareStatement(salQuery);
            preparedStatement.setString(1, name);
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
                System.out.println("Views for " + name + " do not exist.");
                return null;
            }
        } catch (SQLException e) {
            System.err.println("Failed to execute the select views SQL query");
            throw new RuntimeException(e);
        }
        return resultData;
    }

    public static void updateViewsCountSqlQuery(Connection connection, String name) {
        //SQL Query to update the view count
        String sqlQuery = (name == null) ? SQLQueryLoader.get("update.views") : SQLQueryLoader.get("update.views.by.name");
        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {
            if (name != null) {
                preparedStatement.setString(1, name);
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
