package com.stagnationlab.c8y.driver.sensors;

import c8y.Hardware;
import c8y.lx.driver.DeviceManagedObject;
import c8y.lx.driver.Driver;
import c8y.lx.driver.OperationExecutor;
import com.cumulocity.model.ID;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.Platform;
import com.cumulocity.sdk.client.event.EventApi;
import com.stagnationlab.c8y.driver.DeviceManager;
import com.stagnationlab.c8y.driver.models.MotionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimulatedMotionSensor implements Driver {

    private static Logger log = LoggerFactory.getLogger(SimulatedMotionSensor.class);

    private MotionEvent motionEvent = new MotionEvent();
    private Platform platform;
    private EventApi eventApi;
    private static final String type = "Motion";
    private String id;

    public SimulatedMotionSensor(String id) {
        this.id = id;
    }

    @Override
    public void initialize() throws Exception {
        log.info("initializing");
    }

    @Override
    public void initialize(Platform platform) throws Exception {
        log.info("initializing platform");

        this.platform = platform;
        this.eventApi = platform.getEventApi();
    }

    @Override
    public void discoverChildren(ManagedObjectRepresentation parent) {
        log.info("creating child");

        ManagedObjectRepresentation childDevice = DeviceManager.createChild(
                id,
                type,
                platform,
                parent,
                new Hardware(
                        "Simulated Motion Sensor",
                        "927819335679844",
                        "1.0.0"
                ),
                getSupportedOperations()
        );

        motionEvent.setSource(childDevice);
    }

    @Override
    public OperationExecutor[] getSupportedOperations() {
        return new OperationExecutor[0];
    }

    @Override
    public void initializeInventory(ManagedObjectRepresentation parent) {

    }

    @Override
    public void start() {

    }
}
