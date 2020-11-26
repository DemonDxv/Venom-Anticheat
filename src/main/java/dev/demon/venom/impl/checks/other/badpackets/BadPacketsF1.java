package dev.demon.venom.impl.checks.other.badpackets;

import dev.demon.venom.api.check.Check;
import dev.demon.venom.api.check.CheckInfo;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.BlockPlaceEvent;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;
import dev.demon.venom.utils.time.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;

import java.util.Collections;

@CheckInfo(name = "BadPackets", type = "F1", banvl = 100)
public class BadPacketsF1 extends Check {

    @Override
    public void onHandle(User user, AnticheatEvent e) {

        if (e instanceof FlyingInEvent) {

            Block blockAt = user.getPlayer().getTargetBlock(Collections.singleton(user.getPlayer().getEyeLocation().getBlock().getType()), 7);

            if (blockAt != null) {
                if (blockAt.getType().toString().equalsIgnoreCase("AIR")
                        && user.getMovementData().getTo().getPitch() > 25 && TimeUtils.elapsed(user.getMiscData().getLastBlockPlace()) < 300L) {
                    violation++;
                } else {
                    violation = 0;
                }
                if (violation > 10) {
                    alert(user, true, "Placing a block while not looking at it");
                }
            }
        }
    }
}