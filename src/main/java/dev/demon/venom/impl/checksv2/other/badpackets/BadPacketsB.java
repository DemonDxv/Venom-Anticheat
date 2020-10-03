package dev.demon.venom.impl.checksv2.other.badpackets;

import dev.demon.venom.api.check.Check;
import dev.demon.venom.api.check.CheckInfo;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.BlockDigEvent;
import dev.demon.venom.impl.events.BlockSentEvent;
import dev.demon.venom.impl.events.FlyingEvent;
import dev.demon.venom.impl.events.UseEntityEvent;


@CheckInfo(name = "BadPackets", type = "B")
public class BadPacketsB extends Check {

    private boolean block;

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof FlyingEvent) {
            block = false;
        }
        if (e instanceof BlockDigEvent || e instanceof BlockSentEvent) {
            block = true;
        }

        if (e instanceof UseEntityEvent) {
            if (((UseEntityEvent) e).getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK) {
                if (block && !user.getLagProcessor().isLagging()) {
                    alert(user, "B -> " + block);
                }
            }
        }
    }
}
