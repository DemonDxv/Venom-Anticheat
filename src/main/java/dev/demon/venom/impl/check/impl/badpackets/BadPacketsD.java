package dev.demon.venom.impl.check.impl.badpackets;

import dev.demon.venom.impl.check.Check;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.BlockPlaceEvent;

public class BadPacketsD extends Check {
    public BadPacketsD(String checkname, String checktype, boolean experimental, int banVL, boolean enabled) {
        super(checkname, checktype, experimental, banVL, enabled);
    }

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof BlockPlaceEvent) {
            if (((BlockPlaceEvent) e).getItemStack().getType().isBlock()) {
                float vecX = ((BlockPlaceEvent) e).getVecX(), vecY = ((BlockPlaceEvent) e).getVecY(), vecZ = ((BlockPlaceEvent) e).getVecZ();

                if (vecX > 1 || vecY > 1 || vecZ > 1) {
                    handleDetection(user, "Invalid block vector placement");
                }
            }
        }
    }
}
