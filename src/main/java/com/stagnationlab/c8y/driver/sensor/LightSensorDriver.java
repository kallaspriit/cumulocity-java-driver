package com.stagnationlab.c8y.driver.sensor;

import c8y.Hardware;
import c8y.LightMeasurement;
import c8y.LightSensor;
import c8y.lx.driver.DeviceManagedObject;
import c8y.lx.driver.MeasurementPollingDriver;
import com.cumulocity.model.ID;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

public class LightSensorDriver extends MeasurementPollingDriver {

    private static Logger log = LoggerFactory.getLogger(LightSensorDriver.class);

    private static final String type = "Light";
    private LightMeasurement lightMeasurement = new LightMeasurement();
    private ManagedObjectRepresentation managedObjectRepresentation = new ManagedObjectRepresentation();
    private static final double MEASUREMENT_RANGE = 10.0;
    private double illuminance = MEASUREMENT_RANGE / 2.0;
    private String id;

    public LightSensorDriver(String id) {
        super("c8y_" + type + "Sensor", "c8y." + type.toLowerCase(), 5000);

        log.info("creating light sensor driver");

        this.id = id;
        this.managedObjectRepresentation.set(new LightSensor());
    }

    @Override
    public void discoverChildren(ManagedObjectRepresentation parent) {
        log.info("discovering children");

        managedObjectRepresentation.set(getHardware());
        managedObjectRepresentation.setType("c8y_Test_" + type);
        managedObjectRepresentation.setName(parent.getName() + " " + type + " " + id);

        setSource(managedObjectRepresentation);

        DeviceManagedObject deviceManagedObject = new DeviceManagedObject(getPlatform());

        ID extId = new ID("test-" + parent.get(Hardware.class).getSerialNumber() + "-" + id);

        deviceManagedObject.createOrUpdate(managedObjectRepresentation, extId, parent.getId());
    }

    @Override
    public void initialize() throws Exception {
        log.info("initializing");
    }

    @Override
    public void run() {
        double illuminance = getIlluminance();

        lightMeasurement.setIlluminance(new BigDecimal(illuminance));

        super.sendMeasurement(lightMeasurement);

        log.info("sending light illuminance measurement: " + illuminance);
    }

    private Hardware getHardware() {
        log.info("providing hardware info");

        return new Hardware(
                "Simulated Light Sensor",
                "12345678",
                "1.0.0"
        );
    }

    private double getIlluminance() {
        // simulate gradual illuminance change
        double step = 2.0;
        double randomChange = Math.random() * step - step / 2.0;
        illuminance = Math.min(Math.max(illuminance + randomChange, 0.0), 10.0);

        return illuminance;
    }
}
