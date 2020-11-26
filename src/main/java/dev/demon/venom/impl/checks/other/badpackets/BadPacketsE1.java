package dev.demon.venom.impl.checks.other.badpackets;

import dev.demon.venom.api.check.Check;
import dev.demon.venom.api.check.CheckInfo;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.BlockPlaceEvent;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;
import dev.demon.venom.utils.math.MathUtil;
import dev.demon.venom.utils.time.TimeUtils;

@CheckInfo(name = "BadPackets", type = "E1", banvl = 100)
public class BadPacketsE1 extends Check {

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof BlockPlaceEvent) {

            if (((BlockPlaceEvent) e).getItemStack().getType().isBlock()) {
                float vecX = ((BlockPlaceEvent) e).getVecX(), vecY = ((BlockPlaceEvent) e).getVecY(), vecZ = ((BlockPlaceEvent) e).getVecZ();

                if (vecX > 1 || vecY > 1 || vecZ > 1) {
                    alert(user, true, "Invalid block vector placement");
                }
            }
        }
    }
}