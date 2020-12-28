package dev.demon.venom.impl.checks.combat.autoclicker;

import dev.demon.venom.api.checknew.Check;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.ArmAnimationEvent;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;
import dev.demon.venom.utils.math.MathUtil;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class AutoClickerH extends Check {
    public AutoClickerH(String checkname, String checktype, boolean experimental, int banVL, boolean enabled) {
        super(checkname, checktype, experimental, banVL, enabled);
    }

    private double movements;
    private List<Double> delays = new ArrayList<>();

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof FlyingInEvent) {
            movements++;
        }
        if (e instanceof ArmAnimationEvent) {

            if (movements < 10) {
                delays.add(movements);

                if (delays.size() == 500) {
                    Collections.sort(delays);

                    double sigmoid = MathUtil.getSigmoidGraph(delays);

                    Bukkit.broadcastMessage(""+sigmoid);
                }
            }
            movements = 0;
        }
    }
}