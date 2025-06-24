package org.gregenai.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class MySQLUtil {
    private static Properties queries = new Properties();

    static {
        try (InputStream input = MySQLUtil.class.getClassLoader().getResourceAsStream("sql_queries.properties")) {
            if (input == null) {
                throw new RuntimeException("SQL queries not found in resources!");
            }
            queries.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load SQL queries", e);
        }
    }
    public static String getSQLQuery(String key) {
        return queries.getProperty(key);
    }
}
