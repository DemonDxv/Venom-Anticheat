package dev.demon.venom.impl.checks.combat.killaura;

import dev.demon.venom.api.checknew.Check;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;
import dev.demon.venom.impl.events.inevents.UseEntityEvent;
import dev.demon.venom.utils.math.MathUtil;
import dev.demon.venom.utils.time.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KillauraG extends Check {

    public KillauraG(String checkname, String checktype, boolean experimental, int banVL, boolean enabled) {
        super(checkname, checktype, experimental, banVL, enabled);
    }

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof FlyingInEvent) {
            double deltaPitch = Math.abs(user.getMovementData().getTo().getPitch() - user.getMovementData().getFrom().getPitch());

            if (TimeUtils.elapsed(user.getCombatData().getLastUseEntityPacket()) < 100L) {
                if (deltaPitch == MathUtil.round(deltaPitch, 1) && deltaPitch > 0.0) {
                    handleDetection(user, "Rounded Pitch");
                }
            }
        }
    }
}
