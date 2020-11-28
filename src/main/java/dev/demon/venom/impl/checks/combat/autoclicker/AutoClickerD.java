package dev.demon.venom.impl.checks.combat.autoclicker;

import dev.demon.venom.api.check.Check;
import dev.demon.venom.api.check.CheckInfo;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.ArmAnimationEvent;
import dev.demon.venom.impl.events.inevents.BlockDigEvent;
import dev.demon.venom.impl.events.inevents.BlockPlaceEvent;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;
import dev.demon.venom.utils.math.MathUtil;


import java.util.ArrayList;
import java.util.List;

@CheckInfo(name = "Clicker", type = "D", banvl = 5)
public class AutoClickerD extends Check {

    private int movements;
    private final List<Integer> delays = new ArrayList<>(50);
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
                            alert(user, false,"STD -> "+Math.abs(std - lastStDev));
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
