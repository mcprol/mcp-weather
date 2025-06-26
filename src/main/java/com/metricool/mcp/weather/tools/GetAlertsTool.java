package com.metricool.mcp.weather.tools;

import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.spec.McpSchema;

import java.io.IOException;
import java.util.List;

import com.metricool.mcp.weather.service.WeatherService;
import com.metricool.mcp.weather.utils.Utils;


public final class GetAlertsTool {
   
	/**
	 * Get alerts for a specific area
	 * 
     * Tool Arguments:
	 * code state Area code. Two-letter US state code (e.g. CA, NY)
	 *
     * Tool Return:
     * (string): Human readable alert information.
     * 
     * @return {@link McpServerFeatures.SyncToolSpecification}
	 * @throws IOException if something fails
     */
    public static McpServerFeatures.SyncToolSpecification getAlerts() throws IOException {
        // Step 1: Load the JSON schema for the tool input arguments.
        final String schema = Utils.readResourceAsString("schema/get-alerts-json-schema.json");

        // Step 2: Create a tool with name, description, and JSON schema.
        McpSchema.Tool tool = new McpSchema.Tool("get_alerts", "Get weather alerts for a specific area.", schema);

        // Step 3: Create a tool specification with the tool and the call function.
        return new McpServerFeatures.SyncToolSpecification(tool, (exchange, args) -> {
            final String code = args.get("code").toString();
            
            WeatherService ws = new WeatherService();

            boolean isError = false;
            String result = "";
           
            
            try {
            	result = ws.getAlerts(code);
            } catch (Exception e) {
                isError = true;
                result = e + ": " + e.getMessage();
            }

            McpSchema.Content content = new McpSchema.TextContent(result);
            return new McpSchema.CallToolResult(List.of(content), isError);
        });
    }
}

