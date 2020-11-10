package dev.demon.venom.impl.checks.old;

import dev.demon.venom.api.check.Check;
import dev.demon.venom.api.check.CheckInfo;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.ArmAnimationEvent;
import dev.demon.venom.impl.events.inevents.BlockDigEvent;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;
import dev.demon.venom.utils.time.TimeUtils;

@CheckInfo(name = "AutoClicker", type = "K", banvl = 100)
public class AutoClickerK extends Check {

    private int movements;
    private int outliers, lastOutliers;
    private long lastSwing;


    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof ArmAnimationEvent || e instanceof BlockDigEvent) {
            if (movements < 10) {
                if (movements > 3 && movements < 6) {
                    outliers++;
                }
                double outlierDiff = Math.abs(outliers - lastOutliers);

                if (outlierDiff == outliers && outliers == 0) {
                    if ((violation += 0.1f) >= 25) {
                        alert(user, true, "O -> "+outliers + " OD -> "+outlierDiff);
                    }
                } else {
                    violation = 0.0;
                }
            }
            lastSwing = System.currentTimeMillis();
            lastOutliers = outliers;
            outliers = movements = 0;
        } else if (e instanceof FlyingInEvent) {
            movements++;
            if (movements > 0 && TimeUtils.elapsed(lastSwing) > 1000L) {
                violation = 0;
            }
        }
    }
}
