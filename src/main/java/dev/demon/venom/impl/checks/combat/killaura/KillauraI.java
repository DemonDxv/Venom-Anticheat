package dev.demon.venom.impl.checks.combat.killaura;

import dev.demon.venom.api.check.Check;
import dev.demon.venom.api.check.CheckInfo;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.UseEntityEvent;
import org.bukkit.Bukkit;

@CheckInfo(name = "Killaura", type = "I", banvl = 5)
public class KillauraI extends Check {

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof UseEntityEvent) {
            if (((UseEntityEvent) e).getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK) {


                double deltaYaw = Math.abs(user.getMovementData().getTo().getYaw() - user.getMovementData().getFrom().getYaw());

                double fix = (deltaYaw - user.getOldProcessors().getMouseX());


                if (deltaYaw > 0.0 && fix > 60.0) {
                    double snap = Math.abs(deltaYaw - fix);

                    if (snap < 0.70 && fix != 360 && deltaYaw > 99.99) {
                        alert(user, true, "Head Snapping");
                    }
                }
            }
        }
    }
}
