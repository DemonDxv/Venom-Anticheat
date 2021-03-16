package dev.demon.venom.impl.check.impl.killaura;

import dev.demon.venom.impl.check.Check;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.UseEntityEvent;
import org.bukkit.entity.Player;

public class KillauraF extends Check {

    public KillauraF(String checkname, String checktype, boolean experimental, int banVL, boolean enabled) {
        super(checkname, checktype, experimental, banVL, enabled);
    }

    private final double offset = Math.pow(2.0, 24.0);

    private double lastPitchDelta;

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof UseEntityEvent) {
            if (((UseEntityEvent) e).getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK) {
                if (((UseEntityEvent) e).getEntity() instanceof Player) {
                    double deltaPitch = user.getMovementData().getTo().getPitch() - user.getMovementData().getFrom().getPitch();

                    double yaw = user.getMovementData().getYawDelta();

                    if (Math.abs(deltaPitch - lastPitchDelta) < 0.03 && yaw > 3) {
                        if (violation++ > 3) {
                            handleDetection(user, "P -> " + Math.abs(deltaPitch - lastPitchDelta));
                        }
                    } else {
                        violation -= Math.min(violation, 0.25);
                    }

                    lastPitchDelta = deltaPitch;
                }
            }
        }
    }
}
