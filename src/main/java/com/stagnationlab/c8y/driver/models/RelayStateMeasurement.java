package com.stagnationlab.c8y.driver.models;

import c8y.Relay;
import com.cumulocity.model.measurement.MeasurementValue;
import com.cumulocity.model.measurement.StateType;
import com.cumulocity.model.measurement.ValueType;
import org.svenson.JSONProperty;

import java.math.BigDecimal;

public class RelayStateMeasurement {

    private MeasurementValue relayState;

    @SuppressWarnings("unused")
    public RelayStateMeasurement() {
        relayState = new MeasurementValue(
                stateToValue(Relay.RelayState.OPEN),
                "state",
                ValueType.BOOLEAN,
                "x",
                StateType.ORIGINAL
        );
    }

    @SuppressWarnings("unused")
    @JSONProperty("RelayState")
    public MeasurementValue getRelayState() {
        return relayState;
    }

    @SuppressWarnings("unused")
    public void setRelayState(MeasurementValue relayState) {
        this.relayState = relayState;
    }

    @SuppressWarnings("unused")
    @JSONProperty(
            ignore = true
    )
    public BigDecimal getState() {
        return this.relayState.getValue();
    }

    public void setState(Relay.RelayState relayState) {
        this.relayState.setValue(stateToValue(relayState));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } else if (obj == this) {
            return true;
        } else if (!(obj instanceof RelayStateMeasurement)) {
            return false;
        } else {
            RelayStateMeasurement rhs = (RelayStateMeasurement) obj;

            return this.relayState == null ? rhs.relayState == null : this.relayState.equals(rhs.relayState);
        }
    }

    @Override
    public int hashCode() {
        return this.relayState == null ? 0 : this.relayState.hashCode();
    }

    private BigDecimal stateToValue(Relay.RelayState relayState) {
        return new BigDecimal(relayState.ordinal());
    }

}
