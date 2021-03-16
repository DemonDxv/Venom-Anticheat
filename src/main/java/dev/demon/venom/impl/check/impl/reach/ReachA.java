package dev.demon.venom.impl.check.impl.reach;

import dev.demon.venom.Venom;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.check.Check;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;
import dev.demon.venom.impl.events.inevents.UseEntityEvent;
import dev.demon.venom.utils.location.CustomLocation;
import dev.demon.venom.utils.location.PastLocation;
import dev.demon.venom.utils.location.PlayerLocation;

import java.util.List;

public class ReachA extends Check {
    public ReachA(String checkname, String checktype, boolean experimental, int banVL, boolean enabled) {
        super(checkname, checktype, experimental, banVL, enabled);
    }


    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof UseEntityEvent) {
            if (((UseEntityEvent) e).getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK) {
                User target = Venom.getInstance().getUserManager().getUser(((UseEntityEvent) e).getEntity().getUniqueId());
                if (user.getMovementProcessor().getTrackedPositions() != null) {
                    CustomLocation current = user.getMovementData().getTo();
                    CustomLocation lastLocation = user.getMovementData().getFrom();

                    double range = user.getMovementProcessor().getTrackedPositions().stream()
                            .mapToDouble(position -> getReachRange(current, lastLocation, target.getMovementData().getTo()))
                            .min().orElse(0.0);

                    if (range > 3.01) {
                        handleDetection(user, ""+range);
                    }
                }
            }
        }

        if (e instanceof FlyingInEvent) {
        }
    }

    public double getReachRange(CustomLocation playerLocation, CustomLocation lastLocation, CustomLocation targetLocation) {
        return targetLocation.getDistanceSquared(playerLocation, lastLocation);
    }
}
