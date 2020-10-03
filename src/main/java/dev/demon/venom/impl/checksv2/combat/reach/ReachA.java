package dev.demon.venom.impl.checksv2.combat.reach;

import dev.demon.venom.Venom;
import dev.demon.venom.api.check.Check;
import dev.demon.venom.api.check.CheckInfo;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.FlyingEvent;
import dev.demon.venom.impl.events.UseEntityEvent;
import dev.demon.venom.utils.location.PlayerLocation;


import java.util.ArrayList;
import java.util.List;

@CheckInfo(name = "Reach", type = "A")
public class ReachA extends Check {

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof FlyingEvent) {
            PlayerLocation location = new PlayerLocation(((FlyingEvent) e).getX(), ((FlyingEvent) e).getY(), ((FlyingEvent) e).getZ(), System.currentTimeMillis());

            user.getMovementData().setLocation(location);
            user.getPreviousLocations().add(location);

            if (user.getPreviousLocations().size() >= 10) {
                user.getPreviousLocations().removeFirst();
            }
        }

        if (e instanceof UseEntityEvent) {
            if (((UseEntityEvent) e).getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK) {
                User targetUser = Venom.getInstance().getUserManager().getUser(((UseEntityEvent) e).getEntity().getUniqueId());

                if (targetUser != null && user != null) {
                    PlayerLocation location = user.getMovementData().getLocation();

                    List<PlayerLocation> pastLocation = new ArrayList<>(targetUser.getMovementData().getLocation().getEstimatedLocation(targetUser, targetUser.getLagProcessor().getCurrentPing(), 200));

                    float range = (float) pastLocation.stream().mapToDouble(vec -> vec.getDistanceSquared(location)).min().orElse(0.0);

                    range -= (targetUser.getMovementData().getDeltaXZ() * 0.91F + 0.026F);

                    if (range > 6.5) {
                        violation = 0;
                        return;
                    }

                    if (range > 3.0) {
                        if ((violation += 1.25) > 2.5) {
                            alert(user, "R -> "+range);
                        }
                    } else violation -= Math.min(violation, 1);
                }
            }
        }
    }
}
