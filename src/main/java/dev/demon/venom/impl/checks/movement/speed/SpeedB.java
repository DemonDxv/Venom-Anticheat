package dev.demon.venom.impl.checks.movement.speed;

import dev.demon.venom.api.check.Check;
import dev.demon.venom.api.check.CheckInfo;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;
import dev.demon.venom.utils.location.CustomLocation;
import dev.demon.venom.utils.math.MathUtil;
import dev.demon.venom.utils.time.TimeUtils;
import org.bukkit.Bukkit;

import java.text.DecimalFormat;


@CheckInfo(name = "Speed", type = "B", banvl = 10)
public class SpeedB extends Check {

    private double lastDeltaXZ;
    private float friction, getAIMoveSpeed;
    private float friction2 = 0.91F;

    /** Detecting speeds via Air Friction change, and Ground Friction change **/

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof FlyingInEvent) {

            if (TimeUtils.elapsed(user.getMiscData().getLastBlockBreakCancel()) < 1000L
                    || TimeUtils.elapsed(user.getMovementData().getLastTeleport()) < 5000L
                    || user.generalCancel()
                    || user.getBlockData().liquidTicks > 0
                    || TimeUtils.elapsed(user.getMiscData().getLastBlockCancel()) < 1000L
                    || user.getMiscData().isNearBoat()
                    || TimeUtils.elapsed(user.getMovementData().getLastTeleportInBlock()) < 1000L) {
                return;
            }

            CustomLocation to = user.getMovementData().getTo(), from = user.getMovementData().getFrom();
            double deltaXZ = Math.hypot(to.getX() - from.getX(), to.getZ() - from.getZ());

            double prediction = lastDeltaXZ * 0.91F;

            friction2 = 0.91F;

            if (user.getMovementData().getClientGroundTicks() > 4) {
                prediction *= 0.6F;
            }

            if (user.getBlockData().iceTicks > 0) {
                friction2 = 0.6F;
            }

            if (!user.getMovementData().isClientGround() && user.getMovementData().isLastClientGround()) {
                prediction += 0.2F;
            }

            if (user.getVelocityData().getVelocityTicks() <= 20) {
                prediction += user.getVelocityProcessor().getHorizontal();
            }

            if (user.getBlockData().liquidTicks > 0) {
                prediction += 0.014D;
            }

            float strafe = 0.98F, forward = 0.98F;
            float f = strafe * strafe + forward * forward;

            float var3 = (0.6F * friction2);
            getAIMoveSpeed = 0.1F;

            if (user.getMovementData().isSprinting()) {
                getAIMoveSpeed = 0.13000001F;
            }

            float var4 = 0.16277136F / (var3 * var3 * var3);

            if (user.getMovementData().isLastClientGround()) {
                friction = getAIMoveSpeed * var4;
            } else {
                friction = 0.026F;
            }

            if (f >= 1.0E-4F) {
                f = (float) Math.sqrt(f);
                if (f < 1.0F) {
                    f = 1.0F;
                }
                f = friction / f;
                strafe = strafe * f;
                forward = forward * f;
                float f1 = (float) Math.sin(to.getYaw() * (float) Math.PI / 180.0F);
                float f2 = (float) Math.cos(to.getYaw() * (float) Math.PI / 180.0F);
                float motionXAdd = (strafe * f2 - forward * f1);
                float motionZAdd = (forward * f2 + strafe * f1);
                prediction += Math.hypot(motionXAdd, motionZAdd);
            }

            double difference = (deltaXZ - prediction) / friction;
            difference -= 0.001;

            difference -= (user.getMiscData().getSpeedPotionEffectLevel() * 0.2);

            DecimalFormat df2 = new DecimalFormat("0.00");

            if (user.getMovementData().isClientGround()) {
                if (difference > 0 && user.getConnectedTick() > 250) {
                    if (violation++ > 2) {
                        alert(user, false, "Difference -> " + difference);
                    }
                } else {
                    violation -= Math.min(violation, 0.25);
                }
            }

            lastDeltaXZ = Math.max(deltaXZ, 0.1);
        }
    }
}