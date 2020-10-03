package dev.demon.venom.impl.checksv2.other.badpackets;

import dev.demon.venom.api.check.Check;
import dev.demon.venom.api.check.CheckInfo;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.FlyingEvent;

@CheckInfo(name = "BadPackets", type = "A")
public class BadPacketsA extends Check {
    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof FlyingEvent) {
            double pitch = Math.abs(user.getMovementData().getTo().getPitch());

            if (pitch > 90) {
                alert(user, "P -> " + pitch);
            }
        }
    }
}
