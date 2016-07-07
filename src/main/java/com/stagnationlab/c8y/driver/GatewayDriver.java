package com.stagnationlab.c8y.driver;

import c8y.lx.driver.Driver;
import c8y.lx.driver.OperationExecutor;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.model.operation.OperationStatus;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.sdk.client.Platform;
import com.pi4j.io.gpio.RaspiPin;
import com.stagnationlab.c8y.driver.actuators.RaspberryRelayActuator;
import com.stagnationlab.c8y.driver.actuators.SimulatedRelayActuator;
import com.stagnationlab.c8y.driver.sensors.SimulatedLightSensor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class GatewayDriver implements Driver, OperationExecutor {
    private static Logger log = LoggerFactory.getLogger(GatewayDriver.class);

    private List<Driver> drivers = new ArrayList<>();
    private GId gid;


    @Override
    public void initialize() throws Exception {
        log.info("initializing");

        setupSensors();
        setupActuators();

        try {
            initializeDrivers();
        } catch (Exception e) {
            log.warn("initializing drivers failed");
        }
    }

    @Override
    public void initialize(Platform platform) throws Exception {
        log.info("initializing platform");

        try {
            initializeDrivers(platform);
        } catch (Exception e) {
            log.warn("initializing drivers platform failed");
        }
    }

    @Override
    public void initializeInventory(ManagedObjectRepresentation managedObjectRepresentation) {
        log.info("initializing inventory");
    }

    @Override
    public void discoverChildren(ManagedObjectRepresentation managedObjectRepresentation) {
        log.info("discovering children");

        this.gid = managedObjectRepresentation.getId();

        for (Driver driver : drivers) {
            driver.discoverChildren(managedObjectRepresentation);
        }
    }

    @Override
    public void start() {
        log.info("starting driver");

        for (Driver driver : drivers) {
            driver.start();
        }
    }

    @Override
    public String supportedOperationType() {
        log.info("supported operation type requested");

        return "c8y_Restart";
    }

    @Override
    public void execute(OperationRepresentation operation, boolean cleanup) throws Exception {
        log.info("execution requested (cleanup: " + (cleanup ? "yes" : "no") + ")");

        if (!this.gid.equals(operation.getDeviceId())) {
            // Silently ignore the operation if it is not targeted to us, another driver will (hopefully) care.
            return;
        }

        if (cleanup) {
            operation.setStatus(OperationStatus.SUCCESSFUL.toString());
        } else {
            log.info("shutting down");

            new ProcessBuilder(new String[]{"shutdown", "-r"}).start().waitFor();
        }
    }

    @Override
    public OperationExecutor[] getSupportedOperations() {
        log.info("supported operations requested");

        List<OperationExecutor> operationExecutorsList = new ArrayList<>();

        operationExecutorsList.add(this);

        for (Driver driver : drivers) {
            for (OperationExecutor driverOperationExecutor : driver.getSupportedOperations()) {
                operationExecutorsList.add(driverOperationExecutor);
            }
        }

        return operationExecutorsList.toArray(new OperationExecutor[operationExecutorsList.size()]);
    }

    private void setupSensors() {
        log.info("setting up sensors");

        setupSimulatedLightSensor();
    }

    private void setupSimulatedLightSensor() {
        log.info("setting up light sensor");

        SimulatedLightSensor simulatedLightSensor = new SimulatedLightSensor("1");

        drivers.add(simulatedLightSensor);
    }

    private void setupActuators() {
        log.info("setting up actuators");

        setupSimulatedRelayActuator();
        setupRaspberryRelayActuator();
    }

    private void setupSimulatedRelayActuator() {
        log.info("setting up simulated relay actuator");

        SimulatedRelayActuator simulatedRelayActuator = new SimulatedRelayActuator("1");

        drivers.add(simulatedRelayActuator);
    }

    private void setupRaspberryRelayActuator() {
        log.info("setting up raspberry relay actuator");

        RaspberryRelayActuator raspberryRelayActuator = new RaspberryRelayActuator("2", RaspiPin.GPIO_09);

        drivers.add(raspberryRelayActuator);
    }

    private void initializeDrivers() {
        log.info("initializing drivers");

        Iterator<Driver> iterator = drivers.iterator();

        while (iterator.hasNext()) {
            Driver driver = iterator.next();

            try {
                driver.initialize();
            } catch (Exception e) {
                log.warn("initializing driver failed (" + e.getMessage() + "), skipping the driver " + driver.getClass().getName());

                iterator.remove();
            }
        }
    }

    private void initializeDrivers(Platform platform) {
        log.info("initializing drivers with platform");

        Iterator<Driver> iterator = drivers.iterator();

        while (iterator.hasNext()) {
            Driver driver = iterator.next();

            try {
                driver.initialize(platform);
            } catch (Exception e) {
                log.warn("initializing driver platform failed (" + e.getMessage() + "), skipping the driver " + driver.getClass().getName());

                iterator.remove();
            }
        }
    }
}
