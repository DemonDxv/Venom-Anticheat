package dev.demon.venom.impl.checks.combat.aimassist;

import dev.demon.venom.api.check.Check;
import dev.demon.venom.api.check.CheckInfo;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;
import dev.demon.venom.utils.math.MathUtil;
import org.bukkit.Bukkit;

@CheckInfo(name = "AimAssist", type = "D", banvl = 10)
public class AimAssistD extends Check {

    private final double offset = Math.pow(2.0, 24.0);
    private double lastDiff;

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof FlyingInEvent) {
            if (((FlyingInEvent) e).isLook() && ((FlyingInEvent) e).isPos()) {
                if (user.isUsingNewOptifine()) {
                    return;
                }

                if ((System.currentTimeMillis() - user.getCombatData().getLastUseEntityPacket() > 2000L)
                        || user.isUsingNewOptifine()
                        || user.getCombatData().cancelTicks > 0) {
                        violation = 0;
                    }

                double diff = Math.abs(user.getMovementData().getTo().getPitch() - user.getMovementData().getFrom().getPitch());

                int gcd = String.valueOf(MathUtil.gcd((long) (diff * offset), (long) (lastDiff * offset))).length();


                if (user.getMovementData().getTo().getYaw() != user.getMovementData().getFrom().getYaw()
                        && user.getMovementData().getTo().getPitch() != user.getMovementData().getFrom().getPitch()) {
                    if (diff > 0 && Math.abs(user.getMovementData().getTo().getPitch()) != 90.0) {
                        if ((System.currentTimeMillis() - user.getCombatData().getLastUseEntityPacket() < 100L)) {
                            if (gcd > 9 || gcd < 5) {
                                alert(user, false, "GCD -> " + gcd);
                            }
                        }
                    }
                }
                lastDiff = diff;
            }
        }
    }
}
