package dev.demon.venom.impl.checks.combat.reach;

import dev.demon.venom.Venom;
import dev.demon.venom.api.check.Check;
import dev.demon.venom.api.check.CheckInfo;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;
import dev.demon.venom.impl.events.inevents.UseEntityEvent;
import dev.demon.venom.utils.location.PlayerLocation;
import org.bukkit.Bukkit;

@CheckInfo(name = "Reach", type = "A", banvl = 5)
public class ReachA extends Check {

    private String string = "Yes I know this is skidded.";

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof FlyingInEvent) {
            PlayerLocation location = new PlayerLocation(((FlyingInEvent) e).getX(), ((FlyingInEvent) e).getY(), ((FlyingInEvent) e).getZ(), System.currentTimeMillis());

            user.getMovementData().setLocation(location);


            if (user.getMovementData().getLocation() != null) {
                user.getMovementData().setPreviousLocation(user.getMovementData().getLocation());
            }

            if (string != string) {

            }


            user.getPreviousLocations().add(location);

            if (user.getPreviousLocations().size() > 8) {
                user.getPreviousLocations().removeFirst();
            }

        }


        if (e instanceof UseEntityEvent) {
            if (((UseEntityEvent) e).getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK) {

                User targetUser = Venom.getInstance().getUserManager().getUser(((UseEntityEvent) e).getEntity().getUniqueId());

                if (targetUser != null && user != null) {
                    PlayerLocation location = user.getMovementData().getLocation();
                    PlayerLocation previousLocation = user.getMovementData().getPreviousLocation();

                    double range = Math.sqrt(targetUser.getPreviousLocations().stream()
                            .mapToDouble(box -> box.getDistanceSquared(location))
                            .min()
                            .orElse(0D));

                    float playerYaw = user.getMovementData().getTo().getYaw();
                    float playerPitch = user.getMovementData().getTo().getPitch();

                    float targetYaw = targetUser.getMovementData().getTo().getYaw();
                    float targetPitch = targetUser.getMovementData().getTo().getPitch();

                    double offsetX = -Math.cos(Math.toRadians(targetPitch)) * Math.sin(Math.toRadians(targetYaw)) *
                            -Math.cos(Math.toRadians(playerPitch)) * Math.sin(Math.toRadians(playerYaw));

                    double offsetY = -Math.sin(Math.toRadians(playerPitch)) * -Math.sin(Math.toRadians(targetPitch));

                    double offsetZ = Math.cos(Math.toRadians(targetPitch)) * Math.cos(Math.toRadians(targetYaw)) *
                            Math.cos(Math.toRadians(playerPitch)) * Math.cos(Math.toRadians(playerYaw));

                    double threshold = 3.1;

                    if (offsetX + offsetY + offsetZ > 0.4) {
                        threshold += 0.4;
                    }

                    if (range > 6.5) {
                        return;
                    }

                //    Bukkit.broadcastMessage(""+range);

                    if (range > threshold) {
                        if ((violation += range - 2.25) > 5D) {
                            alert(user, false, "R -> "+range);
                        }
                    } else {
                        violation -= Math.min(violation, 0.1);
                    }
                }
            }
        }
    }
}
