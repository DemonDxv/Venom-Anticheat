package dev.demon.venom.impl.check.impl.velocity;

import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.check.Check;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;
import dev.demon.venom.utils.math.MathUtil;
import dev.demon.venom.utils.time.TimeUtils;

public class VelocityE extends Check {

    public VelocityE(String checkname, String checktype, boolean experimental, int banVL, boolean enabled) {
        super(checkname, checktype, experimental, banVL, enabled);
    }

    /** 99% horizontal velocity check **/

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof FlyingInEvent) {

            if (user.generalCancel()
                    || user.getMovementData().getFallDamageTimer().hasNotPassed(20)
                    || user.getMovementData().lastTeleportTimer.hasNotPassed(20)
                    || user.getMovementData().isCollidesHorizontally()) {
                return;
            }

            if (user.getVelocityData().getVelocityTicks() == 1) {
                double deltaXZ = user.getMovementData().getDeltaXZ();

                double velocity = MathUtil.hypot
                        (user.getVelocityProcessor().getVelocityX(), user.getVelocityProcessor().getVelocityZ());

                velocity -= MathUtil.moveFlying
                        (user, user.getMovementData().getTo(), user.getMovementData().isLastClientGround());

                if (user.getMiscData().getSpeedPotionTicks() > 0) {
                    velocity -= (user.getMiscData().getSpeedPotionEffectLevel() * 0.062f);
                }

                double ratio = deltaXZ / velocity;

                if (ratio <= 0.995 && user.getMovementData().isLastClientGround() && user.getConnectedTick() > 100) {
                    handleDetection(user, ratio+"%");
                }
            }
        }
    }
}