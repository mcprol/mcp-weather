package com.metricool.mcp.weather;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.time.Duration;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.ServerParameters;
import io.modelcontextprotocol.client.transport.StdioClientTransport;
import io.modelcontextprotocol.spec.McpSchema.CallToolRequest;
import io.modelcontextprotocol.spec.McpSchema.CallToolResult;
import io.modelcontextprotocol.spec.McpSchema.ClientCapabilities;
import io.modelcontextprotocol.spec.McpSchema.InitializeResult;
import io.modelcontextprotocol.spec.McpSchema.ListToolsResult;


/**
 * A simple MCP client for weather server.
 * Only for test/learning purposes
 * 
 * https://github.com/modelcontextprotocol/modelcontextprotocol/blob/main/docs/sdk/java/mcp-client.mdx
 */
public class McpStdioClientWeatherTest {

    // IMPORTANT: Replace with the actual path to your MCP Java Server JAR
    static final String JAVA = "/Library/Java/JavaVirtualMachines/temurin-17.jdk/Contents/Home/bin/java";
    static final String MCPSERVERJARNAME = "target/mcp-weather-server.jar";
    
    final static Logger logger = LoggerFactory.getLogger(McpStdioClientWeatherTest.class);

    static File serverJar;
    static StdioClientTransport transport;
    static McpSyncClient mcpClient = null;
    
    private static void loadJarFile() throws Exception {
        // Ensure the JAR file exists
        serverJar = new File(MCPSERVERJARNAME);
        if (!serverJar.exists()) {
            throw new Exception("Error: Please build your MCP server and provide the correct path. MCP Server JAR not found at " + MCPSERVERJARNAME);
        }
    }

    
    /**
     * Configure the STDIO transport
     * This tells the client how to start and communicate with the server process.
     */
    private static void buildStdioTransport() {
        ServerParameters serverParameters = ServerParameters.builder(JAVA)
                .arg("-jar")
                .arg(MCPSERVERJARNAME)
                .build();
        
        transport = new StdioClientTransport(serverParameters);
    }
    
    
    /**
     * Build the synchronous MCP client
     */
    private static void buildClient() {
        ClientCapabilities capabilities = ClientCapabilities.builder()
                .roots(true)
                .sampling()
                .build();

        mcpClient = McpClient.sync(transport)
                .requestTimeout(Duration.ofSeconds(10))
                .capabilities(capabilities)
                .build();
    }

    /**
     * Send an 'initialize' request
     */
    private static void sendInitialize() {
        InitializeResult result = mcpClient.initialize();
        
        logger.info("Connected to MCP server: '{}'", result);
    }
    
    
    @BeforeAll
    static void setUpSuite() throws Exception {
        loadJarFile();
        buildStdioTransport();
        buildClient();
        sendInitialize();
    }
    
    @AfterAll
    static void tearDownSuite() {
        if (mcpClient != null) {
            mcpClient.closeGracefully();
        }
    }
    
    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }
    
    
    /**
     * Connection test
     */
    @Test
    public void shouldConnectAndInitialize() {
        // we already are calling to the initialize in the setUp method.
        assertTrue(true);
    }
    

    /**
     * List available tools
     */    
    @Test
    public void getTools() {
        ListToolsResult tools = mcpClient.listTools();
        logger.info("Retrieved tools: '{}'", tools);
        assertTrue(true);
    } 
    
    
    /**
     * get New York weather alerts
     */    
    @Test
    public void getNewYorkAlerts() {
        CallToolRequest request = new CallToolRequest("get_alerts",
                Map.of("code", "NY")
        );
        
        CallToolResult result = mcpClient.callTool(request);
        logger.info("New York alerts: '{}'", result);
        assertTrue(true);
    }     
    
    
    /**
     * get weather forecast
     */    
    @Test
    public void getWeatherForecastByLocation() {
        CallToolRequest request = new CallToolRequest("get_weather_forecast_by_location",
                Map.of("latitude", 47.6062, "longitude", -122.3321)
        );
        
        CallToolResult result = mcpClient.callTool(request);
        logger.info("Weather Forecast: '{}'", result);
        assertTrue(true);
    } 
    

}


