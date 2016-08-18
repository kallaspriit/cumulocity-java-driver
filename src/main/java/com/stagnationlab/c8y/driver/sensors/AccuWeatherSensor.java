package com.stagnationlab.c8y.driver.sensors;

import c8y.Hardware;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AccuWeatherSensor extends AbstractWeatherSensor {
    private static final Logger log = LoggerFactory.getLogger(RaspberryLightSensor.class);

    private static final String API_URL = "http://dataservice.accuweather.com";
    private static final String LOCATION_ID = "131136";
    private static final String API_KEY = "EWtfbtrF590milfbTaSVbz9yuWk74Wi5";

    public AccuWeatherSensor(String id) {
        super(id);
    }

    @Override
    public void initialize() throws Exception {
        super.initialize();
    }

    @Override
    Hardware getHardware() {
        return new Hardware(
                "AccuWeather Sensor",
                "132354132141513",
                "1.0.0"
        );
    }

    @Override
    WeatherInfo getWeatherInfo() {
        double temperature;

        try {
            temperature = fetchRealTemperature();
        } catch (IOException e) {
            temperature = Math.random() * 20 + 10;

            log.warn("fetching real temperature failed (" + e.getMessage() + "), using random value: " + temperature);

            e.printStackTrace();
        }

        log.info("current temperature: " + temperature);

        return new WeatherInfo(temperature);
    }

    private double fetchRealTemperature() throws IOException {
        String url = API_URL + "/currentconditions/v1/" + LOCATION_ID + "?apikey=" + API_KEY;

        URL obj = new URL(url);

        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", "Mozilla/5.0");

        int responseCode = con.getResponseCode();

        if (responseCode != 200) {
            throw new IOException("Got invalid response code: " + responseCode);
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        StringBuilder response = new StringBuilder();
        String inputLine;

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }

        in.close();

        String responseText = response.toString();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode responseArray = mapper.readTree(responseText);

        return responseArray.get(0).get("Temperature").get("Metric").get("Value").asDouble();
    }

}
