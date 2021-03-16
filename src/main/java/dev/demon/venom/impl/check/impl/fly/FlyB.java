package dev.demon.venom.impl.check.impl.fly;

import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.check.Check;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;

import javax.imageio.plugins.jpeg.JPEGImageReadParam;

public class FlyB extends Check {
    public FlyB(String checkname, String checktype, boolean experimental, int banVL, boolean enabled) {
        super(checkname, checktype, experimental, banVL, enabled);
    }

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof FlyingInEvent) {

            if (user.generalCancel()
                    || user.getMovementData().lastTeleportTimer.hasNotPassed(20)
                    || user.getMovementData().fallDamageTimer.hasNotPassed(20)
                    || user.getBlockData().lastInsideBlockTimer.hasNotPassed(20)
                    || !user.getMovementData().isChunkLoaded()) {
                violation = 0;
                return;
            }

            boolean ground = user.getMovementData().isOnGround(), cground = user.getMovementData().isClientGround();

            if (!ground && cground && user.getConnectedTick() > 100) {
                if (++violation > 1.5) {
                    violation = 1.5;
                    handleDetection(user, "Spoofing Ground.");
                }
            } else {
                violation -= Math.min(violation, 0.05);
            }
        }
    }
}
