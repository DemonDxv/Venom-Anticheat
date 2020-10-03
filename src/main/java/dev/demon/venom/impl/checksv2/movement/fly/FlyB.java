package dev.demon.venom.impl.checksv2.movement.fly;

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
        if (e instanceof FlyingEvent && user.getConnectedTick() > 100) {
            if (user.generalCancel()
                    || TimeUtils.elapsed(user.getMovementData().getLastTeleport()) < 1000L) {
                return;
            }

            double deltaY = user.getMovementData().getTo().getY() - user.getMovementData().getFrom().getY();

            if (deltaY < -0.08 && user.getMovementData().isLastClientGround()) {
                alert(user, "DY -> " + deltaY + " CG -> " + user.getMovementData().isLastClientGround());
            }
        }
    }
}