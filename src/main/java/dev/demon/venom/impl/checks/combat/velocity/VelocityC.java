package dev.demon.venom.impl.checks.combat.velocity;

import dev.demon.venom.api.check.Check;
import dev.demon.venom.api.check.CheckInfo;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;
import dev.demon.venom.utils.time.TimeUtils;

import java.util.Map;

@CheckInfo(name = "Velocity", type = "C", banvl = 3)
public class VelocityC extends Check {
    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof FlyingInEvent && user.getConnectedTick() > 250) {

            if (user.generalCancel() || TimeUtils.elapsed(user.getMovementData().getLastTeleport()) < 5000L) {
                return;
            }

            if (user.getVelocityData().getVelocityTicks() < 5) {
                for (Map.Entry<Double, Short> doubleShortEntry : user.getVelocityData().getLastVelocityVertical().entrySet()) {

                    if (user.getMiscData().getTransactionIDVelocity() == doubleShortEntry.getValue()) {
                        user.getVelocityData().setVerticalVelocityTrans((Double) ((Map.Entry) doubleShortEntry).getKey());
                        user.getVelocityData().getLastVelocityVertical().clear();
                    }
                }
            }

            double deltaY = user.getMovementData().getTo().getY() - user.getMovementData().getFrom().getY();

            double ratio = Math.abs(deltaY / user.getVelocityProcessor().getVerticalTransaction());

            if (user.getVelocityData().getVelocityTicks() == 1) {
                if (ratio <= 0.9998 && deltaY <= 0.42F && user.getVelocityProcessor().getVerticalTransaction() < 1
                        && !user.getMovementData().isClientGround() && user.getMovementData().isLastClientGround()) {
                    alert(user, true,"VV -> "+ratio + "%");
                }
            }
        }
    }
}
