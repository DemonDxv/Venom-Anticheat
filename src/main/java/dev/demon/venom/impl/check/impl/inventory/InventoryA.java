package dev.demon.venom.impl.check.impl.inventory;

import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.check.Check;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;
import dev.demon.venom.utils.math.MathUtil;

public class InventoryA extends Check{
    public InventoryA(String checkname, String checktype, boolean experimental, int banVL, boolean enabled) {
        super(checkname, checktype, experimental, banVL, enabled);
    }

    private int ticks;

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof FlyingInEvent) {
            double deltaXZ = user.getMovementData().getDeltaXZ();

            if (user.getMiscData().isInventoryOpen() && deltaXZ > MathUtil.getBaseSpeed(user.getPlayer())) {
                ticks++;

                if (ticks > 7) {
                    if (++violation > 4) {
                        handleDetection(user, "Moving in inventory");
                    }
                } else {
                    violation -= Math.min(violation, 0.15);
                }
            } else {
                violation -= Math.min(violation, 0.15);
                ticks = 0;
            }
        }
    }
}
