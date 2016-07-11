package com.stagnationlab.c8y.driver.events;

import com.cumulocity.rest.representation.event.EventRepresentation;

import java.util.Date;

public class MotionEndedEvent extends EventRepresentation {

    public MotionEndedEvent() {
        setType("c8y_MotionEvent");
        setText("Motion ended");
        setTime(new Date());
    }

}
