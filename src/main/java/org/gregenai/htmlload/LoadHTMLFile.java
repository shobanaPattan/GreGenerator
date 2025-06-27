package org.gregenai.htmlload;

import org.gregenai.api.GREWordTrainerAPI;

import java.io.IOException;
import java.io.InputStream;

public class LoadHTMLFile {

    public static String loadHtmlFile(String fileName) {
        try (InputStream inputStream = GREWordTrainerAPI.class.getResourceAsStream("/public/" + fileName)) {
            if (inputStream == null) {
                return "File not found";
            } else {
                return new String(inputStream.readAllBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Error reading file";
        }
    }
}
