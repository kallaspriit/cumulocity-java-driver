package com.stagnationlab.c8y.driver.devices;

public class DigitalAnalogConverter {

    private int value = 0;

    public DigitalAnalogConverter() {}

    public final int getValue() {
        return value;
    }

    public final void setValue(int value) {
        this.value = value;
    }

    public String toString() {
        return "DigitalAnalogConverter{value=" + value + '}';
    }

}
