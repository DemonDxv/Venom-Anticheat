package dev.demon.venom.impl.check.impl.killaura;

import dev.demon.venom.impl.check.Check;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.UseEntityEvent;
import dev.demon.venom.utils.math.MathUtil;

public class KillauraE extends Check {

    public KillauraE(String checkname, String checktype, boolean experimental, int banVL, boolean enabled) {
        super(checkname, checktype, experimental, banVL, enabled);
    }

    private final double offset = Math.pow(2.0, 24.0);

    private double lastPitchDelta;

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof UseEntityEvent) {
            if (((UseEntityEvent) e).getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK) {
                double deltaPitch = user.getMovementData().getTo().getPitch() - user.getMovementData().getFrom().getPitch();

                long gcd = MathUtil.gcd((long) (deltaPitch * offset), (long) (lastPitchDelta * offset));

                if (deltaPitch != lastPitchDelta && gcd < 131072L && gcd >= 0) {
                    if (violation++ > 10) {
                        handleDetection(user, "GCD -> " + gcd);
                    }
                } else {
                    violation -= Math.min(violation, 0.25);
                }

                lastPitchDelta = deltaPitch;
            }
        }
    }
}
