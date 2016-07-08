package com.stagnationlab.c8y.driver.models;

import com.cumulocity.rest.representation.event.EventRepresentation;

import java.util.Date;

public class MotionEvent extends EventRepresentation {

    public MotionEvent() {
        setType("c8y_MotionEvent");
        setText("Motion detected");
        setTime(new Date());
    }
}
