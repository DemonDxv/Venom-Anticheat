package dev.demon.venom.impl.checks.movement.fly;

import dev.demon.venom.api.check.Check;
import dev.demon.venom.api.check.CheckInfo;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;
import dev.demon.venom.utils.location.CustomLocation;

@CheckInfo(name = "Fly", type = "E", banvl = 10)
public class FlyE extends Check {
    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof FlyingInEvent) {
            CustomLocation to = user.getMovementData().getTo(), from = user.getMovementData().getFrom();

            double deltaY = to.getY() - from.getY();
            double deltaXZ = Math.hypot(to.getX() - from.getX(), to.getZ() - from.getZ());

            if (user.getMovementData().isExplode() || user.getMovementData().getVelocityTicks() < 10 || user.generalCancel()) {
                return;
            }

            if (deltaY >= 1 || deltaXZ > 1.5) {
                alert(user, false, "Increased posY speed or horizontal speed");
            }
        }
    }
}
