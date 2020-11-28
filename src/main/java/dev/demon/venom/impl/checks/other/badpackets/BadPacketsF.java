package dev.demon.venom.impl.checks.other.badpackets;

import dev.demon.venom.api.check.Check;
import dev.demon.venom.api.check.CheckInfo;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.HeldItemSlotInEvent;

@CheckInfo(name = "BadPackets", type = "F", banvl = 10)
public class BadPacketsF extends Check {

    private int lastSlot;

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof HeldItemSlotInEvent) {
            if (((HeldItemSlotInEvent) e).getSlot() == lastSlot) {
                if (user.getConnectedTick() > 250) {
                    if (violation++ > 1) {
                        alert(user, false, "S -> " + ((HeldItemSlotInEvent) e).getSlot() + " LS -> " + lastSlot);
                    }
                } else violation -= Math.min(violation, 0.25);
            }
            lastSlot = ((HeldItemSlotInEvent) e).getSlot();
        }
    }
}
