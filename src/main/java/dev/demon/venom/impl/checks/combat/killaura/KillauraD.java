package dev.demon.venom.impl.checks.combat.killaura;

import dev.demon.venom.api.check.Check;
import dev.demon.venom.api.check.CheckInfo;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.ArmAnimationEvent;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;
import dev.demon.venom.impl.events.inevents.UseEntityEvent;
import dev.demon.venom.utils.math.Verbose;
import org.bukkit.entity.Player;


@CheckInfo(name = "Killaura", type = "D", banvl = 10)
public class KillauraD extends Check {

    private int attack, miss, ticks;
    private Verbose verbose = new Verbose();

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof UseEntityEvent) {
            if (((UseEntityEvent) e).getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK) {
                if (((UseEntityEvent) e).getEntity() instanceof Player) {
                    double yawDiff = (user.getMovementData().getTo().getYaw() - user.getMovementData().getFrom().getYaw());
                    attack++;

                    double ratio = Math.abs((attack - miss) / user.getInBoxTicks());

                    if (ratio <= 0.3 && yawDiff > 2.5 && user.getPlayer().getLocation().distance(((UseEntityEvent) e).getEntity().getLocation()) > 2.8) {
                        if (verbose.flag(20, 1000L)) {
                            alert(user, false, "R -> " + ratio + " Y -> " + yawDiff);
                        }
                    }
                }
            }
        }
        if (e instanceof ArmAnimationEvent) {
            miss++;
        }
        if (e instanceof FlyingInEvent) {
            ticks++;

            if (ticks >= 100) {
                attack = 0;
                miss = 0;
                user.setInBoxTicks(0);
            }
        }
    }
}
