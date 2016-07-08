package com.stagnationlab.c8y.driver.sensors;

import c8y.Hardware;
import c8y.LightMeasurement;
import c8y.LightSensor;
import c8y.lx.driver.MeasurementPollingDriver;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.stagnationlab.c8y.driver.DeviceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

public class SimulatedLightSensor extends MeasurementPollingDriver {

    private static Logger log = LoggerFactory.getLogger(SimulatedLightSensor.class);

    private static final String type = "Light";
    private String id;

    private static final double MEASUREMENT_RANGE = 10.0;
    private double illuminance = MEASUREMENT_RANGE / 2.0;

    public SimulatedLightSensor(String id) {
        super("c8y_" + type + "Sensor", "c8y." + type.toLowerCase(), 5000);

        log.info("creating light sensor driver");

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
                type,
                getPlatform(),
                parent,
                new Hardware(
                        "Simulated Light Sensor",
                        "098245687332343",
                        "1.0.0"
                ),
                getSupportedOperations(),
                new LightSensor()
        );

        setSource(childDevice);
    }

    @Override
    public void run() {
        double illuminance = getIlluminance();

        LightMeasurement lightMeasurement = new LightMeasurement();
        lightMeasurement.setIlluminance(new BigDecimal(illuminance));

        sendMeasurement(lightMeasurement);

        log.info("sending light illuminance measurement: " + illuminance);
    }

    private double getIlluminance() {
        // simulate gradual illuminance change
        double step = 2.0;
        double randomChange = Math.random() * step - step / 2.0;
        illuminance = Math.min(Math.max(illuminance + randomChange, 0.0), 10.0);

        return illuminance;
    }
}
