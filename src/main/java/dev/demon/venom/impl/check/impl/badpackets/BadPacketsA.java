package dev.demon.venom.impl.check.impl.badpackets;

import dev.demon.venom.impl.check.Check;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;

public class BadPacketsA extends Check {
    public BadPacketsA(String checkname, String checktype, boolean experimental, int banVL, boolean enabled) {
        super(checkname, checktype, experimental, banVL, enabled);
    }

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof FlyingInEvent) {
            if (user.getBlockData().lastInsideBlockTimer.hasNotPassed(20)
                    || user.generalCancel()) {
                return;
            }

            float maxPitch = user.getBlockData().climbableTicks > 0 ? 91.1f : 90f;

            if (Math.abs(((FlyingInEvent) e).getPitch()) > maxPitch && user.getMovementData().isChunkLoaded()) {
                handleDetection(user, "P -> "+((FlyingInEvent) e).getPitch());
            }
        }
    }
}
