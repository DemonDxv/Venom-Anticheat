package dev.demon.venom.impl.checks.other.badpackets;

import dev.demon.venom.api.check.Check;
import dev.demon.venom.api.check.CheckInfo;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;
import dev.demon.venom.impl.events.inevents.PlayerActionEvent;
import dev.demon.venom.utils.math.MathUtil;
import dev.demon.venom.utils.time.TimeUtils;

@CheckInfo(name = "BadPackets", type = "G1", banvl = 10)
public class BadPacketsG1 extends Check {

    private long lastFlying;

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof FlyingInEvent) {
            if (user.getLagProcessor().isLagging()) {
                lastFlying = 1000L;
            }
            lastFlying = System.currentTimeMillis();
        }
        if (e instanceof PlayerActionEvent) {
            if (TimeUtils.elapsed(user.getMovementData().getLastTeleportInBlock()) < 1000L) {
                lastFlying = 10000L;
                return;
            }
            if (MathUtil.isPost(lastFlying)) {
                if (violation++ > 10) {
                    alert(user, true, "Sent player action packet late");
                }
            } else violation -= Math.min(violation, 0.75);
        }
    }
}