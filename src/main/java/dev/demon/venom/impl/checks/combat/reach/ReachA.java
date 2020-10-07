package dev.demon.venom.impl.checks.combat.reach;

import dev.demon.venom.Venom;
import dev.demon.venom.api.check.Check;
import dev.demon.venom.api.check.CheckInfo;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.FlyingEvent;
import dev.demon.venom.impl.events.RelMoveEvent;
import dev.demon.venom.impl.events.UseEntityEvent;
import dev.demon.venom.utils.location.PlayerLocation;
import dev.demon.venom.utils.math.MathUtil;
import org.bukkit.Bukkit;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

@CheckInfo(name = "Reach", type = "A")
public class ReachA extends Check {

    @Override
    public void onHandle(User user, AnticheatEvent e) {
       /* if (e instanceof FlyingEvent) {
            PlayerLocation location = new PlayerLocation(((FlyingEvent) e).getX(), ((FlyingEvent) e).getY(), ((FlyingEvent) e).getZ(), System.currentTimeMillis());

            user.getMovementData().setLocation(location);


            if (user.getMovementData().getLocation() != null) {
                user.getMovementData().setPreviousLocation(user.getMovementData().getLocation());
                user.getPreviousLocations().add(location);
            }


            user.getPreviousLocs().add(new PlayerLocation(((FlyingEvent) e).getX(), ((FlyingEvent) e).getY(), ((FlyingEvent) e).getZ(), ((FlyingEvent) e).getYaw(), ((FlyingEvent) e).getPitch()));
        }*/


        if (e instanceof UseEntityEvent) {
            if (((UseEntityEvent) e).getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK) {
                User targetUser = Venom.getInstance().getUserManager().getUser(((UseEntityEvent) e).getEntity().getUniqueId());

                if (targetUser != null && user != null) {
                    PlayerLocation location = user.getMovementData().getLocation();
                    PlayerLocation previousLocation = user.getMovementData().getPreviousLocation();


                    double recalc = targetUser.getPreviousLocs().stream().mapToDouble(vec -> vec.getEstimatedLocation(user, user.getLagProcessor().getCurrentPing(), 200).stream().mapToDouble(vec2 -> vec2.getDistanceSquared(location, previousLocation)).min().orElse(0.0)).min().orElse(0.0);

                    double range = targetUser.getPreviousLocs().stream().mapToDouble(vec -> vec.getDistanceSquared(location, previousLocation)).min().orElse(0.0);

                    range -= recalc;

                    if (range > 7 || user.getCombatData().cancelTicks > 0) {
                        return;
                    }

                    if (range > 3.0 && user.getCombatData().getTransactionHits() <= 1) {
                        alert(user, "R -> " + range);
                    }
                }
            }
        }
    }
}
