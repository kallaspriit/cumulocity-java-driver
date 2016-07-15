package com.stagnationlab.c8y.driver.actuators;

import c8y.Hardware;
import c8y.lx.driver.Driver;
import c8y.lx.driver.OperationExecutor;
import com.cumulocity.model.operation.OperationStatus;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.sdk.client.Platform;
import com.stagnationlab.c8y.driver.DeviceManager;
import com.stagnationlab.c8y.driver.devices.DigitalAnalogConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class AbstractDigitalAnalogConverterActuator implements Driver, OperationExecutor {

    private static final Logger log = LoggerFactory.getLogger(AbstractDigitalAnalogConverterActuator.class);

    private static final String TYPE = "DigitalAnalogConverter";

    private final DigitalAnalogConverter digitalAnalogConverter = new DigitalAnalogConverter();
    private Platform platform;
    private ManagedObjectRepresentation childDevice;
    private final String id;

    AbstractDigitalAnalogConverterActuator(String id) {
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
        log.info("creating childDevice");

        childDevice = DeviceManager.createChild(
                id,
                TYPE,
                platform,
                parent,
                getHardware(),
                getSupportedOperations(),
                new DigitalAnalogConverter()
        );

        log.info("created managed object: " + childDevice.getId());
    }

    @Override
    public String supportedOperationType() {
        return "com_stagnationlab_c8y_driver_devices_DigitalAnalogConverter";
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
        setValue(0);
    }

    @Override
    public void execute(OperationRepresentation operation, boolean cleanup) throws Exception {
        if (!childDevice.getId().equals(operation.getDeviceId())) {
            return;
        }

        //log.info("performing " + (cleanup ? "cleanup" : "normal") + " execution for device '" + operation.getDeviceId() + "'");

        if (cleanup) {
            operation.setStatus(OperationStatus.FAILED.toString());

            return;
        } else {
            operation.setStatus(OperationStatus.SUCCESSFUL.toString());
        }

        DigitalAnalogConverter digitalAnalogConverter = operation.get(DigitalAnalogConverter.class);

        int value = digitalAnalogConverter.getValue();

        setValue(value);
    }

    abstract Hardware getHardware();

    abstract void applyValue(int value);

    private void setValue(int value) {
        applyValue(value);
        updateManagedObjectState(value);
    }

    private void updateManagedObjectState(int value) {
        log.info("updating dac managed object value to " + value);

        digitalAnalogConverter.setValue(value);

        ManagedObjectRepresentation updateRelayManagedObject = new ManagedObjectRepresentation();
        updateRelayManagedObject.setId(childDevice.getId());
        updateRelayManagedObject.set(digitalAnalogConverter);

        childDevice = platform.getInventoryApi().update(updateRelayManagedObject);
    }

}
