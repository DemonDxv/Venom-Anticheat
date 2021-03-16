package dev.demon.venom.impl.check.impl.autoclicker;

import dev.demon.venom.impl.check.Check;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.ArmAnimationEvent;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;
import dev.demon.venom.utils.math.MathUtil;

import java.util.ArrayList;
import java.util.List;

public class AutoClickerE extends Check {

    public AutoClickerE(String checkname, String checktype, boolean experimental, int banVL, boolean enabled) {
        super(checkname, checktype, experimental, banVL, enabled);
    }

    private int movements;
    private List<Integer> delays = new ArrayList<>();
    private double lastStDev = -1;

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof ArmAnimationEvent) {
            if (movements < 10) {
                delays.add(movements);

                if (delays.size() == 50) {
                    double std = MathUtil.getStandardDeviation(delays);

                    if (Math.abs(std - lastStDev) < 0.05) {
                        if (violation++ > 5) {
                            handleDetection(user, "Consistency Found Within Clicks? STD -> "+Math.abs(std - lastStDev));
                        }
                    } else violation -= Math.min(violation, 1);


                    delays.clear();
                    lastStDev = std;
                }
            }
            movements = 0;
        } else if (e instanceof FlyingInEvent) {
            movements++;
            if (user.getMovementData().isBreakingOrPlacingBlock()) {
                violation = 0;
                delays.clear();
            }
        }
    }
}
