package dev.demon.venom.impl.checks.combat.velocity;

import dev.demon.venom.api.check.Check;
import dev.demon.venom.api.check.CheckInfo;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;
import dev.demon.venom.impl.events.outevents.VelocityOutEvent;
import dev.demon.venom.utils.math.MathUtil;
import dev.demon.venom.utils.time.TimeUtils;
import org.bukkit.Bukkit;

@CheckInfo(name = "Velocity", type = "H", banvl = 20)
public class VelocityH extends Check {

    private long flying;

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof FlyingInEvent) {

            if (user.generalCancel()
                    || TimeUtils.elapsed(user.getMovementData().getLastTeleport()) < 5000L
                    || TimeUtils.elapsed(user.getMovementData().getLastFallDamage()) < 1000L) {
                return;
            }

            flying = System.currentTimeMillis();
        }
        if (e instanceof VelocityOutEvent) {
            if (((VelocityOutEvent) e).getId() == user.getPlayer().getEntityId()) {
                boolean post = MathUtil.isPost(flying);
                boolean pre = MathUtil.isPre(flying);

             //   Bukkit.broadcastMessage("Post -> " + post + " Pre -> " + pre);
                if (post) {
                    alert(user, true, "Post Velocity?");
                }
            }
        }
    }
}
