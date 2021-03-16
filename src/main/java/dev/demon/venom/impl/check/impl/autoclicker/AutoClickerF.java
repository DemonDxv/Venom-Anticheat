package dev.demon.venom.impl.check.impl.autoclicker;

import dev.demon.venom.impl.check.Check;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.ArmAnimationEvent;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;

import java.util.ArrayList;
import java.util.List;

public class AutoClickerF extends Check {

    public AutoClickerF(String checkname, String checktype, boolean experimental, int banVL, boolean enabled) {
        super(checkname, checktype, experimental, banVL, enabled);
    }

    private int movements;
    private List<Integer> delays = new ArrayList<>();

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof ArmAnimationEvent) {
            if (movements < 10) {
                delays.add(movements);

                if (delays.size() == 1000) {
                    int outliers = (int) delays.stream()
                            .filter(delay -> delay > 3)
                            .count();

                    if (outliers < 8) {
                        if (violation++ > 3) {
                            handleDetection(user, "Flaw within AutoClickers, O -> "+outliers);
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
