package dev.demon.venom.impl.checks.other.badpackets;

import dev.demon.venom.api.check.Check;
import dev.demon.venom.api.check.CheckInfo;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.CloseWindowInEvent;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;
import dev.demon.venom.impl.events.inevents.HeldItemSlotInEvent;
import dev.demon.venom.impl.events.inevents.UseEntityEvent;
import dev.demon.venom.impl.events.outevents.CloseWindowOutEvent;
import dev.demon.venom.impl.events.outevents.HeldItemSlotOutEvent;

@CheckInfo(name = "BadPackets", type = "K", banvl = 10)
public class BadPacketsK extends Check {

    private int lastSlot;

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof HeldItemSlotOutEvent) {
            if (((HeldItemSlotOutEvent) e).getSlot() == lastSlot) {
                alert(user, false, "S -> "+((HeldItemSlotOutEvent) e).getSlot() + " LS -> "+lastSlot);
            }
            lastSlot = ((HeldItemSlotOutEvent) e).getSlot();
        }
    }
}