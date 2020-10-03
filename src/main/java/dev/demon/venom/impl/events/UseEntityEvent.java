package dev.demon.venom.impl.events;

import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Entity;

@Getter
@Setter
public class UseEntityEvent extends AnticheatEvent {


    private Entity entity;
    private WrappedInUseEntityPacket.EnumEntityUseAction action;
    public UseEntityEvent(Entity entity, WrappedInUseEntityPacket.EnumEntityUseAction action) {

        this.entity = entity;
        this.action = action;
    }
}
