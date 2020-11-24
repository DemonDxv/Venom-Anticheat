package dev.demon.venom.impl.checks.combat.velocity;

import dev.demon.venom.api.check.Check;
import dev.demon.venom.api.check.CheckInfo;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.tinyprotocol.api.Packet;
import dev.demon.venom.api.tinyprotocol.packet.in.WrappedInEntityActionPacket;
import dev.demon.venom.api.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.*;
import dev.demon.venom.utils.location.CustomLocation;
import dev.demon.venom.utils.math.MathUtil;
import dev.demon.venom.utils.time.TimeUtils;
import org.bukkit.Bukkit;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@CheckInfo(name = "Velocity", type = "A", banvl = 2)
public class VelocityA extends Check {

    private float getAIMoveSpeed, friction;
    private double lastDeltaXZ, lastVelocity;

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof FlyingInEvent) {


            if (user.getVelocityData().getVelocityTicks() < 5) {

                for (Map.Entry<Double, Short> doubleShortEntry : user.getVelocityProcessor().getLastVelocityHorizontal().entrySet()) {

                    if (user.getMiscData().getTransactionIDVelocity() == doubleShortEntry.getValue()) {
                        user.getVelocityProcessor().setHorizontalTransaction((Double) ((Map.Entry) doubleShortEntry).getKey());
                        user.getVelocityProcessor().getLastVelocityHorizontal().clear();
                    }
                }
            }

            if (user.getBlockData().wallTicks > 0
                    || user.getBlockData().fenceTicks > 0
                    || user.getMovementData().isCollidesHorizontally()
                    || user.generalCancel()
                    || TimeUtils.elapsed(user.getMovementData().getLastTeleport()) < 5000L
                    || TimeUtils.elapsed(user.getMovementData().getLastFallDamage()) < 1000L) {
                return;
            }

            CustomLocation to = user.getMovementData().getTo(), from = user.getMovementData().getFrom();

            double deltaXZ = Math.hypot(to.getX() - from.getX(), to.getZ() - from.getZ());


            double velocity = Math.hypot(user.getVelocityProcessor().velocityX, user.getVelocityProcessor().velocityZ);

            double prediction = velocity;

            int precision = String.valueOf((int) Math.abs(MathUtil.hypot(user.getMovementData().getTo().getX(), user.getMovementData().getTo().getZ()))).length();

            precision = 15 - precision;

            double preD = Double.valueOf("0.5E-" + precision);

            double mx = to.getX() - from.getX();
            double mz = to.getZ() - from.getZ();

            float motionYaw = (float) (Math.atan2(mz, mx) * 180.0D / Math.PI) - 90.0F;

            int direction = 6;

            motionYaw -= user.getMovementData().getTo().getYaw();

            while (motionYaw > 360.0F)
                motionYaw -= 360.0F;
            while (motionYaw < 0.0F)
                motionYaw += 360.0F;

            motionYaw /= 45.0F;

            float moveS = 0.0F;
            float moveF = 0.0F;

            if (Math.abs(Math.abs(mx) + Math.abs(mz)) > preD) {
                direction = (int) new BigDecimal(motionYaw).setScale(1, RoundingMode.HALF_UP).doubleValue();

                if (direction == 1) {
                    moveF = 1F;
                    moveS = -1F;
                } else if (direction == 2) {
                    moveS = -1F;
                } else if (direction == 3) {
                    moveF = -1F;
                    moveS = -1F;
                } else if (direction == 4) {
                    moveF = -1F;
                } else if (direction == 5) {
                    moveF = -1F;
                    moveS = 1F;
                } else if (direction == 6) {
                    moveS = 1F;
                } else if (direction == 7) {
                    moveF = 1F;
                    moveS = 1F;
                } else if (direction == 8) {
                    moveF = 1F;
                } else if (direction == 0) {
                    moveF = 1F;
                }
            }

            float strafe = (moveS * 0.98F), forward = (moveF * 0.98F);

            float f = strafe * strafe + forward * forward;

            float var3 = (0.6F * 0.91F);
            getAIMoveSpeed = 0.1F;

            if (user.getMovementData().isSprinting() || user.getMovementData().isLastSprint()) {
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
                if (user.getVelocityData().getVelocityTicks() == 1) {
                    prediction -= Math.hypot(motionXAdd, motionZAdd);
                } else if (user.getVelocityData().getVelocityTicks() == 2) {
                    prediction -= Math.hypot(motionXAdd, motionZAdd);
                }
            }

            if (user.getMiscData().getSpeedPotionTicks() > 0) {
                prediction -= (user.getMiscData().getSpeedPotionEffectLevel() * 0.03F);
            }


            double fullVelocity = (deltaXZ / prediction);


            if (user.getVelocityData().getVelocityTicks() == 1 && user.getConnectedTick() > 100) {
                if (fullVelocity <= 0.995 && fullVelocity >= 0 && !user.getMovementData().isClientGround() && user.getMovementData().isLastClientGround()) {
                    alert(user, true,"HV -> " + (deltaXZ / prediction) + "%");
                }
                lastVelocity = fullVelocity;
            }

            lastDeltaXZ = deltaXZ;
        }
    }
}
