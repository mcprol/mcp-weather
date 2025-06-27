package com.metricool.mcp.weather.utils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.metricool.mcp.weather.server.McpSseServer;

public class RestClient {

    private static final Logger logger = LoggerFactory.getLogger(McpSseServer.class);
    
    private static final HttpClient httpClient  = HttpClient.newBuilder()
            .build();;
    
            
    public RestClient() {
    }
    
    
    public String doGet(URI uri, String... headers) throws IOException, InterruptedException {
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .headers(headers)
                .GET()
                .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        log("Status Code: '{}'", response.statusCode());
        log("Response Body: '{}'", response.body());
        
        return response.body();
    }

    
    public String doPost(URI uri, String body, String... headers) throws IOException, InterruptedException {
        
        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(uri)
                .headers(headers)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = httpClient.send(postRequest, HttpResponse.BodyHandlers.ofString());

        log("Status Code: '{}'", response.statusCode());
        log("Response Body: '{}'", response.body());

        return response.body();
    }
    
    
    private void log(String msg, Object... objects) {
        //logger.info(msg, objects);
    }

}