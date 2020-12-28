package dev.demon.venom.impl.checks.player.badpackets;

import dev.demon.venom.api.checknew.Check;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.tinyprotocol.packet.in.WrappedInBlockDigPacket;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.BlockDigEvent;
import dev.demon.venom.impl.events.inevents.CloseWindowInEvent;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;
import dev.demon.venom.impl.events.outevents.CloseWindowOutEvent;
import dev.demon.venom.utils.math.MathUtil;
import dev.demon.venom.utils.time.TimeUtils;
import org.bukkit.Bukkit;

public class BadPacketsH extends Check {
    public BadPacketsH(String checkname, String checktype, boolean experimental, int banVL, boolean enabled) {
        super(checkname, checktype, experimental, banVL, enabled);
    }

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof FlyingInEvent) {
            if (user.getMiscData().isInventoryOpen()) {
                if (user.getMovementData().getDeltaXZ() > MathUtil.getBaseSpeed(user.getPlayer())) {
                    if (violation++ > 10) {
                        handleDetection(user, "Moving in inventory");
                    }
                } else violation -= Math.min(violation, 0.25);
            }
        }
    }
}
