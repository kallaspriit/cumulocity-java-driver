package com.stagnationlab.c8y.driver.sensors;

import c8y.Hardware;
import com.pi4j.system.SystemInfo;
import com.stagnationlab.c8y.driver.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RaspberryMonitoringSensor extends AbstractMonitoringSensor {

    private static final Logger log = LoggerFactory.getLogger(RaspberryMonitoringSensor.class);

    public RaspberryMonitoringSensor(String id) {
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
                "Raspberry Monitoring Sensor",
                "009803006543656",
                "1.0.0"
        );
    }

    @Override
    MonitoringStatus getMonitoringStatus() {
        try {
            return new MonitoringStatus(
                    Util.convertBytesToMb(SystemInfo.getMemoryTotal()),
                    Util.convertBytesToMb(SystemInfo.getMemoryUsed()),
                    Util.convertBytesToMb(SystemInfo.getMemoryFree()),
                    Util.convertBytesToMb(SystemInfo.getMemoryShared()),
                    Util.convertBytesToMb(SystemInfo.getMemoryBuffers()),
                    Util.convertBytesToMb(SystemInfo.getMemoryCached())
            );
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

}
