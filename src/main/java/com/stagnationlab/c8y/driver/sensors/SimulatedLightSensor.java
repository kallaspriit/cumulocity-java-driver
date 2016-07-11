package com.stagnationlab.c8y.driver.sensors;

public class SimulatedLightSensor extends AbstractLightSensor {

    public SimulatedLightSensor(String id) {
        super(id);
    }

    protected double getIlluminance() {
        // simulate gradual illuminance change
        double step = 2.0;
        double randomChange = Math.random() * step - step / 2.0;
        illuminance = Math.min(Math.max(illuminance + randomChange, 0.0), 10.0);

        return illuminance;
    }
}
