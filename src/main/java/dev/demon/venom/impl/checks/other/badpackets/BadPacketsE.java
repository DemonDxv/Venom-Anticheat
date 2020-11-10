package dev.demon.venom.impl.checks.other.badpackets;

import dev.demon.venom.api.check.Check;
import dev.demon.venom.api.check.CheckInfo;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.tinyprotocol.api.ProtocolVersion;
import dev.demon.venom.api.tinyprotocol.api.TinyProtocolHandler;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.ArmAnimationEvent;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;
import dev.demon.venom.impl.events.inevents.UseEntityEvent;
import dev.demon.venom.utils.math.Verbose;
import dev.demon.venom.utils.time.TimeUtils;

@CheckInfo(name = "BadPackets", type = "E", banvl = 5)
public class BadPacketsE  extends Check {

    private long lastSwing, lastAttack;
    private int clicksPerSecond, ticks;
    private Verbose verbose = new Verbose();

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof FlyingInEvent) {
            if (TimeUtils.elapsed(lastAttack) < 1000L
                    || TinyProtocolHandler.getProtocolVersion(user.getPlayer()).equals(ProtocolVersion.V1_7_10)) {
                return;
            }

            if (++ticks == 20) {
                if (clicksPerSecond >= 11) {
                    if (TimeUtils.elapsed(lastSwing) <= 100L) {
                        if (verbose.flag(3, 2000L)) {
                       //     alert(user, true, "C: " + clicksPerSecond + " (1.7 packets on 1.8)");
                        }
                    }
                }
                ticks = clicksPerSecond = 0;
            }
        }
        if (e instanceof UseEntityEvent) {
            lastAttack = System.currentTimeMillis();
        }
        if (e instanceof ArmAnimationEvent) {
            lastSwing = System.currentTimeMillis();
            clicksPerSecond++;
        }
    }
}
