package dev.demon.venom.impl.check.impl.velocity;

import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.check.Check;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;
import dev.demon.venom.utils.time.TimeUtils;

public class VelocityD extends Check {

    public VelocityD(String checkname, String checktype, boolean experimental, int banVL, boolean enabled) {
        super(checkname, checktype, experimental, banVL, enabled);
    }

    /** 99% vertical velocity check (second tick) **/

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof FlyingInEvent) {

            if (user.generalCancel()
                    || user.getMovementData().getFallDamageTimer().hasNotPassed(20)
                    || user.getMovementData().lastTeleportTimer.hasNotPassed(20)
                    || user.getBlockData().blockAboveTicks > 0) {
                return;
            }

            if (user.getVelocityData().getVelocityTicks() == 2) {
                double deltaY = user.getMovementData().getTo().getY();

                double verticalVelocity = user.getVelocityProcessor().getVerticalTransaction();

                double predict = (verticalVelocity - 0.08) * 0.98F;

                double ratio = Math.abs(deltaY - predict);

                if (ratio <= 0.995 && verticalVelocity < 5 && user.getConnectedTick() > 100) {
                    handleDetection(user, ratio+"%");
                }
            }
        }
    }
}