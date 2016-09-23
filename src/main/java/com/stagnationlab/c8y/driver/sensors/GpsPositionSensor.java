package com.stagnationlab.c8y.driver.sensors;

import c8y.Hardware;
import com.stagnationlab.gps.SerialGps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GpsPositionSensor extends AbstractPositionSensor {

    private static final Logger log = LoggerFactory.getLogger(GpsPositionSensor.class);

    private String portName;
    private PositionState positionState = new PositionState();

    public GpsPositionSensor(String id, String portName) {
        super(id);

        this.portName = portName;
    }

    @Override
    public void initialize() throws Exception {
        SerialGps serialGps = new SerialGps(portName);

        serialGps.addStateListener(state -> {
            positionState = new PositionState(state.lat, state.lon, state.altitude, state.hasFix);

            // log.info("position: " + state.lat + ", " + state.lon + " (" + (state.hasFix ? "got fix" : "no fix") + ")");
        });

        log.info("starting serial gps on '" + portName + "'");

        serialGps.start();

        log.info("starting serial gps succeeded");
    }

    @Override
    Hardware getHardware() {
        return new Hardware(
                "GPS Position Sensor",
                "999234552222546",
                "1.0.0"
        );
    }

    @Override
    PositionState getPositionState() {
        return positionState;
    }

}
