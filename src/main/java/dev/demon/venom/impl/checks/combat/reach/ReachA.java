package dev.demon.venom.impl.checks.combat.reach;

import dev.demon.venom.Venom;
import dev.demon.venom.api.checknew.Check;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.UseEntityEvent;
import dev.demon.venom.utils.location.PlayerLocation;

public class ReachA extends Check {
    public ReachA(String checkname, String checktype, boolean experimental, int banVL, boolean enabled) {
        super(checkname, checktype, experimental, banVL, enabled);
    }

    @Override
    public void onHandle(User user, AnticheatEvent e) {

        if (e instanceof UseEntityEvent) {

            if (user != null) {
                User targetUser = Venom.getInstance().getUserManager().getUser(((UseEntityEvent) e).getEntity().getUniqueId());

                if (targetUser != null) {

                    if (((UseEntityEvent) e).getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK) {

                        PlayerLocation location = user.getMovementData().getLocation();
                        PlayerLocation previousLocation = user.getMovementData().getPreviousLocation();

                        double range = targetUser.getPreviousLocations().stream()
                                .mapToDouble(box -> box.getDistanceSquaredCentered(location, previousLocation))
                                .min()
                                .orElse(0D);

                        if (range > 3.0 && user.getCombatData().getTransactionHits() == 0) {
                            handleDetection(user, "Reach -> " + range);
                        }
                    }
                }
            }
        }
    }
}
