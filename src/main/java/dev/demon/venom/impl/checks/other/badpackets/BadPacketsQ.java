package dev.demon.venom.impl.checks.other.badpackets;

import dev.demon.venom.api.check.Check;
import dev.demon.venom.api.check.CheckInfo;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;
import dev.demon.venom.impl.events.inevents.SteerVehicleInEvent;
import org.bukkit.Bukkit;

@CheckInfo(name = "BadPackets", type = "Q", banvl = 10)
public class BadPacketsQ extends Check {

    private double lastY;

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof FlyingInEvent) {

            double packetY = Math.abs(((FlyingInEvent) e).getY() - lastY);
            double serverY = Math.abs(user.getMovementData().getTo().getY() - user.getMovementData().getFrom().getY());



            if (Math.abs(packetY - serverY) >= 0 && !user.getMovementData().isClientGround() && user.getMovementData().isLastClientGround()) {
                if (violation++ > 0) {
                    alert(user, false, "PYSY -> "+Math.abs(packetY - serverY));
                }
            } else violation -= Math.min(violation, 0.5);



            lastY = ((FlyingInEvent) e).getY();
        }
    }
}