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

@CheckInfo(name = "Clicker", type = "H", banvl = 5)
public class AutoClickerH extends Check {

    private int movements;
    private final List<Integer> delays = new ArrayList<>(100);
    private double lastTotal;


    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof ArmAnimationEvent || e instanceof BlockDigEvent) {
            if (movements < 10) {
                delays.add(movements);

                if (delays.size() == 100) {

                    double std = MathUtil.getStandardDeviation(delays);

                    double kurtosis = MathUtil.getKurtosis(delays);

                    int outliers = (int) delays.stream()
                            .filter(delay -> delay > 3)
                            .count();

                    double total = outliers + (Math.abs(std - kurtosis));

                    double totalDiff = Math.abs(total - lastTotal);

                    if (total <= 1.2 && totalDiff <= 0.1) {
                        if (violation++ > 1) {
                            alert(user, false, "T -> " + total + " TD -> " + totalDiff);
                        }
                    } else violation -= Math.min(violation, 0.1);

                    lastTotal = total;
                    delays.clear();
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
