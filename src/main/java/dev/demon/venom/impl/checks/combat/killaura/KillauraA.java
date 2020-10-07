package dev.demon.venom.impl.checks.combat.killaura;

import dev.demon.venom.api.check.Check;
import dev.demon.venom.api.check.CheckInfo;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.FlyingEvent;
import dev.demon.venom.impl.events.UseEntityEvent;
import dev.demon.venom.utils.time.TimeUtils;


@CheckInfo(name = "Killaura", type = "A")
public class KillauraA extends Check {

    private long lastFlying;

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof FlyingEvent) {

            lastFlying = System.currentTimeMillis();

        }
        if (e instanceof UseEntityEvent) {

            if (TimeUtils.elapsed(lastFlying) < 5L) {
                if (violation++ > 10) {
                    alert(user);
                }
            } else violation -= Math.min(violation, 0.5);
        }
    }
}
