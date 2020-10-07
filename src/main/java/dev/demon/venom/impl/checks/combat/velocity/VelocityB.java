package dev.demon.venom.impl.checks.combat.velocity;

import dev.demon.venom.api.check.Check;
import dev.demon.venom.api.check.CheckInfo;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.FlyingEvent;
import dev.demon.venom.impl.events.UseEntityEvent;
import dev.demon.venom.utils.location.CustomLocation;
import dev.demon.venom.utils.math.MathUtil;
import org.bukkit.Bukkit;

import java.math.BigDecimal;
import java.math.RoundingMode;

@CheckInfo(name = "Velocity", type = "B")
public class VelocityB extends Check {

    private float getAIMoveSpeed, friction;
    private double lastDeltaXZ;

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof FlyingEvent && user.getConnectedTick() > 250) {

            if (user.getBlockData().wallTicks > 0
                    || user.getBlockData().fenceTicks > 0
                    || user.getMovementData().isCollidesHorizontally()) {
                return;
            }

            CustomLocation to = user.getMovementData().getTo(), from = user.getMovementData().getFrom();

            double deltaXZ = Math.hypot(to.getX() - from.getX(), to.getZ() - from.getZ());

            double velocity = MathUtil.hypot(user.getVelocityProcessor().getVelocityX(), user.getVelocityProcessor().getVelocityZ());

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
                prediction -= Math.hypot(motionXAdd, motionZAdd);
            }

            double horizontal = (deltaXZ / (prediction - lastDeltaXZ)) - 0.13000001F - 0.026F;


            if (user.getVelocityData().getVelocityTicks() == 2) {
                if (horizontal < 1 && horizontal >= 0) {
                    alert(user, "HV -> " + horizontal + "%");
                }
            }
            lastDeltaXZ = prediction;
        }
    }
}
