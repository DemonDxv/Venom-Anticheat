package dev.demon.venom.impl.checks.combat.aimassist;

import dev.demon.venom.api.check.Check;
import dev.demon.venom.api.check.CheckInfo;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;
import dev.demon.venom.utils.math.MathUtil;
import dev.demon.venom.utils.time.TimeUtils;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

@CheckInfo(name = "AimAssist", type = "C", banvl = 10)
public class AimAssistC extends Check {
    private final List<Double> pitchDeltaSamples = new ArrayList<>();
    private double lastAvg, lastStd, lastKurtosis;

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof FlyingInEvent) {
            double deltaPitch = user.getMovementData().getPitchDelta();
            double deltaYaw = user.getMovementData().getYawDelta();
            if (deltaPitch > 0 && deltaYaw > 1 && TimeUtils.elapsed(user.getCombatData().getLastUseEntityPacket()) < 100L) {
                pitchDeltaSamples.add(deltaPitch);
            }

            if (pitchDeltaSamples.size() == 100) {
                double std = MathUtil.getStandardDeviation(pitchDeltaSamples);
                double kurtosis = MathUtil.getKurtosis(pitchDeltaSamples);
                double average = MathUtil.getAverage(pitchDeltaSamples);

                if (std < 0.8) {
                    alert(user, true, "STD -> "+std);
                }

                if (kurtosis > 15) {
                    alert(user, true, "K -> "+kurtosis + " [1]");
                }

                if (kurtosis < -1) {
                    alert(user, true, "K -> "+kurtosis + " [2]");
                }

                if (average < 0.9) {
                    alert(user, true, "A -> "+average + " [3]");
                }

                if (Math.abs(average - lastAvg) <= 0.001) {
                    alert(user, true, "AD -> "+ Math.abs(average - lastAvg) + " [4]");
                }

                if (Math.abs(std - lastStd) <= 0.1) {
                    alert(user, true, "STD DIFF -> "+ Math.abs(std - lastStd) + " [5]");
                }

                if (Math.abs(kurtosis - lastKurtosis) <= 0.1) {
                    alert(user, true, "KD -> "+ Math.abs(kurtosis - lastKurtosis) + " [6]");
                }


                lastAvg = average;
                lastStd = std;
                lastKurtosis = kurtosis;

                pitchDeltaSamples.clear();
            }
        }
    }
}
