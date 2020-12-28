package dev.demon.venom.impl.checks.movement.flynew;

import dev.demon.venom.api.checknew.Check;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;
import dev.demon.venom.utils.location.CustomLocation;
import org.bukkit.Bukkit;

public class FlyB extends Check {

    public FlyB(String checkname, String checktype, boolean experimental, int banVL, boolean enabled) {
        super(checkname, checktype, experimental, banVL, enabled);
    }



    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof FlyingInEvent) {

            if (user.generalCancel()
                    || !user.getMovementData().isChunkLoaded()
                    || user.getMovementData().getLastTeleportTimer().hasNotPassed(20)
                    || user.getVelocityData().getVelocityTicks() < 20
                    || user.getBlockData().stairTicks > 0
                    || user.getBlockData().slabTicks > 0
                    || user.getBlockData().halfBlockTicks > 0
                    || user.getBlockData().pistionTick > 0
                    || user.getBlockData().lastInsideBlockTimer.hasNotPassed(20)) {
                return;
            }

            CustomLocation to = user.getMovementData().getTo();

            boolean clientGround = user.getMovementData().isClientGround();
            boolean serverGround = user.getMovementData().isOnGround();

            int clientGroundTicks = user.getMovementData().getClientGroundTicks();
            int groundTicks = user.getMovementData().getGroundTicks();

            if (clientGround != serverGround) {
                if (clientGroundTicks > groundTicks) {
                    if (violation++ > 2) {
                        handleDetection(user, "Spoofing Ground State.");
                    }
                } else {
                    violation -= Math.min(violation, 0.2);
                }
            } else {
                violation -= Math.min(violation, 0.2);
            }
        }
    }
}
