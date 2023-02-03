package com.herosoft.user.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ObservationEvent extends ApplicationEvent {
    public ObservationEvent(Object source) {
        super(source);
    }
}
