package dev.demon.venom.impl.checks.combat.velocity;

import dev.demon.venom.api.check.Check;
import dev.demon.venom.api.check.CheckInfo;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.FlyingEvent;
import org.bukkit.Bukkit;

@CheckInfo(name = "Velocity", type = "C")
public class VelocityC extends Check {
    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof FlyingEvent && user.getConnectedTick() > 250) {
            double deltaY = user.getMovementData().getTo().getY() - user.getMovementData().getFrom().getY();

            double ratio = Math.abs(deltaY / user.getVelocityProcessor().getVertical());

            if (ratio == 0.01292731973570811) {
                return;
            }

            if (user.getVelocityData().getVelocityTicks() == 1) {
                if (ratio <= 0.9999 && deltaY <= 0.42F && !user.getMovementData().isClientGround() && user.getMovementData().isLastClientGround()) {
                    alert(user, "VV -> "+ratio + "%");
                }
            }
        }
    }
}
