package dev.demon.venom.impl.checks.other.badpackets;

import dev.demon.venom.api.check.Check;
import dev.demon.venom.api.check.CheckInfo;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.ArmAnimationEvent;
import dev.demon.venom.impl.events.FlyingEvent;
import dev.demon.venom.impl.events.UseEntityEvent;


@CheckInfo(name = "BadPackets", type = "C")
public class BadPacketsC extends Check {

    private boolean swing;

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof FlyingEvent) {
            swing = false;
        }
        if (e instanceof ArmAnimationEvent) {
            swing = true;
        }

        if (e instanceof UseEntityEvent) {
            if (((UseEntityEvent) e).getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK) {
                if (!swing && !user.getLagProcessor().isLagging()) {
                    alert(user, "S -> " + swing);
                }
            }
        }
    }
}
