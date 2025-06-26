package com.metricool.mcp.weather.tools;

import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.spec.McpSchema;

import java.io.IOException;
import java.util.List;

import com.metricool.mcp.weather.service.WeatherService;
import com.metricool.mcp.weather.utils.Utils;


public final class GetWeatherForecastByLocationTool {
   
	/**
	 * Get weather forecast for a location
	 * 
     * Tool Arguments:
	 * latitude
	 * longitude
	 *
     * Tool Return:
     * (string): Human readable information.
     * 
     * @return {@link McpServerFeatures.SyncToolSpecification}
	 * @throws IOException if something fails
     */
    public static McpServerFeatures.SyncToolSpecification getWeatherForecastByLocation() throws IOException {
        // Step 1: Load the JSON schema for the tool input arguments.
        final String schema = Utils.readResourceAsString("schema/get-weather-forecast-by-location-json-schema.json");

        // Step 2: Create a tool with name, description, and JSON schema.
        McpSchema.Tool tool = new McpSchema.Tool("get_weather_forecast_by_location", "Get weather forecast for latitude/longitude inputs.", schema);

        // Step 3: Create a tool specification with the tool and the call function.
        return new McpServerFeatures.SyncToolSpecification(tool, (exchange, args) -> {
            final Double latitude = Double.parseDouble(args.get("latitude").toString());
            final Double longitude = Double.parseDouble(args.get("longitude").toString());
            
            WeatherService ws = new WeatherService();

            boolean isError = false;
            String result = "";
           
            
            try {
            	result = ws.getWeatherForecastByLocation(latitude, longitude);
            } catch (Exception e) {
                isError = true;
                result = e + ": " + e.getMessage();
            }

            McpSchema.Content content = new McpSchema.TextContent(result);
            return new McpSchema.CallToolResult(List.of(content), isError);
        });
    }
}

