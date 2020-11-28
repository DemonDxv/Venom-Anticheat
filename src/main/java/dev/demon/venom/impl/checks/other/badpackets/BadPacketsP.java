package dev.demon.venom.impl.checks.other.badpackets;

import dev.demon.venom.api.check.Check;
import dev.demon.venom.api.check.CheckInfo;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.BlockPlaceEvent;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;
import dev.demon.venom.impl.events.inevents.SteerVehicleInEvent;
import dev.demon.venom.impl.events.outevents.RespawnOutEvent;
import dev.demon.venom.utils.time.TimeUtils;

@CheckInfo(name = "BadPackets", type = "P", banvl = 10)
public class BadPacketsP extends Check {

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof SteerVehicleInEvent) {
            if (TimeUtils.elapsed(user.getMiscData().getLastEjectVechielEject()) < 1000L) {
                return;
            }
            if (!user.getPlayer().isInsideVehicle()) {
                if (violation++ > 10) {
                    alert(user, false, "Spoofing not in a vehicle");
                }
            } else violation -= Math.min(violation, 0.75);
        }
    }
}