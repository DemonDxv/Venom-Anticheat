package dev.demon.venom.impl.checksv2.combat.autoclicker;

import dev.demon.venom.api.check.Check;
import dev.demon.venom.api.check.CheckInfo;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.ArmAnimationEvent;
import dev.demon.venom.impl.events.FlyingEvent;
import dev.demon.venom.utils.math.MathUtil;


import java.util.ArrayList;
import java.util.List;

@CheckInfo(name = "AutoClicker", type = "F")
public class AutoClickerF extends Check {

    private int movements;
    private final List<Integer> delays = new ArrayList<>();

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof ArmAnimationEvent) {
            if (movements < 10) {
                delays.add(movements);

                if (delays.size() == 50) {
                    double kurtosis = MathUtil.getKurtosis(delays);


                    if (kurtosis > 20 || Double.isNaN(kurtosis)) {
                        if (violation++ > 1) {
                            alert(user, "K -> "+kurtosis);
                        }
                    } else violation -= Math.min(violation, 0.125);

                    delays.clear();
                }
            }
            movements = 0;
        } else if (e instanceof FlyingEvent) {
            movements++;
        }
    }
}