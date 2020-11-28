package dev.demon.venom.impl.checks.movement.speed;

import dev.demon.venom.api.check.Check;
import dev.demon.venom.api.check.CheckInfo;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;
import dev.demon.venom.utils.location.CustomLocation;
import dev.demon.venom.utils.time.TimeUtils;
import org.bukkit.Bukkit;

@CheckInfo(name = "Speed", type = "A", banvl = 10)
public class SpeedA extends Check {

    private double lastDeltaXZ;
    private float friction;
    private double jumpFactor;

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof FlyingInEvent) {
            if (TimeUtils.elapsed(user.getMiscData().getLastBlockBreakCancel()) < 1000L
                    || TimeUtils.elapsed(user.getMovementData().getLastTeleport()) < 5000L
                    || user.generalCancel()
                    || user.getBlockData().climbableTicks > 0
                    || user.getBlockData().liquidTicks > 0
                    || TimeUtils.elapsed(user.getMiscData().getLastBlockCancel()) < 1000L
                    || user.getMiscData().isNearBoat()
                    || TimeUtils.elapsed(user.getMovementData().getLastTeleportInBlock()) < 1000L) {
                return;
            }
            CustomLocation to = user.getMovementData().getTo(), from = user.getMovementData().getFrom();

            double deltaXZ = Math.hypot(to.getX() - from.getX(), to.getZ() - from.getZ());

            friction = 0.91F;
            jumpFactor = 0.02F;

            if (user.getMovementData().isSprinting() || user.getMovementData().isLastSprint()) {
                jumpFactor = 0.026F;
            }

            double prediction = lastDeltaXZ * friction + jumpFactor;

            if (user.getVelocityData().getVelocityTicks() < 20) {
                prediction += user.getVelocityProcessor().getHorizontalTransaction();
            }

            if (user.getBlockData().pistionTick > 0) {
                prediction += 0.8;
            }


            double difference = deltaXZ - prediction;

            if (!user.getMovementData().isClientGround() && !user.getMovementData().isLastClientGround() && user.getConnectedTick() > 100) {
                if (difference > 0) {
                    if (violation++ > 2) {
                        alert(user, false, "Difference -> " + difference);
                    }
                } else {
                    violation -= Math.min(violation, 0.25);
                }
            }
            lastDeltaXZ = deltaXZ;
        }
    }
}
