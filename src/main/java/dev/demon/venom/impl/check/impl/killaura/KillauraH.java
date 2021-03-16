package dev.demon.venom.impl.check.impl.killaura;

import dev.demon.venom.impl.check.Check;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;
import dev.demon.venom.utils.math.MathUtil;
import dev.demon.venom.utils.time.TimeUtils;

public class KillauraH extends Check {

    public KillauraH(String checkname, String checktype, boolean experimental, int banVL, boolean enabled) {
        super(checkname, checktype, experimental, banVL, enabled);
    }

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof FlyingInEvent) {
            double deltaYaw = Math.abs(user.getMovementData().getTo().getYaw() - user.getMovementData().getFrom().getYaw());

            if (TimeUtils.elapsed(user.getCombatData().getLastUseEntityPacket()) < 100L) {
                if (deltaYaw == MathUtil.round(deltaYaw, 1) && deltaYaw > 0) {
                    handleDetection(user, "Rounded Yaw");
                }
            }
        }
    }
}
