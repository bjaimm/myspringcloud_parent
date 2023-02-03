package com.herosoft.user.listeners;

import com.herosoft.user.events.ObservationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;

import javax.validation.constraints.NotNull;

public class ObservationListenerB implements ApplicationListener<ObservationEvent> {
    @NotNull
    private String name;

    private String message;

    public ObservationListenerB(String name) {
        this.name = name;
    }

    @Override
    public void onApplicationEvent(ObservationEvent observationEvent) {
        doBusinessB(observationEvent);
    }

    private void doBusinessB(ObservationEvent observationEvent) {
        this.message=(String)observationEvent.getSource();

        System.out.println(this.name+"开始执行相关业务，处理收到的消息："+this.message);
    }
}
