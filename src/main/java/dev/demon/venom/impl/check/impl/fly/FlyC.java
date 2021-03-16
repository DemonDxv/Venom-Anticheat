package dev.demon.venom.impl.check.impl.fly;

import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.check.Check;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;

public class FlyC extends Check {
    public FlyC(String checkname, String checktype, boolean experimental, int banVL, boolean enabled) {
        super(checkname, checktype, experimental, banVL, enabled);
    }

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof FlyingInEvent) {

            if (user.generalCancel()
                    || user.getVelocityData().getVelocityTicks() < 20
                    || user.getMovementData().lastTeleportTimer.hasNotPassed(20)
                    || user.getMovementData().fallDamageTimer.hasNotPassed(20)
                    || user.getBlockData().lastInsideBlockTimer.hasNotPassed(20)
                    || user.getBlockData().blockAboveTicks > 0
                    || !user.getMovementData().isChunkLoaded()) {
                violation = 0;
                return;
            }

            double deltaY = user.getMovementData().getTo().getY() - user.getMovementData().getFrom().getY();

            double max = 0.42f + (user.getMiscData().getJumpPotionMultiplyer() * 0.2);

            if (deltaY < max && !user.getMovementData().isClientGround() && deltaY >= 0.0
                    && user.getMovementData().isLastClientGround() && user.getConnectedTick() > 100) {

                handleDetection(user, "Predicted Jump Invalid, DY=" + deltaY + " M=" + max);
            }
        }
    }
}
