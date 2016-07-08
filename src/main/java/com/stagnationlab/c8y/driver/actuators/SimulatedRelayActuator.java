package com.stagnationlab.c8y.driver.actuators;

import c8y.Hardware;
import c8y.Relay;
import c8y.lx.driver.Driver;
import c8y.lx.driver.OperationExecutor;
import com.cumulocity.model.operation.OperationStatus;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.measurement.MeasurementRepresentation;
import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.sdk.client.Platform;
import com.cumulocity.sdk.client.measurement.MeasurementApi;
import com.stagnationlab.c8y.driver.DeviceManager;
import com.stagnationlab.c8y.driver.models.RelayStateMeasurement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class SimulatedRelayActuator implements Driver, OperationExecutor {

    private static Logger log = LoggerFactory.getLogger(SimulatedRelayActuator.class);

    private static final String type = "Relay";
    private Relay relay = new Relay();
    private Platform platform;
    private MeasurementApi measurementApi;
    private ManagedObjectRepresentation childDevice;
    private String id;

    public SimulatedRelayActuator(String id) {
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
        this.measurementApi = platform.getMeasurementApi();
    }

    @Override
    public void discoverChildren(ManagedObjectRepresentation parent) {
        log.info("creating childDevice");

        childDevice = DeviceManager.createChild(
                id,
                type,
                platform,
                parent,
                getHardware(),
                getSupportedOperations(),
                new Relay()
        );

        log.info("created managed object: " + childDevice.getId());
    }

    @Override
    public String supportedOperationType() {
        return "c8y_" + type;
    }

    @Override
    public OperationExecutor[] getSupportedOperations() {
        return new OperationExecutor[]{this};
    }

    @Override
    public void initializeInventory(ManagedObjectRepresentation managedObject) {
        log.info("initialize inventory");
    }

    @Override
    public void start() {
        setRelayOn(false, true);
    }

    @Override
    public void execute(OperationRepresentation operation, boolean cleanup) throws Exception {
        log.info("checking execution " + (childDevice == null ? "null" : "not null"));

        if (!childDevice.getId().equals(operation.getDeviceId())) {
            log.info((cleanup ? "cleanup" : "normal") + " execution for device '" + operation.getDeviceId() + "' requested but does not match this device (" + childDevice.getId() + "), ignoring it");

            return;
        }

        log.info("performing " + (cleanup ? "cleanup" : "normal") + " execution for device '" + operation.getDeviceId() + "'");

        if (cleanup) {
            operation.setStatus(OperationStatus.FAILED.toString());

            return;
        } else {
            operation.setStatus(OperationStatus.SUCCESSFUL.toString());
        }

        boolean isRelayOn = relay.getRelayState().equals(Relay.RelayState.CLOSED);

        setRelayOn(!isRelayOn);
    }

    protected Hardware getHardware() {
        return new Hardware(
                "Simulated Relay Actuator",
                "356734556743235",
                "1.0.0"
        );
    }

    protected void applyRelayState(boolean isRelayOn) {
        log.info("turning simulated relay " + (isRelayOn ? "on" : "off"));
    }

    private void setRelayOn(boolean isRelayOn, boolean isForced) {
        boolean isRelayCurrentlyOn = relay.getRelayState() == Relay.RelayState.CLOSED;

        if (!isForced && isRelayOn == isRelayCurrentlyOn) {
            log.info("relay is already " + (isRelayOn ? "on" : "off") + ", ignoring request");

            return;
        }

        applyRelayState(isRelayOn);
        updateManagedObjectState(isRelayOn);
        sendStateMeasurement(isRelayOn);
    }

    private void setRelayOn(boolean isRelayOn) {
        setRelayOn(isRelayOn, false);
    }

    private void updateManagedObjectState(boolean isRelayOn) {
        log.info("updating relay managed object state to " + (isRelayOn ? "on" : "off") + " state");

        relay.setRelayState(isRelayOn ? Relay.RelayState.CLOSED : Relay.RelayState.OPEN);

        ManagedObjectRepresentation updateRelayManagedObject = new ManagedObjectRepresentation();
        updateRelayManagedObject.setId(childDevice.getId());
        updateRelayManagedObject.set(relay);

        childDevice = platform.getInventoryApi().update(updateRelayManagedObject);
    }

    private void sendStateMeasurement(boolean isRelayOn) {
        log.info("sending relay state change measurement: " + (isRelayOn ? "on" : "off") + " state");

        RelayStateMeasurement relayStateMeasurement = new RelayStateMeasurement();
        relayStateMeasurement.setState(relay.getRelayState());

        MeasurementRepresentation measurementRepresentation = new MeasurementRepresentation();

        measurementRepresentation.setSource(childDevice);
        measurementRepresentation.setType("c8y_" + type);
        measurementRepresentation.set(relayStateMeasurement);
        measurementRepresentation.setTime(new Date());

        measurementApi.create(measurementRepresentation);
    }
}
