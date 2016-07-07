package com.stagnationlab.c8y.driver.sensors;

import c8y.Hardware;
import c8y.LightMeasurement;
import c8y.LightSensor;
import c8y.lx.driver.DeviceManagedObject;
import c8y.lx.driver.MeasurementPollingDriver;
import com.cumulocity.model.ID;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.stagnationlab.c8y.driver.ChildDeviceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

public class SimulatedLightSensor extends MeasurementPollingDriver {

    private static Logger log = LoggerFactory.getLogger(SimulatedLightSensor.class);

    private static final String type = "Light";
    private LightMeasurement lightMeasurement = new LightMeasurement();
    private static final double MEASUREMENT_RANGE = 10.0;
    private double illuminance = MEASUREMENT_RANGE / 2.0;
    private String id;

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

        ManagedObjectRepresentation child = ChildDeviceFactory.createChild(
                id,
                type,
                new LightSensor(),
                new Hardware(
                        "Simulated Light Sensor",
                        "098245687332343",
                        "1.0.0"
                )
        );

        setSource(child);

        DeviceManagedObject deviceManagedObject = new DeviceManagedObject(getPlatform());
        ID externalId = ChildDeviceFactory.buildExternalId(parent, child, id);

        deviceManagedObject.createOrUpdate(child, externalId, parent.getId());
    }

    @Override
    public void run() {
        double illuminance = getIlluminance();

        lightMeasurement.setIlluminance(new BigDecimal(illuminance));

        super.sendMeasurement(lightMeasurement);

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
