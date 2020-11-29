package dev.demon.venom.impl.checks.other.badpackets;

import dev.demon.venom.api.check.Check;
import dev.demon.venom.api.check.CheckInfo;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.BlockDigEvent;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;
import dev.demon.venom.impl.events.inevents.TransactionEvent;
import dev.demon.venom.utils.time.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Flying;

@CheckInfo(name = "BadPackets", type = "T", banvl = 5)
public class BadPacketsT extends Check {

    private boolean isFakeLagging;
    private long lastTrans, lastLastPos, lastPos;

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof TransactionEvent) {
            if (((TransactionEvent) e).getAction() == -1) {
                lastTrans = System.currentTimeMillis();
            }
        }
        if (TimeUtils.elapsed(lastPos) > 1000 && TimeUtils.elapsed(lastLastPos) > 1000) {
            if (e instanceof FlyingInEvent) {
                lastLastPos = lastPos;
                lastPos = System.currentTimeMillis();

            /*    if (TimeUtils.elapsed(lastTrans) > 100L && TimeUtils.elapsed(lastTrans) != user.getLagProcessor().getLastTransaction() || TimeUtils.elapsed(lastTrans) <= 5) {
                    if (violation++ > 7) {
                        alert(user, true);
                    }
                } else violation -= Math.min(violation, 0.75);
            }*/
            }
        }
    }
}