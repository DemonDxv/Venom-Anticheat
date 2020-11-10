package dev.demon.venom.impl.checks.combat.autoclicker;

import dev.demon.venom.api.check.Check;
import dev.demon.venom.api.check.CheckInfo;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.ArmAnimationEvent;
import dev.demon.venom.impl.events.inevents.BlockDigEvent;
import dev.demon.venom.impl.events.inevents.BlockPlaceEvent;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;


@CheckInfo(name = "Clicker", type = "A", banvl = 3)
public class AutoClickerA extends Check {
    private int ticks, cps;

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof FlyingInEvent) {
            if (++ticks == 20) {
                if (cps >= 22) {
                    alert(user, false,"C -> " + cps);
                }
                ticks = cps = 0;
            }
        } else if (e instanceof ArmAnimationEvent) {
            cps++;
        }

        if (e instanceof BlockDigEvent || e instanceof BlockPlaceEvent) {
            ticks = 0;
            cps = 0;
        }
    }
}
