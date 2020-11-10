package dev.demon.venom.impl.checks.movement.fly;

import dev.demon.venom.api.check.Check;
import dev.demon.venom.api.check.CheckInfo;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;
import dev.demon.venom.utils.time.TimeUtils;
import org.bukkit.Bukkit;

@CheckInfo(name = "Fly", type = "B", banvl = 10)
public class FlyB extends Check {
    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof FlyingInEvent) {

            if (user.generalCancel()
                    || user.getBlockData().blockAboveTicks > 0
                    || !user.isSafe()
                    || TimeUtils.elapsed(user.getMovementData().getLastTeleport()) < 1000L) {
                return;
            }

            boolean posYground = user.getMovementData().getTo().getY() % 0.015625 == 0.0;
            boolean clientGround = user.getMovementData().isClientGround();

            boolean hasVal = hasValue(user.getMovementData().getTo().getY());

            if (hasVal) {
                if (user.getBlockData().chestTicks > 0
                        || user.getBlockData().wallTicks > 0
                        || user.getBlockData().bedTicks > 0
                        || user.getBlockData().stairTicks > 0
                        || user.getBlockData().slabTicks > 0
                        || user.getBlockData().fenceTicks > 0
                        || user.getBlockData().soulSandTicks > 0
                        || user.getBlockData().snowTicks > 0) {
                    return;
                }
            }

            if (!clientGround && posYground && user.getConnectedTick() > 100) {
                alert(user, false, "Spoofing off ground");
            }
        }
    }

    public boolean hasValue(double posY) {
        String posYString = String.valueOf(posY);
        if (posYString.endsWith(".06250")
                || posYString.endsWith(".12500")
                || posYString.endsWith(".25000")
                || posYString.endsWith(".37500")
                || posYString.endsWith(".5")
                || posYString.endsWith(".56250")
                || posYString.endsWith(".62500")
                || posYString.endsWith(".7500")
                || posYString.endsWith(".87500")) {
            return true;
        }
        return false;
    }
}
