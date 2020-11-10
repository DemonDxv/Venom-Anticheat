package dev.demon.venom.impl.checks.combat.killaura;

import dev.demon.venom.api.check.Check;
import dev.demon.venom.api.check.CheckInfo;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.UseEntityEvent;

import javax.xml.bind.helpers.ValidationEventLocatorImpl;

@CheckInfo(name = "Killaura", type = "H", banvl = 5)
public class KillauraH extends Check {

    private double lastPitch;

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof UseEntityEvent) {
            if (((UseEntityEvent) e).getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK) {

                double pitch = Math.abs(user.getMovementData().getTo().getPitch() - user.getMovementData().getFrom().getPitch());
                double yaw = Math.abs(user.getMovementData().getTo().getYaw() - user.getMovementData().getFrom().getYaw());

                if (yaw > 3) {
                    if (Math.abs(pitch - lastPitch) < 0.0005) {
                        if (violation++ > 1) {
                            alert(user, false, "PD -> " + Math.abs(pitch - lastPitch));
                        }
                    } else violation -= Math.min(violation, 0.1);
                }

                lastPitch = pitch;
            }
        }
    }
}
