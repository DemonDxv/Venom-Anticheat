package dev.demon.venom.impl.check.impl.autoclicker;

import dev.demon.venom.impl.check.Check;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.ArmAnimationEvent;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;
import dev.demon.venom.utils.math.MathUtil;

import java.util.ArrayList;
import java.util.List;

public class AutoClickerD extends Check {

    public AutoClickerD(String checkname, String checktype, boolean experimental, int banVL, boolean enabled) {
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

                    double kurtosis = MathUtil.getKurtosis(delays);

                    if (kurtosis < -4.0) {
                        handleDetection(user, "Invalid Clicks? K -> "+kurtosis);
                    }

                }

            }
            movements = 0;
        }
    }
}
