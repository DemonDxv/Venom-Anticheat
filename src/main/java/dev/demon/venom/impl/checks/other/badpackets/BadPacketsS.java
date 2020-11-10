package dev.demon.venom.impl.checks.other.badpackets;

import dev.demon.venom.api.check.Check;
import dev.demon.venom.api.check.CheckInfo;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;
import dev.demon.venom.utils.location.CustomLocation;
import dev.demon.venom.utils.time.TimeUtils;
import org.bukkit.Bukkit;

@CheckInfo(name = "BadPackets", type = "S", banvl = 5)
public class BadPacketsS extends Check {

    private long lastSneak;

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof FlyingInEvent) {
            if (user.getPlayer().isSneaking()) {
                lastSneak = System.currentTimeMillis();
            }

            if (TimeUtils.elapsed(lastSneak) > 5L && TimeUtils.elapsed(lastSneak) < 40L) {
                if (violation++ > 20) {
                    alert(user, false, "LS -> "+TimeUtils.elapsed(lastSneak));
                }
            } else violation -= Math.min(violation, 0.8);
        }
    }
}