package dev.demon.venom.impl.checks.combat.autoclicker;

import dev.demon.venom.api.checknew.Check;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.ArmAnimationEvent;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;
import dev.demon.venom.utils.math.MathUtil;

import java.util.ArrayList;
import java.util.List;

public class AutoClickerB extends Check {

    public AutoClickerB(String checkname, String checktype, boolean experimental, int banVL, boolean enabled) {
        super(checkname, checktype, experimental, banVL, enabled);
    }

    private int movements;
    private List<Integer> delays = new ArrayList<>();

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof FlyingInEvent) {
            movements++;
        }
        if (e instanceof ArmAnimationEvent) {
            if (movements < 10) {
                delays.add(movements);

                if (delays.size() == 100) {

                    double std = MathUtil.getStandardDeviation(delays);

                    if (std < 0.45) {
                        handleDetection(user, "Consistent Click Speed? STD -> "+std);
                    }

                }

            }
            movements = 0;
        }
    }
}
