package dev.demon.venom.impl.checks.other.badpackets;

import dev.demon.venom.api.check.Check;
import dev.demon.venom.api.check.CheckInfo;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.ClickWindowInEvent;
import dev.demon.venom.impl.events.inevents.CloseWindowInEvent;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;
import dev.demon.venom.impl.events.inevents.UseEntityEvent;

@CheckInfo(name = "BadPackets", type = "I", banvl = 10)
public class BadPacketsI extends Check {

    private boolean closeWindow;

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof CloseWindowInEvent) {
            closeWindow = true;
        }

        if (e instanceof UseEntityEvent) {
            if (((UseEntityEvent) e).getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK) {
                if (closeWindow) {
                    alert(user, false, "Closed window while attacking (client)");
                }
            }
        }

        if (e instanceof FlyingInEvent) {
            closeWindow = false;
        }
    }
}
