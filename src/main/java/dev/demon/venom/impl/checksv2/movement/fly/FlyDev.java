package dev.demon.venom.impl.checksv2.movement.fly;

import dev.demon.venom.api.check.Check;
import dev.demon.venom.api.check.CheckInfo;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.FlyingEvent;
import org.bukkit.Bukkit;


@CheckInfo(name = "Fly", type ="(DEV)")
public class FlyDev extends Check {

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof FlyingEvent && user.getConnectedTick() > 100) {
            Bukkit.broadcastMessage(""+(user.getMovementData().getTo().getY() - user.getMovementData().getFrom().getY()));
        }
    }
}