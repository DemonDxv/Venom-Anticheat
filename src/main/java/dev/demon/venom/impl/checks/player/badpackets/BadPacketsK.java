package dev.demon.venom.impl.checks.player.badpackets;

import dev.demon.venom.api.checknew.Check;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;
import org.bukkit.Bukkit;

public class BadPacketsK extends Check {
    public BadPacketsK(String checkname, String checktype, boolean experimental, int banVL, boolean enabled) {
        super(checkname, checktype, experimental, banVL, enabled);
    }

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof FlyingInEvent) {

            if (user.generalCancel()
                    || user.getLagProcessor().isLagging()
                    || !user.getMovementData().isChunkLoaded()
                    || user.getMovementData().getLastTeleportTimer().hasNotPassed(20)
                    || user.getVelocityData().getVelocityTicks() < 20
                    || user.getBlockData().stairTicks > 0
                    || user.getBlockData().slabTicks > 0
                    || user.getBlockData().bedTicks > 0
                    || user.getBlockData().fenceTicks > 0
                    || user.getBlockData().wallTicks > 0
                    || user.getBlockData().halfBlockTicks > 0
                    || user.getBlockData().pistionTick > 0
                    || user.getBlockData().lastInsideBlockTimer.hasNotPassed(20)) {
                return;
            }

            double deltaY = user.getMovementData().getTo().getY() - user.getMovementData().getFrom().getY();

            if (deltaY > 0.0 && user.getMovementData().isClientGround() && user.getConnectedTick() > 100) {
                if (++violation > 1.0) {
                    handleDetection(user, "Change in deltaY while on ground.");
                }
            } else violation -= Math.min(violation, 0.03);
        }
    }
}
