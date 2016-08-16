package com.stagnationlab.c8y.driver.sensors;

import c8y.Hardware;

public class SimulatedPositionSensor extends AbstractPositionSensor {

    private double latitude = 58.383503;
    private double longitude = 26.719825;
    private double altitude = 20;

    public SimulatedPositionSensor(String id) {
        super(id);
    }

    @Override
    Hardware getHardware() {
        return new Hardware(
                "Simulated Position Sensor",
                "999234262222546",
                "1.0.0"
        );
    }

    @Override
    PositionState getPositionState() {
        double positionSpeed = 0.01;
        double altitudeSpeed = 0.5;

        latitude += Math.random() * positionSpeed - (positionSpeed * 0.5);
        longitude += Math.random() * positionSpeed - (positionSpeed * 0.5);
        altitude += Math.random() * altitudeSpeed - (altitudeSpeed * 0.5);

        return new PositionState(latitude, longitude, altitude);
    }
}
