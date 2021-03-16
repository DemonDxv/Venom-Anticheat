package dev.demon.venom.impl.check.impl.badpackets;

import dev.demon.venom.impl.check.Check;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;
import dev.demon.venom.utils.time.TimeUtils;

public class BadPacketsI extends Check {
    public BadPacketsI(String checkname, String checktype, boolean experimental, int banVL, boolean enabled) {
        super(checkname, checktype, experimental, banVL, enabled);
    }

    private long lastInvOpen;
    private int ticks;

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof FlyingInEvent) {
            boolean isOpen = user.getMiscData().isInventoryOpen();
            if (isOpen) {
                ticks++;
                if (ticks == 1) {
                    lastInvOpen = System.currentTimeMillis();
                }
            } else {
                ticks = 0;
            }

            if (TimeUtils.elapsed(lastInvOpen) <= 5L) {
                if (violation++ > 5) {
                    handleDetection(user, "Spamming Open Inventory Packets");
                }
            } else {
                violation = Math.min(violation, 0.055);
            }
        }
    }
}
