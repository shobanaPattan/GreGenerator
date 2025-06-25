package org.gregenai.factory;

import org.gregenai.dependency.db.DynamoDBConnector;
import org.gregenai.dependency.db.MySQLDBConnector;
import org.gregenai.dependency.db.AbstractDataBaseConnector;

public class DataBaseConnectorFactory {
    public static AbstractDataBaseConnector getDataBaseConnector(String dbType) {
        if (dbType.equalsIgnoreCase("mysql")) {
            return new MySQLDBConnector();
        } else if (dbType.equalsIgnoreCase("dynamodb")) {
            return new DynamoDBConnector();
        } else {
            // TODO: or maybe default to one of the above if its ok
            throw new IllegalArgumentException("Unsupported database type: " + dbType);
        }
    }
}
