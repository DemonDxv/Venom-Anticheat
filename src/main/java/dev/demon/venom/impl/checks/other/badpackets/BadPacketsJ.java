package dev.demon.venom.impl.checks.other.badpackets;

import dev.demon.venom.api.check.Check;
import dev.demon.venom.api.check.CheckInfo;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import dev.demon.venom.api.tinyprotocol.packet.outgoing.WrappedOutCloseWindowPacket;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.CloseWindowInEvent;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;
import dev.demon.venom.impl.events.inevents.UseEntityEvent;
import dev.demon.venom.impl.events.outevents.CloseWindowOutEvent;

@CheckInfo(name = "BadPackets", type = "J", banvl = 10)
public class BadPacketsJ extends Check {

    private boolean closeWindow;

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof CloseWindowOutEvent) {
            closeWindow = true;
        }

        if (e instanceof UseEntityEvent) {
            if (((UseEntityEvent) e).getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK) {
                if (closeWindow) {
                    alert(user, false, "Closed window while attacking (server)");
                }
            }
        }

        if (e instanceof FlyingInEvent) {
            closeWindow = false;
        }
    }
}
