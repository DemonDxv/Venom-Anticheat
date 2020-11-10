package dev.demon.venom.impl.checks.combat.killaura;

import dev.demon.venom.api.check.Check;
import dev.demon.venom.api.check.CheckInfo;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.UseEntityEvent;
import org.bukkit.block.Block;

import java.util.Collections;

@CheckInfo(name = "Killaura", type = "G", banvl = 50)
public class KillauraG extends Check {

    private double lastYaw, lastPitch;

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof UseEntityEvent) {
            if (((UseEntityEvent) e).getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK) {

                double pitch = Math.abs(user.getMovementData().getTo().getPitch() - user.getMovementData().getFrom().getPitch());
                double yaw = Math.abs(user.getMovementData().getTo().getYaw() - user.getMovementData().getFrom().getYaw());

                if (yaw > 3) {
                    if (yaw == lastYaw || pitch == lastPitch) {
                        if (violation++ > 2) {
                            alert(user, false, "Consistency within the yaw and or pitch");
                        }
                    } else violation -= Math.min(violation, 0.25);
                }

                lastYaw = yaw;
                lastPitch = pitch;
            }
        }
    }
}
