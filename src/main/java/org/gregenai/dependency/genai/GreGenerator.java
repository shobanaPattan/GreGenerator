package org.gregenai.dependency.genai;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class GreGenerator {

    public static void main(String[] args) {

        System.out.println(chatGPT("who are you?"));
    }

    public static String chatGPT(String message) {
        final Logger logger = LoggerFactory.getLogger(GreGeneratorAPI.class);
        final String openAi_API_Key = "";
        final String openAi_Url = "https://api.openai.com/v1/chat/completions";
        final String model = "gpt-3.5-turbo";

        try {
            URL obj = new URL(openAi_Url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Authorization","Bearer " + openAi_API_Key);
            con.setRequestProperty("Content-Type", "application/json");

            //Build the request body
            String body = "{\"model\" : \"" + model + "\", \"messages\" : [{\"role\": \"user\", \"content\": \"" + message + "\"}]}";
            con.setDoOutput(true);
            OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
            writer.write(body);
            writer.flush();
            writer.close();

            //Get response back
            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            if ((inputLine = reader.readLine()) != null) {
                response.append(inputLine);
            }
            reader.close();

            return (response.toString().split("\"content\":\"")[1].split("\"")[0]).substring(4);
//            return extractContentFromResponse(response.toString());
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static String extractContentFromResponse(String response) {
        int startMarker = response.indexOf("content") + 11; // Marker for where the content starts.
        int endMarker = response.indexOf("\"", startMarker); // Marker for where the content ends.
        return response.substring(startMarker, endMarker); // Returns the substring containing only the response.
    }
}

