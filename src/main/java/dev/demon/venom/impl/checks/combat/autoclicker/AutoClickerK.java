package dev.demon.venom.impl.checks.combat.autoclicker;

import com.google.gson.internal.$Gson$Preconditions;
import dev.demon.venom.api.check.Check;
import dev.demon.venom.api.check.CheckInfo;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.ArmAnimationEvent;
import dev.demon.venom.impl.events.inevents.BlockDigEvent;
import dev.demon.venom.impl.events.inevents.BlockPlaceEvent;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;
import dev.demon.venom.utils.math.MathUtil;
import dev.demon.venom.utils.math.Tuple;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

@CheckInfo(name = "Clicker", type = "K", banvl = 10)
public class AutoClickerK extends Check {

    private double ticks;
    private List<Double> delays = new ArrayList<>(100);

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof ArmAnimationEvent || e instanceof BlockDigEvent) {
            if (ticks < 5) {
                delays.add(ticks);

                if (delays.size() == 100) {
                    Tuple<List<Double>, List<Double>> outlierPair = MathUtil.getOutliers(delays);

                    int outliers = outlierPair.one.size() + outlierPair.two.size();
                    int duplicates = (int) (delays.size() - delays.stream().distinct().count());

                    if (outliers <= 0D && duplicates > 15) {
                        if (violation++ > 1.25) {
                            alert(user, false, "O -> " + outliers + " D -> " + duplicates);
                        }
                    } else violation -= Math.min(violation, 0.25);
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
