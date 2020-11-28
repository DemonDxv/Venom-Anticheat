package dev.demon.venom.api.tinyprotocol.packet.types.enums;

import dev.demon.venom.api.tinyprotocol.api.ProtocolVersion;
import dev.demon.venom.api.tinyprotocol.api.packets.reflections.Reflections;
import dev.demon.venom.api.tinyprotocol.api.packets.reflections.types.WrappedClass;
import dev.demon.venom.api.tinyprotocol.packet.types.WrappedChatMessage;

public enum WrappedEnumMainHand {
    LEFT(new WrappedChatMessage("options.mainHand.left")),
    RIGHT(new WrappedChatMessage("options.mainHand.right"));

    private final WrappedChatMessage c;
    public static WrappedClass vanillaClass;

    WrappedEnumMainHand(WrappedChatMessage var2) {
        this.c = var2;
    }

    public String toString() {
        return this.c.getChatMessage();
    }

    public <T> T toVanilla() {
        return (T) vanillaClass.getEnum(name());
    }

    public static WrappedEnumMainHand fromVanilla(Object object) {
        if(object instanceof Enum) {
            return valueOf(((Enum)object).name());
        }
        return null;
    }

    static {
        if(ProtocolVersion.getGameVersion().isAbove(ProtocolVersion.V1_8_9))
            vanillaClass = Reflections.getNMSClass("EnumMainHand");
    }
}