package dev.demon.venom.impl.checks.combat.aimassist;

import dev.demon.venom.api.check.Check;
import dev.demon.venom.api.check.CheckInfo;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.ArmAnimationEvent;
import dev.demon.venom.impl.events.UseEntityEvent;
import dev.demon.venom.utils.location.CustomLocation;


@CheckInfo(name = "AimAssist", type = "B")
public class AimAssistB extends Check {

    private int misses, hits;

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof UseEntityEvent) {
            hits++;
        }

        if (e instanceof ArmAnimationEvent) {
            misses++;

            CustomLocation to = user.getMovementData().getTo(), from = user.getMovementData().getFrom();

            double pitchDelta = Math.abs(to.getPitch() - from.getPitch());
            double yawDelta = Math.abs(to.getYaw() - from.getYaw());

            if (misses >= 100 && yawDelta > 3 && pitchDelta > 0.5) {
                if (hits > 75) {
                    if (violation++ > 1) {
                        alert(user, "H -> " + hits + " M -> " + misses);
                    }
                } else violation -= Math.min(violation, 0.75);
                misses = hits = 0;
            }
        }
    }
}
