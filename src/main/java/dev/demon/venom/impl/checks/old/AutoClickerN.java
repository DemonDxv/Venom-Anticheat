package dev.demon.venom.impl.checks.old;

import dev.demon.venom.api.check.Check;
import dev.demon.venom.api.check.CheckInfo;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.ArmAnimationEvent;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;
import dev.demon.venom.utils.math.GraphUtil;
import dev.demon.venom.utils.math.MathUtil;

import java.util.ArrayList;
import java.util.List;

@CheckInfo(name = "AutoClicker", type = "N", banvl = 5)
public class AutoClickerN extends Check {

    private List<Double> autoClickerNList = new ArrayList<>();

    private int lastPositive, lastNegaitive;

    private double movements;
    private final List<Double> delays = new ArrayList<>(500);

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof ArmAnimationEvent) {
            if (movements < 10) {
                delays.add(movements);

                if (delays.size() == 25) {
                    autoClickerNList.add(MathUtil.getKurtosis(delays));

                    if (autoClickerNList.size() == 4) {
                        GraphUtil.GraphResult result = GraphUtil.getGraph(autoClickerNList);

                        if (result.getNegatives() == 2 && result.getPositives() == 2) {
                            if (violation++ >= 1) {
                                alert(user, false, "CLOUD CLICKER LOL NICE SHIT CLICKER HAHHAHA");
                            }
                        } else violation -= Math.min(violation, 0.25);

                        lastPositive = result.getPositives();
                        lastNegaitive = result.getNegatives();

                        autoClickerNList.clear();
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
