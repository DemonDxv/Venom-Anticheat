package dev.demon.venom.impl.checks.combat.killaura;

import dev.demon.venom.api.check.Check;
import dev.demon.venom.api.check.CheckInfo;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.UseEntityEvent;
import dev.demon.venom.utils.location.CustomLocation;
import dev.demon.venom.utils.math.MathUtil;
import dev.demon.venom.utils.math.Verbose;
import org.bukkit.Bukkit;


@CheckInfo(name = "Killaura", type = "B", banvl = 10)
public class KillauraB extends Check {

    private double lastDeltaXZ;
    private Verbose verbose = new Verbose();

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof UseEntityEvent) {

            if (((UseEntityEvent) e).getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK) {
                CustomLocation to = user.getMovementData().getTo(), from = user.getMovementData().getFrom();


                double deltaXZ = Math.hypot(to.getX() - from.getX(), to.getZ() - from.getZ());

                double differenceXZ = Math.abs(deltaXZ - lastDeltaXZ);


                if (user.getMovementData().isSprinting() && deltaXZ > MathUtil.getBaseSpeed(user.getPlayer()) && differenceXZ < 0.027) {
                    if (verbose.flag(3, 750L)) {
                        alert(user, false, "Keep Sprint");
                    }
                }

                lastDeltaXZ = deltaXZ;
            }
        }
    }
}
