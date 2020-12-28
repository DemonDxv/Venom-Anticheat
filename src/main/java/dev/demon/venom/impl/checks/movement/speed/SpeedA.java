package dev.demon.venom.impl.checks.movement.speed;

import dev.demon.venom.Venom;
import dev.demon.venom.api.checknew.Check;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;
import dev.demon.venom.utils.location.CustomLocation;
import dev.demon.venom.utils.math.MathUtil;
import dev.demon.venom.utils.time.TimeUtils;
import dev.demon.venom.utils.version.VersionUtil;
import org.bukkit.Bukkit;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class SpeedA extends Check {

    public SpeedA(String checkname, String checktype, boolean experimental, int banVL, boolean enabled) {
        super(checkname, checktype, experimental, banVL, enabled);
    }

    private double lastDeltaXZ;
    private float friction, jumpFactor;

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof FlyingInEvent) {

            if (user.getBlockData().lastInsideBlockTimer.hasNotPassed(20)
                    || user.generalCancel()
                    || TimeUtils.elapsed(user.getMiscData().getLastBlockBreakCancel()) < 1000L
                    || TimeUtils.elapsed(user.getMiscData().getLastBlockPlaceTick()) < 1000L
                    || user.getMiscData().isNearBoat()
                    || !user.isSafe()
                    || user.getBlockData().webTicks > 0
                    || user.getMovementData().getLastTeleportTimer().hasNotPassed(20)
                    || !user.getMovementData().isChunkLoaded()
                    || user.getBlockData().liquidTicks > 0
                    || TimeUtils.elapsed(user.getCombatData().getLastRespawn()) < 1000L
                    || user.getMiscData().getMountedTicks() > 0
                    || TimeUtils.elapsed(user.getMiscData().getLastEjectVechielEject()) < 2000L
                    || user.getBlockData().climbableTicks > 0
                    || user.getBlockData().lastClimbableTimer.hasNotPassed(20)) {
                return;
            }

            CustomLocation to = user.getMovementData().getTo(), from = user.getMovementData().getFrom();

            double deltaXZ = Math.hypot(to.getX() - from.getX(), to.getZ() - from.getZ());

            friction = 0.91F;
            jumpFactor = 0.02F;

            if (user.getMovementData().isSprinting() || user.getMovementData().isLastSprint()) {
                jumpFactor = 0.026F;
            }

            if (user.getBlockData().iceTicks > 0) {
                friction = 0.98F;
            }

            double prediction = lastDeltaXZ * friction + jumpFactor;

            if (user.getVelocityData().getVelocityTicks() < 20) {
                prediction += user.getVelocityProcessor().getHorizontalTransaction() + (user.getCombatData().getLastBowStrength() * 0.2);
            }

            if (user.getBlockData().webTicks > 0) {
                prediction += 0.4F;
            }

            if (user.getBlockData().pistionTick > 0) {
                prediction += 0.8;
            }

            if (user.getBlockData().redstoneTick > 0 || user.getBlockData().carpetTick > 0 || user.getBlockData().presurePlateTicks > 0) {
                prediction += 0.1;
            }

            if (user.getBlockData().doorTicks > 0) {
                prediction += 0.5;
            }


            double difference = deltaXZ - prediction;

            if (!user.getMovementData().isClientGround() && !user.getMovementData().isLastClientGround() && user.getConnectedTick() > 100) {
                if (difference > 0) {
                    if (violation++ > 1) {
                        handleDetection(user, "Speed Difference -> " + difference);
                    }
                } else {
                    violation -= Math.min(violation, 0.05);
                }
            }
            lastDeltaXZ = deltaXZ;
        }
    }
}