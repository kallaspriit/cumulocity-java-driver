package com.stagnationlab.c8y.driver.sensors;

import c8y.Hardware;
import c8y.Position;
import c8y.lx.driver.MeasurementPollingDriver;
import com.cumulocity.rest.representation.event.EventRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.stagnationlab.c8y.driver.DeviceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Date;

abstract class AbstractPositionSensor extends MeasurementPollingDriver {

    class PositionState {
        double latitude;
        double longitude;
        double altitude;

        public PositionState(double latitude, double longitude, double altitude) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.altitude = altitude;
        }
    }

    private static final Logger log = LoggerFactory.getLogger(SimulatedLightSensor.class);

    private static final String TYPE = "Position";

    private ManagedObjectRepresentation childDevice;
    private final String id;

    AbstractPositionSensor(String id) {
        super("c8y_" + TYPE, "c8y." + TYPE.toLowerCase(), 5000);

        this.id = id;
    }

    @Override
    public void initialize() throws Exception {
        log.info("initializing");
    }

    @Override
    public void discoverChildren(ManagedObjectRepresentation parent) {
        log.info("creating child");

        childDevice = DeviceManager.createChild(
                id,
                TYPE,
                getPlatform(),
                parent,
                getHardware(),
                getSupportedOperations(),
                new Position()
        );

        setSource(childDevice);
    }

    @Override
    public void run() {
        updatePositionState();
        createLocationUpdate();
    }

    private void updatePositionState() {
        PositionState positionState = getPositionState();

        Position position = new Position();
        position.setLat(new BigDecimal(positionState.latitude));
        position.setLng(new BigDecimal(positionState.longitude));
        position.setAlt(new BigDecimal(positionState.altitude));

        ManagedObjectRepresentation stateUpdate = new ManagedObjectRepresentation();
        stateUpdate.setId(childDevice.getId());
        stateUpdate.set(position);

        getPlatform().getInventoryApi().update(stateUpdate);
    }

    private void createLocationUpdate() {
        EventRepresentation locationUpdate = new EventRepresentation();
        locationUpdate.setSource(childDevice);
        locationUpdate.setType("c8y_LocationUpdate");
        locationUpdate.setText("Location updated");
        locationUpdate.setTime(new Date());

        getPlatform().getEventApi().create(locationUpdate);
    }

    abstract Hardware getHardware();

    abstract PositionState getPositionState();

}
