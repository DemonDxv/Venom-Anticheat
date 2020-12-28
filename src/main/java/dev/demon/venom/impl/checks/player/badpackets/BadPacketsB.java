package dev.demon.venom.impl.checks.player.badpackets;

import dev.demon.venom.api.checknew.Check;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.ArmAnimationEvent;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;
import dev.demon.venom.impl.events.inevents.UseEntityEvent;

public class BadPacketsB extends Check {
    public BadPacketsB(String checkname, String checktype, boolean experimental, int banVL, boolean enabled) {
        super(checkname, checktype, experimental, banVL, enabled);
    }

    private boolean swing;

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (user.getLagProcessor().isLagging()) {
            return;
        }

        if (e instanceof FlyingInEvent) {
            swing = false;
        }

        if (e instanceof ArmAnimationEvent) {
            swing = true;
        }

        if (e instanceof UseEntityEvent) {
            if (((UseEntityEvent) e).getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK) {
                if (!swing) {
                    handleDetection(user, "Attacked without swinging.");
                }
            }
        }
    }
}
