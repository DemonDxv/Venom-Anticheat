package dev.demon.venom.impl.checks.movement.fly;

import dev.demon.venom.api.check.Check;
import dev.demon.venom.api.check.CheckInfo;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;
import dev.demon.venom.utils.time.TimeUtils;

@CheckInfo(name = "Fly", type = "D", banvl = 10)
public class FlyD extends Check {
    private double vl;
    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof FlyingInEvent) {
            if (TimeUtils.elapsed(user.getMovementData().getLastTeleportInBlock()) < 1000L) {
                return;
            }
            if (user.getMovementData().isClientGround() && !user.getMovementData().isLastClientGround() && !user.getMovementData().isOnGround()) {
                if (user.getConnectedTick() > 200) {
                    if (vl++ > 0) {
                        alert(user, false, "Spoofing Ground [Tick]");
                    }
                }
            } else vl -= Math.min(vl, 0.75);

            if (user.getMovementData().isClientGround() && !user.getMovementData().isOnGround() && user.getConnectedTick() > 200) {
                if (violation++ > 5) {
                    alert(user, false, "Spoofing Ground [Tick]");
                }
            } else violation -= Math.min(violation, 0.25);
        }
    }
}