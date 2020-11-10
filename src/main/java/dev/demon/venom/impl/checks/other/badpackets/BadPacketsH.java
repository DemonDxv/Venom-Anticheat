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

@CheckInfo(name = "BadPackets", type = "H", banvl = 10)
public class BadPacketsH extends Check {

    private boolean clickWindow;

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof ClickWindowInEvent) {
            clickWindow = true;
        }

        if (e instanceof UseEntityEvent) {
            if (((UseEntityEvent) e).getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK) {
                if (clickWindow) {
                    alert(user, false, "Clicked inside window while attacking");
                }
            }
        }

        if (e instanceof FlyingInEvent) {
            clickWindow = false;
        }
    }
}
