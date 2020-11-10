package dev.demon.venom.impl.checks.combat.killaura;

import dev.demon.venom.api.check.Check;
import dev.demon.venom.api.check.CheckInfo;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;
import dev.demon.venom.utils.math.MathUtil;
import org.bukkit.Bukkit;

@CheckInfo(name = "Killaura", type = "E", banvl = 25)
public class KillauraE extends Check {
    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof FlyingInEvent) {
            double pitch = Math.abs(user.getMovementData().getTo().getPitch() - user.getMovementData().getFrom().getPitch());
            double yaw = Math.abs(user.getMovementData().getTo().getYaw() - user.getMovementData().getFrom().getYaw());

            boolean primePitch = MathUtil.isPrime((int) pitch), primeYaw = MathUtil.isPrime((int) yaw);

            if (primePitch && primeYaw) {
                if (violation++ > 3.2) {
                    alert(user, false, "Prime Number Check");
                }
            } else violation -= Math.min(violation, 0.75);
        }
    }
}
