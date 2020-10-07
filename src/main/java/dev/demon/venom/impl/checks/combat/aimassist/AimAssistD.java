package dev.demon.venom.impl.checks.combat.aimassist;

import dev.demon.venom.api.check.Check;
import dev.demon.venom.api.check.CheckInfo;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.FlyingEvent;


@CheckInfo(name = "AimAssist", type = "D")
public class AimAssistD extends Check {

    private double lastDeltaPitch;

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof FlyingEvent) {
            double pitchDelta = user.getMovementData().getPitchDelta();
            double yawDelta = user.getMovementData().getYawDelta();

            double pitchDiffDelta = pitchDelta - lastDeltaPitch;

            if (user.isUsingNewOptifine()) {
                return;
            }

            if (yawDelta > 1.5 && pitchDelta <= 0.009 && pitchDelta > 0) {
                alert(user, "PD -> "+pitchDelta + " PDD -> "+pitchDiffDelta);
            }

            lastDeltaPitch = pitchDelta;
        }
    }
}
