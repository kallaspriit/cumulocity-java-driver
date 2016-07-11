package com.stagnationlab.c8y.driver.actuators;

import c8y.Hardware;
import com.pi4j.io.gpio.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RaspberryRelayActuator extends AbstractRelayActuator {

    private static final Logger log = LoggerFactory.getLogger(RaspberryRelayActuator.class);

    private final Pin pinName;
    private GpioPinDigitalOutput relayPin;

    public RaspberryRelayActuator(String id, Pin pinName) {
        super(id);

        this.pinName = pinName;
    }

    @Override
    public void initialize() throws Exception {
        String osName = System.getProperty("os.name");

        if (!osName.toLowerCase().contains("linux")) {
            log.info("not linux platform (" + osName + "), skipping initialization");

            throw new Exception("Skipping initialization on a non-linux platform");
        }

        log.info("initializing on " + osName);

        try {
            GpioController gpio = GpioFactory.getInstance();

            relayPin = gpio.provisionDigitalOutputPin(pinName, PinState.LOW);
            relayPin.setShutdownOptions(true, PinState.HIGH);
        } catch (Exception e) {
            throw new Exception("provisioning pin failed (" + e.getMessage() + ")");
        }
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
    protected void applyRelayState(boolean isRelayOn) {
        log.info("turning raspberry led " + (isRelayOn ? "on" : "off") + " on pin " + pinName.getName());

        relayPin.setState(isRelayOn);
    }
}
