package dev.demon.venom.impl.checks.old;

import dev.demon.venom.api.check.Check;
import dev.demon.venom.api.check.CheckInfo;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.ArmAnimationEvent;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;


import java.util.ArrayList;
import java.util.List;

@CheckInfo(name = "AutoClicker", type = "H", banvl = 10)
public class AutoClickerH extends Check {

    private int movements;
    private final List<Integer> delays = new ArrayList<>(100);


    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof ArmAnimationEvent) {
            if (movements < 10) {
                delays.add(movements);

                if (delays.size() == 100) {

                    int outliers = (int) delays.stream()
                            .filter(delay -> delay > 3)
                            .count();

                    if (outliers >= 1 && outliers <= 2) {
                        if (violation++ > 2) {
                            alert(user, true,"O -> "+outliers);
                        }
                    } else violation -= Math.min(violation, 0.5);

                    delays.clear();
                }
            }
            movements = 0;
        } else if (e instanceof FlyingInEvent) {
            movements++;
        }
    }
}
