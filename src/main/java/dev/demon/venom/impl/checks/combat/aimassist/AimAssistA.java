package dev.demon.venom.impl.checks.combat.aimassist;

import dev.demon.venom.api.check.Check;
import dev.demon.venom.api.check.CheckInfo;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.FlyingEvent;
import dev.demon.venom.utils.math.MathUtil;

@CheckInfo(name = "AimAssist", type = "A")
public class AimAssistA extends Check {

    private final double offset = Math.pow(2.0, 24.0);
    private double lastDiff;

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof FlyingEvent) {

            if ((System.currentTimeMillis() - user.getCombatData().getLastUseEntityPacket() > 2000L)) violation = 0;

            double diff = Math.abs(user.getMovementData().getTo().getPitch() - user.getMovementData().getFrom().getPitch());

            long gcd = MathUtil.gcd((long) (diff * offset), (long) (lastDiff * offset));


            if (user.getMovementData().getTo().getYaw() != user.getMovementData().getFrom().getYaw()
                    && user.getMovementData().getTo().getPitch() != user.getMovementData().getFrom().getPitch()) {
                if (diff > 0 && Math.abs(user.getMovementData().getTo().getPitch()) != 90.0) {
                    if ((System.currentTimeMillis() - user.getCombatData().getLastUseEntityPacket() < 720L)) {
                        if (gcd < 131072L) {
                            if (violation < 25) violation+=2;
                        }else {
                            if (violation > 0) violation--;
                        }
                    }
                }
            }
            if (violation >= 20) {
                alert(user);
            }
            lastDiff = diff;
        }
    }
}
