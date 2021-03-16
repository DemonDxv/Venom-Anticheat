package dev.demon.venom.impl.check.impl.fly;

import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.check.Check;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;

public class FlyA extends Check {
    public FlyA(String checkname, String checktype, boolean experimental, int banVL, boolean enabled) {
        super(checkname, checktype, experimental, banVL, enabled);
    }

    private Double lastDeltaY;

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof FlyingInEvent) {
            double deltaY = user.getMovementData().getTo().getY() - user.getMovementData().getFrom().getY();

            if (user.generalCancel()
                    || user.getVelocityData().getVelocityTicks() < 20
                    || user.getMovementData().lastTeleportTimer.hasNotPassed(20)
                    || user.getMovementData().fallDamageTimer.hasNotPassed(20)
                    || user.getBlockData().lastInsideBlockTimer.hasNotPassed(20)
                    || !user.getMovementData().isChunkLoaded()) {
                violation = 0;
                return;
            }

            if (lastDeltaY != null) {
                if (!user.getMovementData().isClientGround() && !user.getMovementData().isLastClientGround()) {
                    if ((deltaY - lastDeltaY) > 0.005 && user.getConnectedTick() > 100) {
                        if (++violation > 7) {
                            handleDetection(user, "Invalid Gravity");
                        }
                    } else {
                        violation -= Math.min(violation, 0.15);
                    }
                } else {
                    violation -= Math.min(violation, 0.15);
                }
            }
            lastDeltaY = (deltaY - 0.08D) * 0.98F;
        }
    }
}
