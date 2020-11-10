package dev.demon.venom.impl.checks.other.badpackets;

import dev.demon.venom.api.check.Check;
import dev.demon.venom.api.check.CheckInfo;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.tinyprotocol.packet.in.WrappedInBlockDigPacket;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.BlockDigEvent;
import dev.demon.venom.impl.events.inevents.BlockPlaceEvent;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;
import dev.demon.venom.utils.time.TimeUtils;

@CheckInfo(name = "BadPackets", type = "V", banvl = 5)
public class BadPacketsV extends Check {

    private long lastPlace;

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof FlyingInEvent) {
            if (user.getLagProcessor().isTotalLag()) {
                lastPlace = 1000L;
                violation = 0;
                return;
            }
            if (TimeUtils.elapsed(lastPlace) <= 5L) {
                if (violation++ > 5) {
                    alert(user, false, "Spamming Block Place Packets");
                }
            } else violation -= Math.min(violation, 0.95);
        }
        if (e instanceof BlockPlaceEvent) {
            lastPlace = System.currentTimeMillis();
        }
    }
}