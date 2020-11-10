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

@CheckInfo(name = "AutoClicker", type = "O", banvl = 5)
public class AutoClickerO extends Check {

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
                    autoClickerOList.add(MathUtil.getKurtosis(delays));

                    if (autoClickerOList.size() == 2) {
                        GraphUtil.GraphResult result = GraphUtil.getGraph(autoClickerOList);

                        if ((result.getNegatives() - result.getPositives()) == 0.0) {
                            if (violation++ >= 1) {
                                alert(user, false, "");
                            }
                        } else violation -= Math.min(violation, 0.25);

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
