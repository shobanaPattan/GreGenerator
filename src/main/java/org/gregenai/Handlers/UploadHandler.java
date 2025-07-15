package org.gregenai.Handlers;

import org.gregenai.dependency.db.DynamoDBConnector;
import org.gregenai.util.JSONUtil;
import software.amazon.awssdk.services.dynamodb.endpoints.internal.Value;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class UploadHandler {

    public static String handleCSVFileUpload(InputStream inputStream) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            boolean skipHeader = true;

            while ((line = reader.readLine()) != null) {
                if (skipHeader) {
                    skipHeader = false;
                    continue;
                }

                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    String word = parts[0].trim();
                    String definition = parts[1].trim();
                    String example = parts[2].trim();

                    DynamoDBConnector.saveGreWordDetails(word, definition, example);
                }
            }
            return JSONUtil.generateJsonStringFromObject("GRE words uploaded successfully");
        } catch (Exception e) {
            System.err.println("Failed to upload GRE details from CSV file : " + e.getMessage());
            return JSONUtil.generateErrorJsonStringFromObject("Failed to upload GRE words.");
        }
    }
}
