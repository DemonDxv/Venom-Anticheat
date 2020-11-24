package dev.demon.venom.impl.events.outevents;

import dev.demon.venom.api.event.AnticheatEvent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomPayLoadOutEvent extends AnticheatEvent {

    @Getter
    @Setter
    private String channel;
    private Object data;

    public CustomPayLoadOutEvent(String channel, Object data) {
        this.channel = channel;
        this.data = data;
    }
}
