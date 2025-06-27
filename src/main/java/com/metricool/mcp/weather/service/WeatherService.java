/*
* Adapted from https://github.com/spring-projects/spring-ai-examples
* 
* Copyright 2024 - 2024 the original author or authors.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* https://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.metricool.mcp.weather.service;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.metricool.mcp.weather.utils.RestClient;


public class WeatherService {

    private static final String BASE_URL = "https://api.weather.gov";
    private static final String[] HEADERS = new String[] {
        "Accept", "application/geo+json",
        "User-Agent", "WeatherApiClient/1.0 (your@email.com)"
    };

    private final RestClient restClient;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public WeatherService() {
        this.restClient = new RestClient();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Points(@JsonProperty("properties") Props properties) {
        @JsonIgnoreProperties(ignoreUnknown = true)
        public record Props(@JsonProperty("forecast") String forecast) {
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Forecast(@JsonProperty("properties") Props properties) {
        @JsonIgnoreProperties(ignoreUnknown = true)
        public record Props(@JsonProperty("periods") List<Period> periods) {
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public record Period(@JsonProperty("number") Integer number, @JsonProperty("name") String name,
                @JsonProperty("startTime") String startTime, @JsonProperty("endTime") String endTime,
                @JsonProperty("isDaytime") Boolean isDayTime, @JsonProperty("temperature") Integer temperature,
                @JsonProperty("temperatureUnit") String temperatureUnit,
                @JsonProperty("temperatureTrend") String temperatureTrend,
                @JsonProperty("probabilityOfPrecipitation") Map probabilityOfPrecipitation,
                @JsonProperty("windSpeed") String windSpeed, @JsonProperty("windDirection") String windDirection,
                @JsonProperty("icon") String icon, @JsonProperty("shortForecast") String shortForecast,
                @JsonProperty("detailedForecast") String detailedForecast) {
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Alert(@JsonProperty("features") List<Feature> features) {

        @JsonIgnoreProperties(ignoreUnknown = true)
        public record Feature(@JsonProperty("properties") Properties properties) {
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public record Properties(@JsonProperty("event") String event, @JsonProperty("areaDesc") String areaDesc,
                @JsonProperty("severity") String severity, @JsonProperty("description") String description,
                @JsonProperty("instruction") String instruction) {
        }
    }

    /**
     * Get forecast for a specific latitude/longitude
     * 
     * @param latitude  Latitude
     * @param longitude Longitude
     * @return The forecast for the given location
     * @throws InterruptedException 
     * @throws IOException 
     * @throws RestClientException if the request fails
     */
    public String getWeatherForecastByLocation(double latitude, double longitude) throws IOException, InterruptedException {
        String endpoint = String.format(Locale.ENGLISH, "/points/%f,%f", latitude, longitude);
        
        URI uri = URI.create(BASE_URL + endpoint);
        String response = restClient.doGet(uri, HEADERS);

        Points points = objectMapper.readValue(response, Points.class);

        uri = URI.create(points.properties().forecast());
        response = restClient.doGet(uri, HEADERS);

        Forecast forecast = objectMapper.readValue(response, Forecast.class);
        String forecastText = forecast.properties().periods().stream().map(p -> String.format("""
                %s:
                Temperature: %s %s
                Wind: %s %s
                Forecast: %s
                """, 
                p.name(), 
                p.temperature(), p.temperatureUnit(), 
                p.windSpeed(), p.windDirection(),
                p.detailedForecast())
                ).collect(Collectors.joining());

        return forecastText;
    }

    
    /**
     * Get alerts for a specific area
     * 
     * @param state Area code. Two-letter US state code (e.g. CA, NY)
     * @return Human readable alert information
     * @throws InterruptedException 
     * @throws IOException 
     * @throws RestClientException if the request fails
     */
    public String getAlerts(String state) throws IOException, InterruptedException {
        String endpoint = String.format(Locale.ENGLISH, "/alerts/active/area/%s", state);

        URI uri = URI.create(BASE_URL + endpoint);
        String response = restClient.doGet(uri, HEADERS);

        Alert alert = objectMapper.readValue(response, Alert.class);
        String alertText = alert.features().stream().map(f -> String.format("""
                Event: %s
                Area: %s
                Severity: %s
                Description: %s
                Instructions: %s
                """, 
                f.properties().event(), 
                f.properties.areaDesc(), 
                f.properties.severity(),
                f.properties.description(), 
                f.properties.instruction())
                ).collect(Collectors.joining("\n"));
        
        return alertText;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        WeatherService client = new WeatherService();
        System.out.println(client.getWeatherForecastByLocation(47.6062, -122.3321));
        System.out.println(client.getAlerts("NY"));
    }

}
