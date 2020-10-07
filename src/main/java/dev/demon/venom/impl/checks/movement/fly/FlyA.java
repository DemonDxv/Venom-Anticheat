package dev.demon.venom.impl.checks.movement.fly;

import dev.demon.venom.api.check.Check;
import dev.demon.venom.api.check.CheckInfo;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.FlyingEvent;
import org.bukkit.Bukkit;


@CheckInfo(name = "Fly", type = "A")
public class FlyA extends Check {

    private double lastDeltaY;

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof FlyingEvent && user.getConnectedTick() > 250) {
            double deltaY = user.getMovementData().getTo().getY() - user.getMovementData().getFrom().getY();

            if (user.getVelocityData().getVelocityTicks() <= 20) {
                return;
            }

            if (user.getBlockData().liquidTicks > 0 || user.getBlockData().climbableTicks > 0) {
                return;
            }

            if (!user.getMovementData().isClientGround() && !user.getMovementData().isLastClientGround()) {
                if (deltaY > (lastDeltaY + 0.001)) {
                    alert(user, "DY -> " + deltaY + " LDY -> "+lastDeltaY);
                }
            }
            lastDeltaY = (deltaY - 0.08D) * 0.9800000190734863D;
        }
    }
}
