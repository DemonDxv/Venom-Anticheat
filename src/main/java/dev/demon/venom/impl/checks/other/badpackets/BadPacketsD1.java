package dev.demon.venom.impl.checks.other.badpackets;

import dev.demon.venom.api.check.Check;
import dev.demon.venom.api.check.CheckInfo;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.BlockPlaceEvent;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;
import dev.demon.venom.utils.math.MathUtil;
import dev.demon.venom.utils.time.TimeUtils;

@CheckInfo(name = "BadPackets", type = "D1", banvl = 100)
public class BadPacketsD1 extends Check {

    private long lastFlying;
    private boolean returnBlock;

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof FlyingInEvent) {
            if (user.getLagProcessor().isLagging() || user.getLagProcessor().getLastPingDiff() > 100) {
                lastFlying = 1000L;
            }
            lastFlying = System.currentTimeMillis();
        }
        if (e instanceof BlockPlaceEvent) {
            if (((BlockPlaceEvent) e).getItemStack().getType().isBlock()) {
                if (MathUtil.isPost(lastFlying)) {
                    if (violation++ > 10) {
                        alert(user, true, "Sent block blacement packet late");
                    }
                } else violation = 0;
            }
        }
    }
}