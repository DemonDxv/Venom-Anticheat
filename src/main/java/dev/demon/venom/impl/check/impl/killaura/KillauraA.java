package dev.demon.venom.impl.check.impl.killaura;

import dev.demon.venom.impl.check.Check;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;
import dev.demon.venom.impl.events.inevents.UseEntityEvent;
import dev.demon.venom.utils.math.MathUtil;

public class KillauraA extends Check {

    public KillauraA(String checkname, String checktype, boolean experimental, int banVL, boolean enabled) {
        super(checkname, checktype, experimental, banVL, enabled);
    }

    private long lastFlying;

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof FlyingInEvent) {
            lastFlying = System.currentTimeMillis();
        }
        if (e instanceof UseEntityEvent) {
            if (((UseEntityEvent) e).getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK) {
                if (MathUtil.isPost(lastFlying)) {
                    if (violation++ > 10) {
                        handleDetection(user, "Sent attack packet late.");
                    }
                } else {
                    violation = 0;
                }
            }
        }
    }
}
