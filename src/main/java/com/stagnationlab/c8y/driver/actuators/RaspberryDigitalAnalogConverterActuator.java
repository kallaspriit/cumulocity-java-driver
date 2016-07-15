package com.stagnationlab.c8y.driver.actuators;

import c8y.Hardware;
import com.pi4j.wiringpi.SoftPwm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RaspberryDigitalAnalogConverterActuator extends AbstractDigitalAnalogConverterActuator {

    private static final Logger log = LoggerFactory.getLogger(RaspberryDigitalAnalogConverterActuator.class);

    private final int gpioNumber;

    public RaspberryDigitalAnalogConverterActuator(String id, int gpioNumber) {
        super(id);

        this.gpioNumber = gpioNumber;
    }

    @Override
    public void initialize() throws Exception {
        String osName = System.getProperty("os.name");

        if (!osName.toLowerCase().contains("linux")) {
            log.info("not linux platform (" + osName + "), skipping initialization");

            throw new Exception("Skipping initialization on a non-linux platform");
        }

        log.info("initializing on " + osName);

        SoftPwm.softPwmCreate(gpioNumber, 0, 100);
    }

    @Override
    protected Hardware getHardware() {
        return new Hardware(
                "Raspberry Relay Actuator",
                "876123688932234",
                "1.0.0"
        );
    }

    @Override
    void applyValue(int value) {
        //log.info("setting dac value: " + value);

        SoftPwm.softPwmWrite(gpioNumber, value);
    }


}
