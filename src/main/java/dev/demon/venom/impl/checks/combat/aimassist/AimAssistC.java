package dev.demon.venom.impl.checks.combat.aimassist;

import dev.demon.venom.api.check.Check;
import dev.demon.venom.api.check.CheckInfo;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.FlyingEvent;
import org.bukkit.Bukkit;


@CheckInfo(name = "AimAssist", type = "C")
public class AimAssistC extends Check {

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

            if (yawDelta > 1.5 && pitchDiffDelta <= 0.03 && pitchDelta <= 0.03 && pitchDelta > 0 && pitchDiffDelta > 0) {
                if (violation++ > 3) {
                    alert(user, "PD -> " + pitchDelta + " PDD -> " + pitchDiffDelta);
                }
            } else violation -= Math.min(violation, 0.1);

            lastDeltaPitch = pitchDelta;
        }
    }
}
