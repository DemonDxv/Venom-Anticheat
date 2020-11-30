package dev.demon.venom.impl.checks.movement.fly;

import dev.demon.venom.api.check.Check;
import dev.demon.venom.api.check.CheckInfo;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;
import dev.demon.venom.utils.location.CustomLocation;
import dev.demon.venom.utils.time.TimeUtils;
import org.bukkit.Bukkit;

@CheckInfo(name = "Fly", type = "A", banvl = 10)
public class FlyA extends Check {

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof FlyingInEvent) {

            if (user.generalCancel()
                    || user.getBlockData().blockAboveTicks > 0
                    || user.getVelocityData().getVelocityTicks() < 20
                    || !user.isSafe()
                    || TimeUtils.elapsed(user.getMovementData().getLastTeleport()) < 1000L
                    || TimeUtils.elapsed(user.getMiscData().getLastBlockCancel()) < 1000L
                    || TimeUtils.elapsed(user.getMiscData().getLastBlockBreakCancel()) < 1000L
                    || user.getMiscData().isNearBoat()
                    || user.getBlockData().climbableTicks > 0
                    || user.getBlockData().liquidTicks > 0
                    || user.getBlockData().doorTicks > 0
                    || user.getBlockData().bedTicks > 0
                    || user.getMiscData().getMountedTicks() > 0
                    || TimeUtils.elapsed(user.getMiscData().getLastEjectVechielEject()) < 2000L
                    || user.getBlockData().webTicks > 0
                    || TimeUtils.elapsed(user.getCombatData().getLastFireDamage()) < 1000L
                    || TimeUtils.elapsed(user.getCombatData().getLastPoisonDamage()) < 1000L
                    || TimeUtils.elapsed(user.getMovementData().getLastTeleportInBlock()) < 2000L
                    || TimeUtils.elapsed(user.getCombatData().getLastRespawn()) < 2000L) {
                return;
            }


            double deltaY = user.getMovementData().getTo().getY() - user.getMovementData().getFrom().getY();

            double max = 0.42f + user.getMiscData().getJumpPotionMultiplyer() * 0.2;

            if (user.getBlockData().wallTicks > 0 || user.getBlockData().slabTicks > 0
                    || user.getBlockData().fenceTicks > 0 || user.getBlockData().halfBlockTicks > 0
                    || user.getBlockData().stairTicks > 0) {
                max = 0.5;
            }
            if (user.getBlockData().bedTicks > 0) {
                max = 0.5625;
            }

            if (deltaY <= (0.40444491418477924 + 1.0E-9F) && deltaY >= (0.40444491418477924 - 1.0E-9F)) {
                return;
            }

            if (user.getBlockData().pistionTick > 0) {
                max = 1;
            }

            if (deltaY > 0) {
                if (deltaY > max || deltaY < max && max < 0.5) {
                    if (!user.getMovementData().isClientGround() && user.getMovementData().isLastClientGround() && user.getConnectedTick() > 250) {
                        alert(user, false, "DY -> " + deltaY);
                    }
                }
            }
        }
    }
}
