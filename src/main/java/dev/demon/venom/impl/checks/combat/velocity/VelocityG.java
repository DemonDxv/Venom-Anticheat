package dev.demon.venom.impl.checks.combat.velocity;

import dev.demon.venom.api.check.Check;
import dev.demon.venom.api.check.CheckInfo;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;
import dev.demon.venom.utils.time.TimeUtils;
import org.bukkit.Bukkit;

@CheckInfo(name = "Velocity", type = "G", banvl = 20)
public class VelocityG extends Check {
    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof FlyingInEvent && user.getConnectedTick() > 250) {

            if (user.generalCancel()
                    || TimeUtils.elapsed(user.getMovementData().getLastTeleport()) < 5000L
                    || TimeUtils.elapsed(user.getMovementData().getLastFallDamage()) < 1000L) {
                return;
            }

            if (user.getVelocityData().getVelocityTicks() > user.getVelocityProcessor().ticksSinceVelocity) {
                if (violation++ > 20) {
                    alert(user, true, "Canceling Transactions");
                }
            } else violation -= Math.min(violation, 0.75);
        }
    }
}
