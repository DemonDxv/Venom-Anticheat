package dev.demon.venom.impl.checks.other.badpackets;

import dev.demon.venom.api.check.Check;
import dev.demon.venom.api.check.CheckInfo;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.FlyingEvent;


@CheckInfo(name = "BadPackets", type = "D")
public class BadPacketsD extends Check {

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof FlyingEvent) {
            double deltaY = user.getMovementData().getTo().getY() - user.getMovementData().getFrom().getY();
            if (deltaY != 0) {
                if (deltaY < 0.005 && deltaY > -0.005) {
                    alert(user, "DY -> "+deltaY);
                }
            }
        }
    }
}
