package dev.demon.venom.impl.events.inevents;

import dev.demon.venom.api.event.AnticheatEvent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomPayLoadInEvent extends AnticheatEvent {

    @Getter
    @Setter
    private String channel;
    private Object data;

    public CustomPayLoadInEvent(String channel, Object data) {
        this.channel = channel;
        this.data = data;
    }
}
