package dev.demon.venom.api.tinyprotocol.packet.outgoing;

import dev.demon.venom.api.tinyprotocol.api.NMSObject;
import lombok.Getter;

@Getter
public class WrappedOutEntityDestroy extends NMSObject {
    private static final String packet = Server.ENTITY_DESTROY;

    protected int[] ids;

    public WrappedOutEntityDestroy(int[] ids) {
        this.ids = ids;

        setPacket(packet, ids);
    }

    @Override
    public void updateObject() {

    }
}