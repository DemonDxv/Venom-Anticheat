package dev.demon.venom.impl.checksv2.combat.killaura;

import dev.demon.venom.api.check.Check;
import dev.demon.venom.api.check.CheckInfo;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.UseEntityEvent;
import dev.demon.venom.utils.location.CustomLocation;


@CheckInfo(name = "Killaura", type = "B")
public class KillauraB extends Check {

    private double lastDeltaXZ;

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof UseEntityEvent) {

            CustomLocation to = user.getMovementData().getTo(), from = user.getMovementData().getFrom();
            double deltaXZ = Math.hypot(to.getX() - from.getX(), to.getZ() - from.getZ());

            double differenceXZ = Math.abs(deltaXZ - lastDeltaXZ);

            if (user.getMovementData().isSprinting() && differenceXZ <= 0.027) {
                if (violation++ > 4) {
                    alert(user);
                }
            } else {
                violation = 0;
            }

            lastDeltaXZ = deltaXZ;
        }
    }
}
