package dev.demon.venom.impl.checks.movement.fly;

import dev.demon.venom.api.check.Check;
import dev.demon.venom.api.check.CheckInfo;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;
import dev.demon.venom.utils.time.TimeUtils;
import org.bukkit.Bukkit;

@CheckInfo(name = "Fly", type = "C", banvl = 10)
public class FlyC extends Check {

    private double lastDeltaY;

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof FlyingInEvent) {

            if (TimeUtils.elapsed(user.getMiscData().getLastBlockBreakCancel()) < 1000L
                    || TimeUtils.elapsed(user.getMovementData().getLastTeleport()) < 5000L
                    || user.generalCancel()
                    || user.getBlockData().liquidTicks > 0
                    || TimeUtils.elapsed(user.getMiscData().getLastBlockCancel()) < 1000L
                    || user.getMiscData().isNearBoat()
                    || user.getBlockData().climbableTicks > 0
                    || user.getMiscData().getMountedTicks() > 0
                    || user.getBlockData().doorTicks > 0
                    || TimeUtils.elapsed(user.getMiscData().getLastEjectVechielEject()) < 2000L
                    || user.getBlockData().webTicks > 0
                    || user.getBlockData().bedTicks > 0
                    || TimeUtils.elapsed(user.getMovementData().getLastTeleportInBlock()) < 2000L
                    || TimeUtils.elapsed(user.getMovementData().getLastExplode()) < 1000L) {

                return;
            }

            double deltaY = user.getMovementData().getTo().getY() - user.getMovementData().getFrom().getY();

            double prediction = (lastDeltaY - 0.08D) * 0.9800000190734863D;

            if (user.getBlockData().doorTicks > 0) {
                prediction += 1;
            }

            if (user.getVelocityData().getVelocityTicks() < 20 || user.getBlockData().redstoneTick > 0 || user.getBlockData().pistionTick > 0) {
                prediction += 1;
            }


            if (!user.getMovementData().isClientGround() && !user.getMovementData().isLastClientGround()) {
                if ((deltaY - prediction) > 0.002 && user.getConnectedTick() > 150) {
                    if (violation++ > 2) {
                        alert(user, false, "Prediction -> " + (deltaY - prediction));
                    }
                } else violation -= Math.min(violation, 0.1);
            }

            lastDeltaY = deltaY;
        }
    }
}
