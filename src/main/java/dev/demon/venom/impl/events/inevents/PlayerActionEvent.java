package dev.demon.venom.impl.events.inevents;

import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.tinyprotocol.packet.in.WrappedInEntityActionPacket;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerActionEvent extends AnticheatEvent {

    WrappedInEntityActionPacket.EnumPlayerAction action;

    public PlayerActionEvent(WrappedInEntityActionPacket.EnumPlayerAction action) {
        this.action = action;
    }
}
