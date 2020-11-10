package dev.demon.venom.api.tinyprotocol.packet.outgoing;

import dev.demon.venom.api.tinyprotocol.api.NMSObject;
import dev.demon.venom.api.tinyprotocol.api.Packet;
import dev.demon.venom.api.tinyprotocol.api.ProtocolVersion;
import dev.demon.venom.api.tinyprotocol.api.packets.reflections.Reflections;
import dev.demon.venom.api.tinyprotocol.api.packets.reflections.types.WrappedClass;
import dev.demon.venom.api.tinyprotocol.api.packets.reflections.types.WrappedField;
import org.bukkit.entity.Player;

public class WrappedOutCloseWindowPacket extends NMSObject {

    private static final WrappedClass packet = Reflections.getNMSClass(Packet.Server.CLOSE_WINDOW);
    private static final WrappedField idField = packet.getFieldByType(int.class, 0);

    public WrappedOutCloseWindowPacket(Object object, Player player) {
        super(object, player);
    }

    public WrappedOutCloseWindowPacket(int id) {
        this.id = id;
        updateObject();
    }

    public int id;

    @Override
    public void updateObject() {
        setObject(NMSObject.construct(getObject(), Server.CLOSE_WINDOW, id));
    }

    @Override
    public void process(Player player, ProtocolVersion version) {
        id = fetch(idField);
    }
}