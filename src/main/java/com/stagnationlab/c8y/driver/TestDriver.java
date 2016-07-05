package com.stagnationlab.c8y.driver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.model.operation.OperationStatus;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.operation.OperationRepresentation;
import com.cumulocity.sdk.client.Platform;

import c8y.Hardware;
import c8y.lx.driver.Driver;
import c8y.lx.driver.HardwareProvider;
import c8y.lx.driver.OperationExecutor;
import c8y.lx.driver.OpsUtil;


public class TestDriver implements Driver, OperationExecutor, HardwareProvider {
    private static Logger log = LoggerFactory.getLogger(TestDriver.class);

    private static final String WINDOWS_DEVICE_INFO_COMMAND = "wmic csproduct get name,identifyingnumber /format:list";
    private static final String HARDWARE_MODEL = "PC";
    private static final String HARDWARE_SERIAL = "1234";
    private static final String HARDWARE_REVISION = "1.0.0";
    private final Hardware hardware = new Hardware(HARDWARE_MODEL, HARDWARE_SERIAL, HARDWARE_REVISION);
    private GId gid;

    @Override
    public Hardware getHardware() {
        log.info("hardware requested");

        return hardware;
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
    public void initialize() throws Exception {
        log.info("initializing");

        initializeFromProcessCommand(WINDOWS_DEVICE_INFO_COMMAND);
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
    public OperationExecutor[] getSupportedOperations() {
        log.info("supported operations requested");

        return new OperationExecutor[]{this};
    }

    private void initializeFromProcessCommand(String command) throws Exception {
        Process process = Runtime.getRuntime().exec(command);

        InputStream inputStream = process.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

        initializeFromReader(inputStreamReader);
    }

    private void initializeFromReader(Reader reader) throws IOException {
        try (BufferedReader bufferedReader = new BufferedReader(reader)) {
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                String[] keyValuePair = line.trim().split("=");

                if ("Name".equals(keyValuePair[0])) {
                    this.hardware.setModel(keyValuePair[1]);
                }

                if ("IdentifyingNumber".equals(keyValuePair[0])) {
                    this.hardware.setSerialNumber(keyValuePair[1]);
                }
            }
        }

        log.info("detected model: " + this.hardware.getModel() + ", serial number: " + this.hardware.getSerialNumber());
    }
}
