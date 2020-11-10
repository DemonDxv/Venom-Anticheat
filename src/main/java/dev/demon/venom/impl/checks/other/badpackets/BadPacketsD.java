package dev.demon.venom.impl.checks.other.badpackets;

import dev.demon.venom.api.check.Check;
import dev.demon.venom.api.check.CheckInfo;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;
import dev.demon.venom.utils.time.TimeUtils;


@CheckInfo(name = "BadPackets", type = "D", banvl = 10)
public class BadPacketsD extends Check {

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof FlyingInEvent) {
            double deltaY = user.getMovementData().getTo().getY() - user.getMovementData().getFrom().getY();
            if (user.generalCancel()
                    || TimeUtils.elapsed(user.getMovementData().getLastTeleport()) < 5000L
                    || !user.isSafe()) {
                return;
            }
            if (deltaY != 0 && user.getConnectedTick() > 250) {
                if (deltaY < 0.005 && deltaY > -0.005) {
                    alert(user, false,"DY -> "+deltaY);
                }
            }
        }
    }
}
