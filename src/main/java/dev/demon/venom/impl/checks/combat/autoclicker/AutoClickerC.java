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

@CheckInfo(name = "Clicker", type = "C", banvl = 30)
public class AutoClickerC extends Check {

    private int movements;
    private final List<Integer> delays = new ArrayList<>(500);

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof ArmAnimationEvent) {
            if (movements < 10) {
                delays.add(movements);

                if (delays.size() == 500) {
                    double kurtosis = MathUtil.getKurtosis(delays);

                    if (kurtosis < 0D) {
                        if (violation++ > 1) {
                            alert(user, true,"K -> "+kurtosis);
                        }
                    } else violation -= Math.min(violation, 0.125);

                    delays.clear();
                }
            }
            movements = 0;
        } else if (e instanceof FlyingInEvent) {
            movements++;
        }
        if (e instanceof BlockDigEvent || e instanceof BlockPlaceEvent) {
            movements = 0;
            delays.clear();
        }
    }
}
