package dev.demon.venom.impl.checks.old;

import dev.demon.venom.api.check.Check;
import dev.demon.venom.api.check.CheckInfo;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.ArmAnimationEvent;
import dev.demon.venom.impl.events.inevents.BlockDigEvent;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;
import dev.demon.venom.utils.math.MathUtil;

import java.util.ArrayList;
import java.util.List;

@CheckInfo(name = "AutoClicker", type = "I", banvl = 5)
public class AutoClickerI extends Check {

    private int movements;
    private final List<Integer> delays = new ArrayList<>(100);



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

                    if (outliers == 0 && std < 0.9 && std > 0.55 && kurtosis < -0.43) {
                        alert(user, true, "O -> "+outliers + " S -> "+std + " K -> "+kurtosis);
                    }


                    delays.clear();
                }
            }
            movements = 0;
        } else if (e instanceof FlyingInEvent) {
            movements++;
        }
    }
}
