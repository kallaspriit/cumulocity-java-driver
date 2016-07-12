package com.stagnationlab.c8y.driver.sensors;

import c8y.Hardware;
import com.pi4j.system.SystemInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RaspberryTemperatureSensor extends AbstractTemperatureSensor {

    private static final Logger log = LoggerFactory.getLogger(RaspberryTemperatureSensor.class);

    public RaspberryTemperatureSensor(String id) {
        super(id);
    }

    @Override
    public void initialize() throws Exception {
        super.initialize();

        String osName = System.getProperty("os.name");

        if (!osName.toLowerCase().contains("linux")) {
            log.info("not linux platform (" + osName + "), skipping initialization");

            throw new Exception("Skipping initialization on a non-linux platform");
        }

        log.info("initializing on " + osName);
    }

    @Override
    Hardware getHardware() {
        return new Hardware(
                "Raspberry Temperature Sensor",
                "377742456262508",
                "1.0.0"
        );
    }

    @Override
    double getTemperature() {
        try {
            return SystemInfo.getCpuTemperature();
        } catch (Exception e) {
            e.printStackTrace();

            log.warn("getting raspberry CPU temperature failed (" + e.getMessage() + ")");

            return 0;
        }
    }
}
