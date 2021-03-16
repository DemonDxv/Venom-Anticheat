package dev.demon.venom.impl.check.impl.timer;

import dev.demon.venom.impl.check.Check;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;
import dev.demon.venom.utils.math.MathUtil;

public class TimerA extends Check {

    public TimerA(String checkname, String checktype, boolean experimental, int banVL, boolean enabled) {
        super(checkname, checktype, experimental, banVL, enabled);
    }

    private Long lastTime;
    private long balance = -100;

    @Override
    public void onHandle(User user, AnticheatEvent e) {

        if (e instanceof FlyingInEvent) {

            if (user.generalCancel()
                    || user.getMovementData().lastTeleportTimer.hasNotPassed(20)
                    || user.getBlockData().pistionTick > 0
                    || user.getBlockData().lastInsideBlockTimer.hasNotPassed(20)
                    || user.getLagProcessor().isLagging()
                    || !user.getMovementData().isChunkLoaded()) {
                balance = -100;
                return;
            }

            long time = System.nanoTime();

            if (this.lastTime != null) {

                long rate = time - lastTime;

                balance += MathUtil.toNanos(50L) - rate;

                if (balance >= MathUtil.toNanos(45)) {

                    if (user.getConnectedTick() > 300) {
                        handleDetection(user);
                    }
                    balance = 0;
                }
            }
            lastTime = time;
        }
    }
}
