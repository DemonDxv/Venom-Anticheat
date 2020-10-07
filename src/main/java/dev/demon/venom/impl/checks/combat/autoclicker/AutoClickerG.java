package dev.demon.venom.impl.checks.combat.autoclicker;

import dev.demon.venom.api.check.Check;
import dev.demon.venom.api.check.CheckInfo;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.ArmAnimationEvent;
import dev.demon.venom.impl.events.FlyingEvent;


import java.util.ArrayList;
import java.util.List;

@CheckInfo(name = "AutoClicker", type = "G")
public class AutoClickerG extends Check {

    private int movements;
    private final List<Integer> delays = new ArrayList<>(250);


    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof ArmAnimationEvent) {
            if (movements < 10) {
                delays.add(movements);

                if (delays.size() == 250) {

                    int outliers = (int) delays.stream()
                            .filter(delay -> delay > 3)
                            .count();

                    if (outliers <= 7) {
                        if (violation++ > 3) {
                            alert(user, "O -> "+outliers);
                        }
                    } else violation -= Math.min(violation, 0.5);

                    delays.clear();
                }
            }
            movements = 0;
        } else if (e instanceof FlyingEvent) {
            movements++;
        }
    }
}