package dev.demon.venom.impl.checks.other.badpackets;

import dev.demon.venom.api.check.Check;
import dev.demon.venom.api.check.CheckInfo;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;
import dev.demon.venom.utils.location.CustomLocation;

@CheckInfo(name = "BadPackets", type = "Z", banvl = 100)
public class BadPacketsZ extends Check {

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof FlyingInEvent) {
             if (!((FlyingInEvent) e).isLook() && ((FlyingInEvent) e).isPos()) {
                CustomLocation to = user.getMovementData().getTo(), from = user.getMovementData().getFrom();
                double pitch = Math.abs(to.getPitch() - from.getPitch());
                double yaw = Math.abs(to.getYaw() - from.getYaw());
                if (yaw > 0 || pitch > 0) {
                    alert(user, true, "Moving Head Without Packet [type 3]");
                }
            }
        }
    }
}