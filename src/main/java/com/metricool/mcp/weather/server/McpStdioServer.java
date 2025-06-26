package com.metricool.mcp.weather.server;

import com.metricool.mcp.weather.tools.McpTools;

import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.server.transport.StdioServerTransportProvider;
import io.modelcontextprotocol.spec.McpSchema;

/**
 * Java Server implementing Model Context Protocol (MCP)
 *
 */
public class McpStdioServer {
    /**
     * The MCP sync server instance.
     */
    private McpSyncServer server;

    
    /**
     * Initialize the STDIO MCP server.
     */
    private void initialize() {
        McpSchema.ServerCapabilities serverCapabilities = McpSchema.ServerCapabilities.builder()
            .tools(true)
            .prompts(true)
            .resources(true, true)
            .build();

        server = McpServer.sync(new StdioServerTransportProvider())
            .serverInfo(ServerInfo.SERVER_NAME, ServerInfo.SERVER_VERSION)
            .capabilities(serverCapabilities)
            .build();

        // We are in STDIO mode, so logging is unavailable and messages are output to STDERR only
        System.err.println(ServerInfo.SERVER_NAME + " " + ServerInfo.SERVER_VERSION + " initialized in STDIO mode");
    }

    
    /**
     * Main entry point for the STDIO MCP server.
     */
    public static void main(String[] args) {
        // Initialize MCP server
        McpStdioServer mcpStdioServer = new McpStdioServer();
        mcpStdioServer.initialize();
        
        // Add resources, prompts, and tools to the MCP server
        //McpResources.addAllTo(mcpStdioServer.server);
        //McpPrompts.addAllTo(mcpStdioServer.server);
        McpTools.addAllTo(mcpStdioServer.server);
    }

}
