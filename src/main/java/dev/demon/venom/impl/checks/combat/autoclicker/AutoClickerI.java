package dev.demon.venom.impl.checks.combat.autoclicker;

import dev.demon.venom.api.check.Check;
import dev.demon.venom.api.check.CheckInfo;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.ArmAnimationEvent;
import dev.demon.venom.impl.events.inevents.BlockDigEvent;
import dev.demon.venom.impl.events.inevents.BlockPlaceEvent;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;

@CheckInfo(name = "Clicker", type = "I", banvl = 5)
public class AutoClickerI extends Check {

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

                if (user.getCombatData().getCps() > 8) {
                    if (outlierDiff != outliers && outliers == 1) {
                        if ((violation += 0.5f) >= 2) {
                            alert(user, false, "O -> " + outliers + " OD -> " + outlierDiff);
                        }
                    } else {
                        violation -= Math.min(violation, 0.1);
                    }
                }
            }
            lastOutliers = outliers;
            outliers = movements = 0;
        } else if (e instanceof FlyingInEvent) {
            movements++;
        }
        if (e instanceof BlockDigEvent || e instanceof BlockPlaceEvent) {
            violation = 0;
        }
    }
}
