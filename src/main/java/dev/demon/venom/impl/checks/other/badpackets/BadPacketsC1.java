package dev.demon.venom.impl.checks.other.badpackets;

import dev.demon.venom.Venom;
import dev.demon.venom.api.check.Check;
import dev.demon.venom.api.check.CheckInfo;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.*;
import dev.demon.venom.utils.math.MathUtil;
import dev.demon.venom.utils.time.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Flying;
import org.bukkit.scheduler.BukkitRunnable;

@CheckInfo(name = "BadPackets", type = "C1", banvl = 100)
public class BadPacketsC1 extends Check {

    private boolean returnBlock;

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof FlyingInEvent) {
            if (user.getMovementData().getTo().getPitch() > 77 && user.getMovementData().isSprinting()
                    && TimeUtils.elapsed(user.getMiscData().getLastBlockPlace()) < 250L && !returnBlock) {
                if (violation++ > 3) {
                    alert(user, true, "Scaffolding while sprinting");
                }
            } else violation -= Math.min(violation, 0.125);
        }
        if (e instanceof BlockPlaceEvent) {

            if (((BlockPlaceEvent) e).getItemStack().getType().isBlock()) {
                float vecY = ((BlockPlaceEvent) e).getVecY();

                if (vecY == 1 || vecY == 0) {
                    returnBlock = true;
                } else {
                    returnBlock = false;
                }
            }
        }
    }
}