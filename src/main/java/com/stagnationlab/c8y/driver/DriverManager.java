package com.stagnationlab.c8y.driver;

import c8y.Hardware;
import c8y.lx.driver.Driver;
import c8y.lx.driver.HardwareProvider;
import c8y.lx.driver.OperationExecutor;
import c8y.lx.driver.OpsUtil;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.model.operation.OperationStatus;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.sdk.client.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DriverManager implements Driver, OperationExecutor, HardwareProvider {
    private static Logger log = LoggerFactory.getLogger(DriverManager.class);

    private Hardware hardware;
    private GId gid;

    @Override
    public Hardware getHardware() {
        log.info("hardware requested");

        return hardware;
    }

    @Override
    public void initialize() throws Exception {
        log.info("initializing");

        hardware = PlatformManager.resolveHardware();
    }

    @Override
    public void initialize(Platform platform) throws Exception {
        log.info("initializing platform");
    }

    @Override
    public void initializeInventory(ManagedObjectRepresentation managedObjectRepresentation) {
        log.info("initializing inventory");

        managedObjectRepresentation.set(hardware);

        OpsUtil.addSupportedOperation(managedObjectRepresentation, supportedOperationType());
    }

    @Override
    public void discoverChildren(ManagedObjectRepresentation managedObjectRepresentation) {
        log.info("discovering children");

        this.gid = managedObjectRepresentation.getId();
    }

    @Override
    public void start() {
        log.info("starting driver");
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

        return new OperationExecutor[]{this};
    }


}
