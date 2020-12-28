package dev.demon.venom.impl.checks.player.badpackets;

import dev.demon.venom.api.checknew.Check;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.BlockPlaceEvent;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;
import dev.demon.venom.utils.math.MathUtil;
import dev.demon.venom.utils.time.TimeUtils;
import org.bukkit.Bukkit;

public class BadPacketsE extends Check {
    public BadPacketsE(String checkname, String checktype, boolean experimental, int banVL, boolean enabled) {
        super(checkname, checktype, experimental, banVL, enabled);
    }

    private long lastBlocking;

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof BlockPlaceEvent) {
            boolean item = TimeUtils.elapsed(user.getMiscData().getLastBlockPlace()) < 100L
                    || user.getMiscData().isSword(user.getPlayer().getItemInHand());

            if (item) {
                lastBlocking = System.currentTimeMillis();
            }
        }

        if (e instanceof FlyingInEvent) {
            if (user.generalCancel()
                    || user.getLagProcessor().isLagging()
                    || user.getBlockData().lastInsideBlockTimer.hasNotPassed(20)
                    || user.getMovementData().getLastTeleportTimer().hasNotPassed(20)
                    || !user.getMovementData().isChunkLoaded()) {
                violation = 0;
                lastBlocking = 1000L;
                return;
            }

            if (TimeUtils.elapsed(lastBlocking) <= 5L) {
                if (violation++ > 3) {
                    handleDetection(user, "Sent block placement packet late");
                }
            } else violation = 0;
        }
    }
}
