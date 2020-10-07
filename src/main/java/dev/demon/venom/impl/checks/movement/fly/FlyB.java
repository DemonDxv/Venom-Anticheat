package dev.demon.venom.impl.checks.movement.fly;

import dev.demon.venom.api.check.Check;
import dev.demon.venom.api.check.CheckInfo;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.FlyingEvent;
import dev.demon.venom.utils.time.TimeUtils;


@CheckInfo(name = "Fly", type = "B")
public class FlyB extends Check {

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof FlyingEvent && user.getConnectedTick() > 250) {
            if (user.generalCancel()
                    || TimeUtils.elapsed(user.getMovementData().getLastTeleport()) < 1000L) {
                return;
            }

            double deltaY = Math.abs(user.getMovementData().getTo().getY() - user.getMovementData().getFrom().getY());

            if (deltaY <= 0 && user.getMovementData().isLastClientGround() && !user.getMovementData().isOnGround()) {
                alert(user, "DY -> " + deltaY + " LCG -> " + user.getMovementData().isLastClientGround());
            }
        }
    }
}