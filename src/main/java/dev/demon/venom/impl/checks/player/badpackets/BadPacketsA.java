package dev.demon.venom.impl.checks.player.badpackets;

import dev.demon.venom.api.checknew.Check;
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
            if (Math.abs(((FlyingInEvent) e).getPitch()) > 90 && user.getMovementData().isChunkLoaded()) {
                handleDetection(user, "P -> "+((FlyingInEvent) e).getPitch());
            }
        }
    }
}
