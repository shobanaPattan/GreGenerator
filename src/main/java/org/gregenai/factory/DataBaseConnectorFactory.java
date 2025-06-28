package org.gregenai.factory;

import org.gregenai.dependency.db.DynamoDBConnector;
import org.gregenai.dependency.db.MySQLDBConnector;
import org.gregenai.dependency.db.AbstractDataBaseConnector;

public class DataBaseConnectorFactory {
    public static AbstractDataBaseConnector getDataBaseConnector(String dbType) {
        System.out.println("Executing DB connector");
        System.out.println("DataBase Type : " + dbType);
        if (dbType.trim().equalsIgnoreCase("mysql")) {
            return new MySQLDBConnector();
        } else if (dbType.trim().equalsIgnoreCase("dynamodb")) {
            return new DynamoDBConnector();
        } else {
            System.out.println("DataType is empty or miss spelled : " + dbType + ", so returning MySQL DB as default.");
            return new MySQLDBConnector();
//            throw new IllegalArgumentException("Unsupported database type: " + dbType);
        }
    }
}
