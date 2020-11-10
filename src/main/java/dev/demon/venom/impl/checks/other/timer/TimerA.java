package dev.demon.venom.impl.checks.other.timer;

import dev.demon.venom.api.check.Check;
import dev.demon.venom.api.check.CheckInfo;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;
import dev.demon.venom.utils.math.RollingAverageDouble;
import dev.demon.venom.utils.time.TimeUtils;


@CheckInfo(name = "Timer", type = "A", banvl = 10)
public class TimerA extends Check {
    private long lastTimerMove, timerCheck;
    private RollingAverageDouble timerRate = new RollingAverageDouble(40, 50.0);

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof FlyingInEvent) {

            if ((user.generalCancel() || TimeUtils.elapsed(user.getMovementData().getLastTeleport()) < 1000L)) {
                violation = 0;
                return;
            }

            long now = System.currentTimeMillis();

            double diff = now - lastTimerMove;

            timerRate.add(diff);

            if (now - timerCheck >= 1000L) {
                timerCheck = now;

                double timerSpeed = 50.0 / timerRate.getAverage();

                double max = TimeUtils.elapsed(user.getMovementData().getLastEnderpearl()) < 1000L ? 1.04 : 1.01;

                if (timerSpeed >= max) {
                    if (violation++ > 3) {
                        alert(user, false,"TS -> " + timerSpeed);
                    }
                } else violation -= Math.min(violation, 0.5);
            }
            lastTimerMove = now;
        }
    }
}
