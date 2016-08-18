package com.stagnationlab.c8y.driver.sensors;

import c8y.Hardware;
import c8y.lx.driver.MeasurementPollingDriver;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.stagnationlab.c8y.driver.DeviceManager;
import com.stagnationlab.c8y.driver.measurements.WeatherMeasurement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class AbstractWeatherSensor extends MeasurementPollingDriver {

    class WeatherSensor {}

    class WeatherInfo {
        public double temperature;

        public WeatherInfo(double temperature) {
            this.temperature = temperature;
        }
    }

    private static final Logger log = LoggerFactory.getLogger(SimulatedLightSensor.class);

    private static final String TYPE = "Weather";

    private final String id;

    AbstractWeatherSensor(String id) {
        // super("c8y_" + TYPE + "Sensor", "c8y." + TYPE.toLowerCase(), 10 * 60 * 1000);
        super("c8y_" + TYPE + "Sensor", "c8y." + TYPE.toLowerCase(), 60000);

        this.id = id;
    }

    @Override
    public void initialize() throws Exception {
        log.info("initializing");
    }

    @Override
    public void discoverChildren(ManagedObjectRepresentation parent) {
        log.info("creating child");

        ManagedObjectRepresentation childDevice = DeviceManager.createChild(
                id,
                TYPE,
                getPlatform(),
                parent,
                getHardware(),
                getSupportedOperations(),
                new WeatherSensor()
        );

        setSource(childDevice);
    }

    @Override
    public void run() {
        WeatherInfo weatherInfo = getWeatherInfo();

        WeatherMeasurement weatherMeasurement = new WeatherMeasurement();
        weatherMeasurement.update(weatherInfo.temperature);

        sendMeasurement(weatherMeasurement);
    }

    abstract Hardware getHardware();

    abstract WeatherInfo getWeatherInfo();

}
