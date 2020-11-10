package dev.demon.venom.impl.checks.other.badpackets;

import dev.demon.venom.api.check.Check;
import dev.demon.venom.api.check.CheckInfo;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.BlockPlaceEvent;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;
import dev.demon.venom.impl.events.inevents.SteerVehicleInEvent;
import dev.demon.venom.impl.events.outevents.RespawnOutEvent;

@CheckInfo(name = "BadPackets", type = "P", banvl = 10)
public class BadPacketsP extends Check {

    private boolean sentRespawn;

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof SteerVehicleInEvent) {
            if (!user.getPlayer().isInsideVehicle()) {
                alert(user, false, "Spoofing not in a vehicle");
            }
        }
    }
}