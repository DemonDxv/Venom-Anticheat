package dev.demon.venom.impl.checks.combat.killaura;

import dev.demon.venom.Venom;
import dev.demon.venom.api.check.Check;
import dev.demon.venom.api.check.CheckInfo;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;
import dev.demon.venom.impl.events.inevents.UseEntityEvent;
import dev.demon.venom.utils.box.BoundingBox;
import dev.demon.venom.utils.location.CustomLocation;
import dev.demon.venom.utils.location.PastLocation;
import dev.demon.venom.utils.location.RayTrace;
import dev.demon.venom.utils.math.MathUtil;
import dev.demon.venom.utils.math.Verbose;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;


@CheckInfo(name = "Killaura", type = "C", banvl = 5)
public class KillauraC extends Check {

    private Verbose verbose = new Verbose();
    private PastLocation hitBoxPastLocations = new PastLocation();

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof UseEntityEvent) {
            if (((UseEntityEvent) e).getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK) {
                List<BoundingBox> boundingBoxList = new ArrayList<>();
                List<CustomLocation> pastLocation = hitBoxPastLocations.getEstimatedLocation(user.getLagProcessor().getLastTransaction(), Math.abs(user.getLagProcessor().getLastTransaction() - user.getLagProcessor().getLastLastTransaction()) + 200);
                if (pastLocation.size() > 0) {

                    if (Venom.getInstance().isLagging() || user.getLagProcessor().isTotalLag() || user.getCombatData().cancelTicks > 3 || (user.getCombatData().getTargetUser() != null && user.getCombatData().getTargetUser().getMovementData().isCollidesHorizontally()) || (user.getCombatData().getLastEntityAttacked() != null && user.getPlayer().getLocation().distance(user.getCombatData().getLastEntityAttacked().getLocation()) < 1.3)) {
                        verbose.setVerbose(0);
                        return;
                    }

                    Location loc = user.getMovementData().getTo().clone().toLocation(user.getPlayer().getWorld());

                    LivingEntity livingEntity = (LivingEntity) ((UseEntityEvent) e).getEntity();

                    pastLocation.forEach(loc1 -> boundingBoxList.add(MathUtil.getHitbox(livingEntity, loc1, user)));

                    loc.setY(loc.getY() + (user.getPlayer().isSneaking() ? 1.53 : user.getPlayer().getEyeHeight()));

                    RayTrace trace = new RayTrace(loc.toVector(), user.getPlayer().getEyeLocation().getDirection());

                    boolean interspects = boundingBoxList.stream().noneMatch(box -> trace.intersects(box, box.getMinimum().distance(loc.toVector()) + 1.0, 0.2));

                    if (interspects && verbose.flag(8, 10000L)) {
                        alert(user, false,"BBS -> " + boundingBoxList.size());
                    } else {
                        user.setInBoxTicks(user.getInBoxTicks() + 1);
                    }
                }
            }
        }
        if (e instanceof FlyingInEvent) {
            if (user.getCombatData().getLastEntityAttacked() != null) {
                hitBoxPastLocations.addLocation(user.getCombatData().getLastEntityAttacked().getLocation());
            }
        }
    }
}