package dev.demon.venom.impl.checks.combat.aimassist;

import dev.demon.venom.api.check.Check;
import dev.demon.venom.api.check.CheckInfo;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;
import dev.demon.venom.utils.math.MathUtil;

@CheckInfo(name = "AimAssist", type = "B", banvl = 10)
public class AimAssistB extends Check {
    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof FlyingInEvent) {
            double deltaPitch = user.getMovementData().getPitchDelta();

            if (deltaPitch > 0) {
                if (deltaPitch % 0.1 == 0.0) {
                    alert(user, false, "DP -> " + deltaPitch);
                }
                if (deltaPitch == Math.round(deltaPitch)) {
                    alert(user, false, "DP -> "+ deltaPitch + " [1]");
                }
                if (deltaPitch == MathUtil.round(deltaPitch, 1)) {
                    alert(user, false, "DP -> "+ deltaPitch + " [2]");
                }
            }
        }
    }
}
