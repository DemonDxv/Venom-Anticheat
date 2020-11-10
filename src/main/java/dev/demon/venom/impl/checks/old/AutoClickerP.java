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

@CheckInfo(name = "AutoClicker", type = "P", banvl = 5)
public class AutoClickerP extends Check {

    private List<Double> autoClickerOList = new ArrayList<>();

    private int lastPositive, lastNegaitive;

    private double movements;
    private final List<Double> delays = new ArrayList<>(500);

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof ArmAnimationEvent) {
            if (movements < 10) {
                delays.add(movements);

                if (delays.size() == 25) {
                    autoClickerOList.add(MathUtil.getStandardDeviation(delays));

                    if (autoClickerOList.size() == 4) {
                        GraphUtil.GraphResult result = GraphUtil.getGraph(autoClickerOList);

                        if (result.getPositives() - result.getNegatives() != 0.0) {
                            if (violation++ >= 10) {
                                alert(user, true, "P -> " + result.getPositives() + " N -> " + result.getNegatives());
                            }
                        } else {
                            violation = 0.0;
                        }

                        lastPositive = result.getPositives();
                        lastNegaitive = result.getNegatives();

                        autoClickerOList.clear();
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
