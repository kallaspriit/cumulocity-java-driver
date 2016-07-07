package com.stagnationlab.c8y.driver.actuators;

import c8y.Hardware;
import c8y.Relay;
import c8y.lx.driver.DeviceManagedObject;
import c8y.lx.driver.Driver;
import c8y.lx.driver.OperationExecutor;
import c8y.lx.driver.OpsUtil;
import com.cumulocity.model.ID;
import com.cumulocity.model.operation.OperationStatus;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.sdk.client.Platform;
import com.stagnationlab.c8y.driver.ChildDeviceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimulatedRelayActuator implements Driver, OperationExecutor {

    private static Logger log = LoggerFactory.getLogger(SimulatedRelayActuator.class);

    private static final String type = "Relay";
    private ManagedObjectRepresentation relayManagedObject;
    private Relay relay = new Relay();
    private String id;
    private Platform platform;

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
    }

    @Override
    public void discoverChildren(ManagedObjectRepresentation parent) {
        log.info("creating child");

        relay.setRelayState(Relay.RelayState.OPEN);

        relayManagedObject = ChildDeviceFactory.createChild(
                id,
                type,
                new Relay(),
                new Hardware(
                        "Simulated Relay Actuator",
                        "356734556743235",
                        "1.0.0"
                )
        );

        for (OperationExecutor operation : getSupportedOperations()) {
            log.info("registering supported operation type '" + operation.supportedOperationType() + "'");

            OpsUtil.addSupportedOperation(relayManagedObject, operation.supportedOperationType());
        }

        DeviceManagedObject deviceManagedObject = new DeviceManagedObject(platform);
        ID externalId = ChildDeviceFactory.buildExternalId(parent, relayManagedObject, id);

        deviceManagedObject.createOrUpdate(relayManagedObject, externalId, parent.getId());
    }

    @Override
    public String supportedOperationType() {
        return "c8y_Relay";
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
        setLedOn(false, true);
    }

    @Override
    public void execute(OperationRepresentation operation, boolean cleanup) throws Exception {
        if (!relayManagedObject.getId().equals(operation.getDeviceId())) {
            log.info((cleanup ? "cleanup" : "normal") + " execution for device '" + operation.getDeviceId() + "' requested but does not match this device (" + relayManagedObject.getId() + "), ignoring it");

            return;
        }

        log.info("performing " + (cleanup ? "cleanup" : "normal") + " execution for device '" + operation.getDeviceId() + "'");

        if (cleanup) {
            operation.setStatus(OperationStatus.FAILED.toString());

            return;
        } else {
            operation.setStatus(OperationStatus.SUCCESSFUL.toString());
        }

        boolean isLedOn = relay.getRelayState().equals(Relay.RelayState.CLOSED);

        setLedOn(!isLedOn);
    }

    private void setLedOn(boolean isLedOn, boolean isForced) {
        boolean isRelayActivated = relay.getRelayState() == Relay.RelayState.CLOSED;

        if (!isForced && isLedOn == isRelayActivated) {
            log.info("led is already " + (isLedOn ? "on" : "off") + ", ignoring request");

            return;
        }

        log.info("turning led " + (isLedOn ? "on" : "off"));

        relay.setRelayState(isLedOn ? Relay.RelayState.CLOSED : Relay.RelayState.OPEN);

        ManagedObjectRepresentation updateRelayManagedObject = new ManagedObjectRepresentation();
        updateRelayManagedObject.setId(relayManagedObject.getId());
        updateRelayManagedObject.set(relay);

        platform.getInventoryApi().update(updateRelayManagedObject);
    }

    private void setLedOn(boolean isLedOn) {
        setLedOn(isLedOn, false);
    }
}
