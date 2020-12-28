package dev.demon.venom.impl.checks.combat.autoclicker;

import dev.demon.venom.api.checknew.Check;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.ArmAnimationEvent;
import dev.demon.venom.impl.events.inevents.BlockDigEvent;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;

import java.util.ArrayList;
import java.util.List;

public class AutoClickerG extends Check {

    public AutoClickerG(String checkname, String checktype, boolean experimental, int banVL, boolean enabled) {
        super(checkname, checktype, experimental, banVL, enabled);
    }

    private int movements;
    private int outliers, lastOutliers;

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof ArmAnimationEvent || e instanceof BlockDigEvent) {
            if (movements < 10) {
                if (movements > 3 && movements < 6) {
                    outliers++;
                }
                double outlierDiff = Math.abs(outliers - lastOutliers);

                if (outlierDiff != outliers && outliers == 1) {
                    if ((violation += 0.5f) >= 2) {
                        handleDetection(user, "O -> " + outliers + " OD -> " + outlierDiff);
                    }
                } else {
                    violation -= Math.min(violation, 0.1);
                }
            }
            lastOutliers = outliers;
            outliers = movements = 0;
        } else if (e instanceof FlyingInEvent) {
            movements++;
            if (user.getMovementData().isBreakingOrPlacingBlock()) {
                violation = 0;
                outliers = 0;
                lastOutliers = 0;
            }
        }
    }
}