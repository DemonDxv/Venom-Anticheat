package dev.demon.venom.impl.check.impl.badpackets;

import dev.demon.venom.impl.check.Check;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;
import dev.demon.venom.utils.time.TimeUtils;

public class BadPacketsG extends Check {
    public BadPacketsG(String checkname, String checktype, boolean experimental, int banVL, boolean enabled) {
        super(checkname, checktype, experimental, banVL, enabled);
    }

    private double lastY;

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof FlyingInEvent) {

            if (((FlyingInEvent) e).isPos() && ((FlyingInEvent) e).isLook()) {

                double packetY = Math.abs(((FlyingInEvent) e).getY() - lastY);
                double serverY = Math.abs(user.getMovementData().getTo().getY() - user.getMovementData().getFrom().getY());

                if (TimeUtils.elapsed(user.getMovementData().getLastTeleport()) < 1000L
                        || TimeUtils.elapsed(user.getMiscData().getLastBlockBreakCancel()) < 1000L
                        || user.getBlockData().climbableTicks > 0
                        || user.getBlockData().blockAboveTicks > 0
                        || user.getBlockData().stairTicks > 0
                        || user.getBlockData().slabTicks > 0
                        || user.getBlockData().bedTicks > 0
                        || user.getMovementData().lastTeleportTimer.hasNotPassed(20)
                        || user.getBlockData().pistionTick > 0
                        || user.getBlockData().lastInsideBlockTimer.hasNotPassed(20)
                        || TimeUtils.elapsed(user.getMovementData().getLastTeleportInBlock()) < 1000L) {
                    return;
                }



                if (Math.abs(packetY - serverY) >= 0 && !user.getMovementData().isClientGround() && user.getMovementData().isLastClientGround() && user.getConnectedTick() > 100) {
                    if (violation++ > 2) {
                        handleDetection(user,  "Invalid DeltaY");
                    }
                } else violation -= Math.min(violation, 0.15);


                lastY = ((FlyingInEvent) e).getY();
            }
        }
    }
}
