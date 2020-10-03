package dev.demon.venom.impl.checksv2.combat.autoclicker;

import dev.demon.venom.api.check.Check;
import dev.demon.venom.api.check.CheckInfo;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.ArmAnimationEvent;
import dev.demon.venom.impl.events.BlockDigEvent;
import dev.demon.venom.impl.events.BlockSentEvent;
import dev.demon.venom.impl.events.FlyingEvent;


@CheckInfo(name = "AutoClicker", type = "A")
public class AutoClickerA extends Check {
    private int ticks, cps;

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof FlyingEvent) {
            if (++ticks == 20) {
                if (cps >= 20) {
                    alert(user, "C -> " + cps);
                }
                ticks = cps = 0;
            }
        } else if (e instanceof ArmAnimationEvent) {
            cps++;
        }

        if (e instanceof BlockDigEvent || e instanceof BlockSentEvent) {
            ticks = 0;
            cps = 0;
        }
    }
}
