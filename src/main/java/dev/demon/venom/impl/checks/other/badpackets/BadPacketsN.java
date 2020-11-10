package dev.demon.venom.impl.checks.other.badpackets;

import dev.demon.venom.api.check.Check;
import dev.demon.venom.api.check.CheckInfo;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.BlockDigEvent;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;
import dev.demon.venom.impl.events.inevents.UseEntityEvent;
import dev.demon.venom.impl.events.outevents.RespawnOutEvent;

@CheckInfo(name = "BadPackets", type = "M", banvl = 10)
public class BadPacketsN extends Check {

    private boolean sentRespawn;

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof RespawnOutEvent) {
            sentRespawn = true;
        }

        if (e instanceof BlockDigEvent) {
            if (sentRespawn) {
                alert(user, false, "Respawn Sent With Dig Packet");
            }
        }

        if (e instanceof FlyingInEvent) {
            sentRespawn = false;
        }
    }
}