package org.gregenai.dependency.genai;

import com.google.gson.Gson;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;


import static spark.Spark.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GreGeneratorAPI {

    private static final Logger logger = LoggerFactory.getLogger(GreGeneratorAPI.class);
    private static final String openAi_API_Key = "";
    private static final String openAi_Url = "https://api.openai.com/v1/chat/completions";

    public static void main(String[] args) {
        System.out.println("Loading API...");
        logger.info("Server starting...");
        logger.debug("Debugging GreGeneratorAPI initialization.");

        port(4567);

        get("/word", (request, response) -> {
            String prompt = "Give me an uncommon or GRE english word along with its definition.";
            String chatGPTResponse;
            try{
                chatGPTResponse = askChatGPT(prompt);
            }catch (Exception e){
                e.printStackTrace();
                logger.info("Error calling OpenAI....");
                response.status(500);
                return new Gson().toJson(Map.of("error", "Interval server error"));
            }

            response.type("application/json");
            Map<String, String> result = new HashMap<>();
            result.put("WordInfo", chatGPTResponse);
            return new Gson().toJson(result);
        });
           System.out.println("API loaded...");
    }

    public static String askChatGPT(String prompt) throws Exception {
        CloseableHttpClient client = HttpClients.createDefault(); //Creating CloseableHttpClient object
        HttpPost httpPost = new HttpPost(openAi_Url); //Creates new POST request object

        //Creating headers
        httpPost.setHeader("Authorization", "Bearer " + openAi_API_Key.trim()); //Authorization and Bearer used to sends API key to servers saying your allowed to call the API and to prove their identity
        httpPost.setHeader("Content-Type", "Application/JSON"); //Content type defines what type of data your sending to server

        //Creating Map object that what users says to AI
        Map<String, Object> message = new HashMap<>();
        message.put("role", "User");
        message.put("content", prompt);

        //Creating Map object PlayLoad to send into POST request body
        Map<String, Object> payLoad = new HashMap<>(); //Object is used because it lets you store any type of data(string,numbers,arrays)
        payLoad.put("model", "gpt-3.5-turbo");
        payLoad.put("messages", new Object[]{message});
        payLoad.put("temperature", 0.7);

        //Creating string entity
        StringEntity entity = new StringEntity(new Gson().toJson(payLoad), ContentType.APPLICATION_JSON); //Gson() library - converts java(map) into Json string format where StringEntity is a class which converts json string into object.
        httpPost.setEntity(entity); //Sets the object (Json playLoad) as the body of POST request body

        try (CloseableHttpResponse res = client.execute(httpPost)) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(res.getEntity().getContent()));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            System.out.println("Raw response from OpenAI:");
            System.out.println(result.toString());

            Map<String, Object> json = new Gson().fromJson(result.toString(), Map.class); //Converting result json into map
            Object choicesObj = json.get("choices"); //Creating object for each choice
            List<Object> choices = (List<Object>) choicesObj; //Creating list of objects for all choices
            Map<String, Object> firstChoice = (Map<String, Object>) choices.get(0); //Extracting first choice from a list of objects
            Map<String, String> messageObj = (Map<String, String>) firstChoice.get("message"); //Extracting message from choice
            return messageObj.get("content");
        }

    }
}
