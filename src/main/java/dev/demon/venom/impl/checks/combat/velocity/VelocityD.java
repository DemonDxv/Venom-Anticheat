package dev.demon.venom.impl.checks.combat.velocity;

import dev.demon.venom.api.check.Check;
import dev.demon.venom.api.check.CheckInfo;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.FlyingEvent;
import org.bukkit.Bukkit;

@CheckInfo(name = "Velocity", type = "D")
public class VelocityD extends Check {
    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof FlyingEvent && user.getConnectedTick() > 250) {
            double deltaY = user.getMovementData().getTo().getY() - user.getMovementData().getFrom().getY();
            double velocityY = user.getVelocityProcessor().getVertical();

            velocityY -= 0.08D;
            velocityY *= 0.9800000190734863D;

            double ratio = Math.abs(deltaY / velocityY);


            if (user.getVelocityData().getVelocityTicks() == 2) {
                if (ratio <= 0.9999 && deltaY <= 0.42F && !user.getMovementData().isClientGround() && user.getMovementData().isLastClientGround()) {
                    alert(user, "VV -> "+ratio + "%");
                }
            }
        }
    }
}
