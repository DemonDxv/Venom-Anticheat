package dev.demon.venom.impl.checks.player.badpackets;

import dev.demon.venom.api.checknew.Check;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;
import dev.demon.venom.utils.time.TimeUtils;

public class BadPacketsJ extends Check {
    public BadPacketsJ(String checkname, String checktype, boolean experimental, int banVL, boolean enabled) {
        super(checkname, checktype, experimental, banVL, enabled);
    }

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof FlyingInEvent) {

            if (user.generalCancel()
                    || user.getMovementData().getFallDamageTimer().hasNotPassed(20)
                    || user.getBlockData().pistionTick > 0) {
                return;
            }

            double deltaY = user.getMovementData().getTo().getY() - user.getMovementData().getFrom().getY();

            if (deltaY <= -1 && user.getMovementData().getAirTicks() < 8) {
                handleDetection(user, "Falling Rapidly");
            }

        }
    }
}
