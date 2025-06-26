package com.metricool.mcp.weather.tools;

import io.modelcontextprotocol.server.McpSyncServer;
import java.io.IOException;


/**
 * A class that defines tools for the MCP server.
 *
 */
public final class McpTools {
   
    /**
     * Add all tools to the MCP server.
     *
     * @param server The MCP server to add tools to.
     */
    public static void addAllTo(McpSyncServer server) {
        try {
            server.addTool(GetAlertsTool.getAlerts());
            server.addTool(GetWeatherForecastByLocationTool.getWeatherForecastByLocation());
        } catch (IOException e) {
            // We are in STDIO mode, so logging is unavailable and messages are output to STDERR only
            System.err.println("Error adding tools");
            e.printStackTrace(System.err);
        }
    }

}

