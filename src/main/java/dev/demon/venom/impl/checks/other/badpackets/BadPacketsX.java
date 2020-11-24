package dev.demon.venom.impl.checks.other.badpackets;

import dev.demon.venom.Venom;
import dev.demon.venom.api.check.Check;
import dev.demon.venom.api.check.CheckInfo;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;
import dev.demon.venom.utils.location.CustomLocation;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

@CheckInfo(name = "BadPackets", type = "X", banvl = 100)
public class BadPacketsX extends Check {

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof FlyingInEvent) {
            if (((FlyingInEvent) e).isLook() && !((FlyingInEvent) e).isPos()) {
                CustomLocation to = user.getMovementData().getTo(), from = user.getMovementData().getFrom();
                double pitch = Math.abs(to.getPitch() - from.getPitch());
                double yaw = Math.abs(to.getYaw() - from.getYaw());

                double deltaXZ = Math.hypot(to.getX() - from.getX(), to.getZ() - from.getZ());

                if (yaw == 0 || pitch == 0 || deltaXZ > 0) {
                    alert(user, true, "Not Moving Head With Packet [type 1]");
                }
            }
        }
    }
}