package dev.demon.venom.impl.checks.other.badpackets;

import dev.demon.venom.api.check.Check;
import dev.demon.venom.api.check.CheckInfo;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;
import dev.demon.venom.impl.events.inevents.HeldItemSlotInEvent;
import dev.demon.venom.impl.events.inevents.UseEntityEvent;
import dev.demon.venom.impl.events.outevents.HeldItemSlotOutEvent;
import dev.demon.venom.impl.events.outevents.RespawnOutEvent;

@CheckInfo(name = "BadPackets", type = "M", banvl = 10)
public class BadPacketsM extends Check {

    private boolean sentRespawn;

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof RespawnOutEvent) {
            sentRespawn = true;
        }

        if (e instanceof UseEntityEvent) {
            if (((UseEntityEvent) e).getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK) {
                if (sentRespawn) {
                    alert(user, false, "Respawn while attacking");
                }
            }
        }

        if (e instanceof FlyingInEvent) {
            sentRespawn = false;
        }
    }
}