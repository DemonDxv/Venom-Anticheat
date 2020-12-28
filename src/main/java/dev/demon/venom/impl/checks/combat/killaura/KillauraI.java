package dev.demon.venom.impl.checks.combat.killaura;

import dev.demon.venom.api.checknew.Check;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;
import dev.demon.venom.impl.events.inevents.UseEntityEvent;
import dev.demon.venom.utils.math.MathUtil;
import dev.demon.venom.utils.time.TimeUtils;

import java.util.ArrayList;
import java.util.List;

public class KillauraI extends Check {

    public KillauraI(String checkname, String checktype, boolean experimental, int banVL, boolean enabled) {
        super(checkname, checktype, experimental, banVL, enabled);
    }

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof UseEntityEvent) {
            if (((UseEntityEvent) e).getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK) {


                double deltaYaw = Math.abs(user.getMovementData().getTo().getYaw() - user.getMovementData().getFrom().getYaw());

                double fix = (deltaYaw - user.getOldProcessors().getMouseX());

                double clientSens = user.getOldProcessors().getMouseX();

                if (clientSens > 100000) {
                    handleDetection(user, "Head Snapping [1]");
                }

                if (deltaYaw > 0.0 && fix > 60.0) {
                    double snap = Math.abs(deltaYaw - fix);

                    if (snap < 0.70 && fix != 360 && deltaYaw > 99.99) {
                        handleDetection(user, "Head Snapping [2]");
                    }
                }
            }
        }
    }
}
