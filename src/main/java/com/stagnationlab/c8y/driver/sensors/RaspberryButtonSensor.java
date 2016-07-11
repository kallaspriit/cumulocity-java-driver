package com.stagnationlab.c8y.driver.sensors;

import c8y.Hardware;
import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RaspberryButtonSensor extends AbstractButtonSensor {

    private static final Logger log = LoggerFactory.getLogger(RaspberryMotionSensor.class);

    private final Pin pinName;
    private GpioPinDigitalInput buttonPin;

    public RaspberryButtonSensor(String id, Pin pinName) {
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

            buttonPin = gpio.provisionDigitalInputPin(pinName, PinPullResistance.PULL_UP);
        } catch (Exception e) {
            throw new Exception("provisioning pin failed (" + e.getMessage() + ")");
        }
    }

    @Override
    Hardware getHardware() {
        return new Hardware(
                "Raspberry Button Sensor",
                "223735095238234",
                "1.0.0"
        );
    }

    @Override
    public void start() {
        super.start();

        log.info("starting");

        sendInitialState();
        setupStateChangeListener();
    }

    private void sendInitialState() {
        if (buttonPin.getState() == PinState.LOW) {
            log.info("sending initial button pressed");

            triggerButtonPressed();
        } else {
            log.info("sending initial button released");

            triggerButtonReleased();
        }
    }

    private void setupStateChangeListener() {
        log.info("setting up state change listener");

        buttonPin.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                if (event.getState() == PinState.LOW) {
                    log.info("button pressed");

                    triggerButtonPressed();
                } else {
                    log.info("button released");

                    triggerButtonReleased();
                }
            }
        });
    }

}
