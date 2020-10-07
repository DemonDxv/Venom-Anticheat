package dev.demon.venom.impl.checks.combat.reach;

import dev.demon.venom.Venom;
import dev.demon.venom.api.check.Check;
import dev.demon.venom.api.check.CheckInfo;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.UseEntityEvent;
import dev.demon.venom.utils.location.PlayerLocation;

@CheckInfo(name = "Reach", type = "B")
public class ReachB extends Check {

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof UseEntityEvent) {
            if (((UseEntityEvent) e).getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK) {
                if (user.getCombatData().getTransactionHits() > 1) {
                    alert(user, "TT -> "+user.getCombatData().getTransactionHits());
                }
            }
        }
    }
}
