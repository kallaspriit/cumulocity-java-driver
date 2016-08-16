package com.stagnationlab.c8y.driver.sensors;

import c8y.Hardware;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import com.stagnationlab.c8y.driver.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@SuppressWarnings("FieldCanBeLocal")
public class RaspberryLightSensor extends AbstractLightSensor {

    private static final Logger log = LoggerFactory.getLogger(RaspberryLightSensor.class);

    private final int RANGE_MIN = 0;
    private final int RANGE_MAX = 2500;

    private int i2cBus;
    private int deviceAddress;
    private I2CDevice device;

    public RaspberryLightSensor(String id, int i2cBus, int deviceAddress) {
        super(id);

        this.i2cBus = i2cBus;
        this.deviceAddress = deviceAddress;
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

        I2CBus bus = I2CFactory.getInstance(i2cBus);
        device = bus.getDevice(deviceAddress);

        // configure device
        device.write(0x02, (byte) 0x20);
    }

    @Override
    Hardware getHardware() {
        return new Hardware(
                "Raspberry Light Sensor",
                "632622205364780",
                "1.0.0"
        );
    }

    protected double getIlluminance() {
        byte[] data = new byte[2];
        try {
            device.read(0x00, data, 0, 2);
        } catch (IOException e) {
            e.printStackTrace();

            log.warn("reading from i2c bus failed (" + e.getMessage() + ")");

            return 0.0;
        }

        // convert the data to 12-bits
        int value = ((data[0] & 0x0F) * 256 + (data[1] & 0xFF));

        return Math.max(Math.min(Util.map(value, RANGE_MIN, RANGE_MAX, 0.0, 100.0), 100.0), 0.0);
    }

}
