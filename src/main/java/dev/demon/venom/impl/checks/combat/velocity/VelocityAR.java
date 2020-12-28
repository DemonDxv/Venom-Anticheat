package dev.demon.venom.impl.checks.combat.velocity;

import dev.demon.venom.api.checknew.Check;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.tinyprotocol.api.TinyProtocolHandler;
import dev.demon.venom.api.tinyprotocol.packet.in.WrappedInBlockDigPacket;
import dev.demon.venom.api.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import dev.demon.venom.api.tinyprotocol.packet.outgoing.WrappedOutTransaction;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.*;
import dev.demon.venom.impl.events.outevents.VelocityOutEvent;
import dev.demon.venom.utils.location.CustomLocation;
import dev.demon.venom.utils.time.TimeUtils;
import org.bukkit.Bukkit;

public class VelocityAR extends Check {

    public VelocityAR(String checkname, String checktype, boolean experimental, int banVL, boolean enabled) {
        super(checkname, checktype, experimental, banVL, enabled);
    }

    private double horizontalVL, velocityX, velocityZ;
    private boolean blocking;

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof VelocityOutEvent) {

            if (((VelocityOutEvent) e).getId() == user.getPlayer().getEntityId()) {
                WrappedOutTransaction packet =
                        new WrappedOutTransaction(0, user.getMiscData().randomTransactionIDVelocity(), false);

                TinyProtocolHandler.getInstance().getChannel().sendPacket(user.getPlayer(), packet);

                velocityX = ((VelocityOutEvent) e).getX();
                velocityZ = ((VelocityOutEvent) e).getZ();
            }
        }

        if (e instanceof BlockDigEvent) {
            if (((BlockDigEvent) e).getAction() == WrappedInBlockDigPacket.EnumPlayerDigType.RELEASE_USE_ITEM) {
                if (user.getMiscData().isSword(user.getPlayer().getItemInHand())) {
                    blocking = false;
                }
            }
        }

        if (e instanceof BlockPlaceEvent) {
            if (user.getMiscData().isSword(user.getPlayer().getItemInHand())) {
                blocking = true;
            }
        }

        if (e instanceof FlyingInEvent) {
            if (blocking) {
                velocityX *= 0.6F;
                velocityZ *= 0.6F;
            }

            if (TimeUtils.elapsed(user.getCombatData().getLastBowDamage()) < 1000L) {
                velocityX *= 0.6F;
                velocityZ *= 0.6F;
            }

            if (TimeUtils.elapsed(user.getCombatData().getLastBowDamage()) < 1000L) {
                velocityX *= 0.6F;
                velocityZ *= 0.6F;
            }
        }

        if (e instanceof UseEntityEvent) {
            if (((UseEntityEvent) e).getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK
                    || user.getMovementData().isLastSprint()) {
                velocityX *= 0.6F;
                velocityZ *= 0.6F;
            }
            if (((UseEntityEvent) e).getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK) {
                blocking = false;
            }
        }

        if (e instanceof TransactionEvent) {
            short id = ((TransactionEvent) e).getAction();
            short currentIDVelocity = user.getMiscData().getTransactionIDVelocity();

            if (id == currentIDVelocity) {
                horizontalVL = Math.hypot(velocityX, velocityZ);
            }
        }

        if (e instanceof FlyingInEvent) {

            CustomLocation to = user.getMovementData().getTo(), from = user.getMovementData().getFrom();

            double deltaXZ = Math.hypot(to.getX() - from.getX(), to.getZ() - from.getZ());

            if (user.getVelocityData().getVelocityTicks() == 1
                    && TimeUtils.elapsed(user.getCombatData().getLastEntityDamage()) < 1000L) {
                if (user.getMovementData().isLastClientGround()) {
                    if ((deltaXZ / horizontalVL) <= 0.995) {
                        handleDetection(user, "Horizontal Velocity -> "+(deltaXZ / horizontalVL));
                    }
                }
            }
        }
    }
}
