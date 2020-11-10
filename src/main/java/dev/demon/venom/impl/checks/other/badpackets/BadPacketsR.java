package dev.demon.venom.impl.checks.other.badpackets;

import dev.demon.venom.api.check.Check;
import dev.demon.venom.api.check.CheckInfo;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;
import org.bukkit.Bukkit;

@CheckInfo(name = "BadPackets", type = "R", banvl = 5)
public class BadPacketsR extends Check {

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof FlyingInEvent) {

            if (user.getMovementData().isClientGround() && user.getPlayer().isOnGround() && !user.getMovementData().isOnGround() && user.getConnectedTick() > 250) {
                if (violation++ > 0.0) {
                    alert(user, false, "Spoofing Client Ground");
                }
            } else violation -= Math.min(violation, 0.5);

        }
    }
}