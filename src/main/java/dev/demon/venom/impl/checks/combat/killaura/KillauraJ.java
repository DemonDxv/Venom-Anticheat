package dev.demon.venom.impl.checks.combat.killaura;

import dev.demon.venom.api.check.Check;
import dev.demon.venom.api.check.CheckInfo;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.UseEntityEvent;
import dev.demon.venom.utils.math.Verbose;

@CheckInfo(name = "Killaura", type = "J", banvl = 5)
public class KillauraJ extends Check {

    private double lastYawDelta, lastLastYawDelta;
    private Verbose verbose = new Verbose();

    /** Credits To FlyCode **/

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof UseEntityEvent) {
            if (((UseEntityEvent) e).getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK) {
                double yawDelta = Math.abs(user.getMovementData().getTo().getYaw() - user.getMovementData().getFrom().getYaw());

                double delta = Math.abs(yawDelta - lastYawDelta);
                double lastDelta = Math.abs(lastYawDelta - lastLastYawDelta);

                if (delta > 0.0 && lastDelta > 0.0 && !user.isUsingNewOptifine()) {

                    boolean small = !(delta > 0.001);

                    if (delta < 0.099 && lastDelta > 0.65 && !small && verbose.flag((delta < 0.060 ? 7 : 3), 1000L * 2L)) {
                        alert(user, false,"D -> " + delta, " LD -> " + lastDelta);
                    }
                }

                lastLastYawDelta = lastYawDelta;
                lastYawDelta = yawDelta;
            }
        }
    }
}