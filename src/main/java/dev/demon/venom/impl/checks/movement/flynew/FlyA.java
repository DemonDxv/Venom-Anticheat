package dev.demon.venom.impl.checks.movement.flynew;

import dev.demon.venom.api.checknew.Check;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;
import dev.demon.venom.utils.location.CustomLocation;
import org.bukkit.Bukkit;

public class FlyA extends Check {

    public FlyA(String checkname, String checktype, boolean experimental, int banVL, boolean enabled) {
        super(checkname, checktype, experimental, banVL, enabled);
    }

    private double lastDeltaY;

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof FlyingInEvent) {

            if (user.generalCancel()
                    || user.getBlockData().lastInsideBlockTimer.hasNotPassed(20)
                    || user.getMovementData().getLastTeleportTimer().hasNotPassed(20)
                    || user.getVelocityData().getVelocityTicks() < 20
                    || user.getBlockData().climbableTicks > 0
                    || user.getBlockData().lastClimbableTimer.hasNotPassed(20)
                    || user.getBlockData().liquidTicks > 0
                    || user.getBlockData().pistionTick > 0
                    || !user.getMovementData().isChunkLoaded()) {
                return;
            }

            boolean ground = user.getMovementData().getClientAirTicks() <= 6;

            double deltaY = user.getMovementData().getTo().getY() - user.getMovementData().getFrom().getY();

            double prediction = (lastDeltaY - 0.08D) * 0.9800000190734863D;


            if (!ground) {
                if (Math.abs(deltaY - prediction) > 0.001 && Math.abs(prediction) >= 0.005D) {
                    if (violation++ > 4) {
                        handleDetection(user, "Predicted -> " + Math.abs(deltaY - prediction) + " Ticks -> " + user.getMovementData().getClientAirTicks());
                    }
                } else {
                    violation -= Math.min(violation, 0.25);
                }
            }

            lastDeltaY = deltaY;
        }
    }
}
