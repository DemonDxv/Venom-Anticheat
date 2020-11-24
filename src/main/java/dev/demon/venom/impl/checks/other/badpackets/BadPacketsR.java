package dev.demon.venom.impl.checks.other.badpackets;

import dev.demon.venom.api.check.Check;
import dev.demon.venom.api.check.CheckInfo;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;
import dev.demon.venom.utils.time.TimeUtils;
import org.bukkit.Bukkit;

@CheckInfo(name = "BadPackets", type = "R", banvl = 5)
public class BadPacketsR extends Check {

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof FlyingInEvent) {
            if (TimeUtils.elapsed(user.getMovementData().getLastTeleport()) < 5000L
                    || TimeUtils.elapsed(user.getMiscData().getLastBlockBreakCancel()) < 1000L
                    || TimeUtils.elapsed(user.getMiscData().getLastBlockCancel()) < 1000L) {
                return;
            }
            if (user.getMovementData().isClientGround() && user.getMovementData().isClientGround() && !user.getMovementData().isOnGround() && user.getConnectedTick() > 250) {
                if (violation++ > 0.0) {
                    alert(user, false, "Spoofing Client Ground");
                }
            } else violation -= Math.min(violation, 0.5);

        }
    }
}