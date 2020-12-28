package dev.demon.venom.impl.checks.combat.killaura;

import dev.demon.venom.api.checknew.Check;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.ArmAnimationEvent;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;
import dev.demon.venom.impl.events.inevents.UseEntityEvent;
import dev.demon.venom.utils.math.MathUtil;

public class KillauraB extends Check {

    public KillauraB(String checkname, String checktype, boolean experimental, int banVL, boolean enabled) {
        super(checkname, checktype, experimental, banVL, enabled);
    }

    private long lastFlying;

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof FlyingInEvent) {
            lastFlying = System.currentTimeMillis();
        }
        if (e instanceof ArmAnimationEvent) {
            if (MathUtil.isPost(lastFlying)) {
                if (violation++ > 10) {
                    handleDetection(user, "Sent arm swing packet late.");
                }
            } else {
                violation = 0;
            }
        }
    }
}
