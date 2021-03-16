package dev.demon.venom.impl.check.impl.speed;

import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.check.Check;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;
import dev.demon.venom.utils.math.MathUtil;
import org.bukkit.Bukkit;

public class Speed extends Check{
    public Speed(String checkname, String checktype, boolean experimental, int banVL, boolean enabled) {
        super(checkname, checktype, experimental, banVL, enabled);
    }

    private double lastDeltaXZ;

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof FlyingInEvent) {

            if (user.generalCancel()
                    || user.getMovementData().lastTeleportTimer.hasNotPassed(20)
                    || user.getMovementData().fallDamageTimer.hasNotPassed(20)
                    || user.getBlockData().lastInsideBlockTimer.hasNotPassed(20)
                    || !user.getMovementData().isChunkLoaded()) {
                violation = 0;
                return;
            }

            double deltaXZ = user.getMovementData().getDeltaXZ();

            double friction = user.getMovementData().getClientGroundTicks() > 4 ? (0.91F * 0.91F) : 0.91F;

            double prediction = lastDeltaXZ * friction;

            prediction += MathUtil.moveFlying
                    (user, user.getMovementData().getTo(), user.getMovementData().isLastClientGround());

            if (!user.getMovementData().isClientGround() && user.getMovementData().isLastClientGround()) {
                prediction += 0.2F;
            }

            if (user.getVelocityProcessor().getLastVelocityHorizontal() != null) {
                prediction += user.getVelocityProcessor().getHorizontalTransaction();
            }

            if (user.getMiscData().getSpeedPotionTicks() > 0) {
                prediction += (user.getMiscData().getSpeedPotionEffectLevel() * 0.062f);
            }

            double speedUp = (deltaXZ - prediction);

            if (speedUp > 0.001 && user.getConnectedTick() > 100) {
                if (deltaXZ > 0.2) {
                    if (++violation > 1) {
                        handleDetection(user, "Speeding -> " + speedUp + "%");
                    }
                } else {
                    violation -= Math.min(violation, 0.001);
                }
            } else {
                violation -= Math.min(violation, 0.001);
            }


            lastDeltaXZ = deltaXZ;
        }
    }
}
