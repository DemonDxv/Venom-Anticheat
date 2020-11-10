package dev.demon.venom.impl.events.inevents;

import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.tinyprotocol.packet.in.WrappedInBlockDigPacket;
import dev.demon.venom.api.tinyprotocol.packet.types.BaseBlockPosition;
import dev.demon.venom.api.tinyprotocol.packet.types.EnumDirection;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BlockDigEvent extends AnticheatEvent {

    private BaseBlockPosition position;
    private EnumDirection direction;
    private WrappedInBlockDigPacket.EnumPlayerDigType action;

    public BlockDigEvent(WrappedInBlockDigPacket.EnumPlayerDigType action, EnumDirection direction, BaseBlockPosition position) {
        this.action = action;
        this.direction = direction;
        this.position = position;
    }
}
