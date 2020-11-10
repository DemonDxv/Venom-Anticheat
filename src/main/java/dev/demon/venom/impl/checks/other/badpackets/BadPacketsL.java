package dev.demon.venom.impl.checks.other.badpackets;

import dev.demon.venom.api.check.Check;
import dev.demon.venom.api.check.CheckInfo;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;
import dev.demon.venom.impl.events.inevents.HeldItemSlotInEvent;
import dev.demon.venom.impl.events.outevents.HeldItemSlotOutEvent;
import org.bukkit.command.defaults.HelpCommand;

@CheckInfo(name = "BadPackets", type = "L", banvl = 10)
public class BadPacketsL extends Check {

    private int serverSlot, clientSlot;

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof HeldItemSlotOutEvent) {
            serverSlot = ((HeldItemSlotOutEvent) e).getSlot();
        }
        if (e instanceof HeldItemSlotInEvent) {
            clientSlot = ((HeldItemSlotInEvent) e).getSlot();
        }
        if (e instanceof FlyingInEvent) {
            if (clientSlot != serverSlot) {
             //   alert(user, false, "CS -> " + clientSlot + " SS -> "+serverSlot);
            }
        }
    }
}