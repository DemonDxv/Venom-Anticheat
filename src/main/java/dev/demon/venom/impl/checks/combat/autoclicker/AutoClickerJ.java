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
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

@CheckInfo(name = "Clicker", type = "J", banvl = 10)
public class AutoClickerJ extends Check {

    private double ticks, lastAvg;
    private List<Double> delays = new ArrayList<>(100);

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof ArmAnimationEvent || e instanceof BlockDigEvent) {
            if (ticks < 10) {
                delays.add(ticks);

                if (delays.size() == 100) {
                    double avg = MathUtil.getAverage(delays);

                    if (Math.abs(avg - lastAvg) <= 0.01) {
                        if (violation++ > 2) {
                            alert(user, false, "AVG -> " + avg + " LAVG -> " + lastAvg);
                        }
                    } else violation -= Math.min(violation, 0.125);

                    lastAvg = avg;
                    delays.clear();
                }
            }
            ticks = 0;
        } else if (e instanceof FlyingInEvent) {
            ticks++;
            if (user.getMovementData().isBreakingOrPlacingBlock()) {
                violation = 0;
                delays.clear();
            }
        }
    }
}
