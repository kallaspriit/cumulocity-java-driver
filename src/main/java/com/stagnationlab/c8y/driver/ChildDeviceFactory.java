package com.stagnationlab.c8y.driver;

import c8y.Hardware;
import com.cumulocity.model.ID;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;

public class ChildDeviceFactory {

    public static ManagedObjectRepresentation createChild(
            String id,
            String type,
            Object sensorFragment,
            Hardware hardware
    ) {
        ManagedObjectRepresentation managedObjectRepresentation = new ManagedObjectRepresentation();
        managedObjectRepresentation.set(sensorFragment);
        managedObjectRepresentation.set(hardware);
        managedObjectRepresentation.setType(type);
        managedObjectRepresentation.setName(managedObjectRepresentation.getType() + " " + id);

        return managedObjectRepresentation;
    }

    public static ID buildExternalId(ManagedObjectRepresentation parent, ManagedObjectRepresentation child, String id) {
        return new ID(
                parent.get(Hardware.class).getSerialNumber() +
                "-" + child.get(Hardware.class).getSerialNumber() +
                "-" + id
        );
    }

}
