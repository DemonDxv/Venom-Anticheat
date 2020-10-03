package dev.demon.venom.api.tinyprotocol.packet;

import dev.demon.venom.api.tinyprotocol.api.packets.reflections.types.WrappedField;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class PacketField<T> {
    private WrappedField field;
    private T value;
}
