package dev.demon.venom.impl.events.inevents;

import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.tinyprotocol.packet.in.WrappedInWindowClickPacket;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

@Getter
@Setter
public class ClickWindowInEvent extends AnticheatEvent {

    @Getter
    @Setter
    private int id;

    private WrappedInWindowClickPacket.ClickType clickType;

    private byte mode, button;

    private short counter, slot;

    private ItemStack itemStack;

    public ClickWindowInEvent(int id, WrappedInWindowClickPacket.ClickType clickType, byte button, short counter, ItemStack itemStack, byte mode, short slot) {
        this.id = id;
        this.clickType = clickType;
        this.button = button;
        this.counter = counter;
        this.itemStack = itemStack;
        this.mode = mode;
        this.slot = slot;
    }
}
