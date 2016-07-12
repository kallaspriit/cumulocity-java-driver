package com.stagnationlab.c8y.driver.sensors;

import c8y.Hardware;
import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RaspberryMotionSensor extends AbstractMotionSensor {

    private static final Logger log = LoggerFactory.getLogger(RaspberryMotionSensor.class);

    private final Pin pinName;

    public RaspberryMotionSensor(String id, Pin pinName) {
        super(id);

        this.pinName = pinName;
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

        GpioPinDigitalInput motionPin;

        try {
            GpioController gpio = GpioFactory.getInstance();

            motionPin = gpio.provisionDigitalInputPin(pinName, PinPullResistance.PULL_DOWN);
        } catch (Exception e) {
            throw new Exception("provisioning pin failed (" + e.getMessage() + ")");
        }

        motionPin.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                if (event.getState() == PinState.HIGH) {
                    log.info("motion detected");

                    triggerMotionDetected();
                } else {
                    log.info("motion reset");

                    triggerMotionEnded();
                }
            }
        });
    }

    @Override
    Hardware getHardware() {
        return new Hardware(
                "Raspberry Motion Sensor",
                "123429524592063",
                "1.0.0"
        );
    }
}
