package dev.demon.venom.impl.checks.combat.killaura;

import dev.demon.venom.api.check.Check;
import dev.demon.venom.api.check.CheckInfo;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.UseEntityEvent;
import dev.demon.venom.utils.location.CustomLocation;
import dev.demon.venom.utils.math.Verbose;


@CheckInfo(name = "Killaura", type = "B", banvl = 10)
public class KillauraB extends Check {

    private double lastDeltaXZ;
    private Verbose verbose = new Verbose();

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof UseEntityEvent) {

            CustomLocation to = user.getMovementData().getTo(), from = user.getMovementData().getFrom();
            double deltaXZ = Math.hypot(to.getX() - from.getX(), to.getZ() - from.getZ());

            double differenceXZ = Math.abs(deltaXZ - lastDeltaXZ);

            if (user.getMovementData().isSprinting() && deltaXZ > 0.26) {
                if (verbose.flag(7, 1000L)) {
                    alert(user,false, "Keep Sprint");
                }
            }

            lastDeltaXZ = deltaXZ;
        }
    }
}
