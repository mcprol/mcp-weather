package com.metricool.mcp.weather.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.metricool.mcp.weather.tools.McpTools;

import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.server.transport.HttpServletSseServerTransportProvider;
import io.modelcontextprotocol.spec.McpSchema;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.servlet.ServletHolder;
import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class McpSseServer {

    private static final Logger logger = LoggerFactory.getLogger(McpSseServer.class);

    public static final String MCP_SERVER_BASE_URI = "http://localhost:8080";
    public static final String MCP_SERVER_MSG_ENDPOINT = "/mcp/message";
    public static final String MCP_SERVER_SSE_ENDPOINT = "/sse";

    private McpSyncServer server;
    private static final ObjectMapper JSON = new ObjectMapper();

    /**
     * Initialize the HTTP SSE MCP server.
     */
    private void initialize() {
        McpSchema.ServerCapabilities serverCapabilities = McpSchema.ServerCapabilities.builder()
            .tools(true)
            .prompts(true)
            .resources(true, true)
            .build();

        HttpServletSseServerTransportProvider transport = new HttpServletSseServerTransportProvider(
            JSON, MCP_SERVER_MSG_ENDPOINT, MCP_SERVER_SSE_ENDPOINT
        );
        server = McpServer.sync(transport)
            .serverInfo(ServerInfo.SERVER_NAME, ServerInfo.SERVER_VERSION)
            .capabilities(serverCapabilities)
            .build();

        // Add resources, prompts, and tools to the MCP server
        //McpResources.addAllTo(server);
        //McpPrompts.addAllTo(server);
        McpTools.addAllTo(server);

        // Start the HTTP server
        startHttpServer(transport);
    }

    /**
     * Start the HTTP server with Jetty.
     */
    private void startHttpServer(HttpServletSseServerTransportProvider transport) {
        ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        servletContextHandler.setContextPath("/");

        ServletHolder servletHolder = new ServletHolder(transport);
        servletContextHandler.addServlet(servletHolder, "/*");

        Server httpserver = new Server(8080);
        httpserver.setHandler(servletContextHandler);

        try {
            httpserver.start();
            logger.info("Jetty-based HTTP server started on http://127.0.0.1:8080");

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    logger.info("Shutting down HTTP server");
                    httpserver.stop();
                    server.close();
                } catch (Exception e) {
                    logger.error("Error stopping HTTP server", e);
                }
            }));

            // Wait for the HTTP server to stop
            httpserver.join();
        } catch (Exception e) {
            logger.error("Error starting HTTP server on http://127.0.0.1:8080", e);
            server.close();
        }
    }

    /**
     * Main entry point for the HTTP SSE MCP server.
     */
    public static void main(String[] args) {
        // Initialize MCP server
        McpSseServer mcpSseServer = new McpSseServer();
        mcpSseServer.initialize();
    }

}
