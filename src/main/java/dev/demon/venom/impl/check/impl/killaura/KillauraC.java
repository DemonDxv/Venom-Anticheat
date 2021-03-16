package dev.demon.venom.impl.check.impl.killaura;

import dev.demon.venom.impl.check.Check;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.*;

public class KillauraC extends Check {

    public KillauraC(String checkname, String checktype, boolean experimental, int banVL, boolean enabled) {
        super(checkname, checktype, experimental, banVL, enabled);
    }

    private boolean block;

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof FlyingInEvent) {
            block = false;
        }
        if (e instanceof BlockPlaceEvent || e instanceof BlockDigEvent) {
            if (user.getPlayer().getItemInHand() != null) {
                if (user.getMiscData().isSword(user.getPlayer().getItemInHand())) {
                    block = true;
                }
            }
        }
        if (e instanceof UseEntityEvent) {
            if (((UseEntityEvent) e).getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK) {
                if (block) {
                    handleDetection(user, "Blocking and attacking at the same time.");
                }
            }
        }
    }
}
