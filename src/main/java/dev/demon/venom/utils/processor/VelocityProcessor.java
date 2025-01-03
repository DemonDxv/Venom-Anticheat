package dev.demon.venom.utils.processor;

import dev.demon.venom.api.tinyprotocol.api.Packet;
import dev.demon.venom.api.tinyprotocol.api.TinyProtocolHandler;
import dev.demon.venom.api.tinyprotocol.packet.in.WrappedInFlyingPacket;
import dev.demon.venom.api.tinyprotocol.packet.in.WrappedInTransactionPacket;
import dev.demon.venom.api.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import dev.demon.venom.api.tinyprotocol.packet.outgoing.WrappedOutTransaction;
import dev.demon.venom.api.tinyprotocol.packet.outgoing.WrappedOutVelocityPacket;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.BlockDigEvent;
import dev.demon.venom.impl.events.inevents.BlockPlaceEvent;
import dev.demon.venom.utils.math.MathUtil;
import dev.demon.venom.utils.time.TimeUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;


@Getter
@Setter
public class VelocityProcessor {
    private User user;
    public double velocityX, velocityY, velocityZ, horizontal, vertical, horizontalTransaction, verticalTransaction;
    private boolean blocking;
    public int ticksSinceVelocity;
    public HashMap<Double, Short> lastVelocityVertical = new HashMap(), lastVelocityHorizontal = new HashMap();

    public void update(Object packet, String type) {
        if (user != null) {
            if (type.equalsIgnoreCase(Packet.Server.ENTITY_VELOCITY)) {
                WrappedOutVelocityPacket wrappedOutVelocityPacket = new WrappedOutVelocityPacket(packet, user.getPlayer());

                if (wrappedOutVelocityPacket.getId() == user.getPlayer().getEntityId()) {

                    user.getEntityActionProcessor().addAction(EntityActionProcessor.ActionType.VELOCITY);


                    user.getLagProcessor().setHitTime(System.currentTimeMillis());

                    ticksSinceVelocity = 0;


                    user.getVelocityData().setLastVelocity(System.currentTimeMillis());

                    velocityX = wrappedOutVelocityPacket.getX();
                    velocityY = wrappedOutVelocityPacket.getY();
                    velocityZ = wrappedOutVelocityPacket.getZ();


                    horizontal = MathUtil.hypot(velocityX, velocityZ);
                    vertical = Math.pow(velocityY + 2.0, 2.0) * 5.0;


                    if (user.getMovementData().isClientGround()) {
                        vertical = velocityY;
                    }

                    user.getCombatData().setLastVelocitySqr((Math.sqrt(velocityX * velocityX + velocityZ * velocityZ) * 0.2));

                    WrappedOutTransaction wrappedOutTransaction = new WrappedOutTransaction(0, user.getMiscData().getTransactionIDVelocity(), false);
                    TinyProtocolHandler.getInstance().getChannel().sendPacket(user.getPlayer(), wrappedOutTransaction.getObject());
                }
            }

            if (type.equalsIgnoreCase(Packet.Client.BLOCK_PLACE)) {
                if (user.getMiscData().isSword(user.getPlayer().getItemInHand())) {
                    blocking = true;
                }
            }

            if (type.equalsIgnoreCase(Packet.Client.BLOCK_DIG)) {
                if (user.getMiscData().isSword(user.getPlayer().getItemInHand())) {
                    blocking = false;
                }
            }

            if (type.equalsIgnoreCase(Packet.Client.POSITION) || type.equalsIgnoreCase(Packet.Client.POSITION_LOOK) || type.equalsIgnoreCase(Packet.Client.LOOK) || type.equalsIgnoreCase(Packet.Client.FLYING)) {
                if (user.getVelocityData().getVelocityTicks() < 5) {
                    for (Map.Entry<Double, Short> doubleShortEntry : getLastVelocityVertical().entrySet()) {

                        if (user.getMiscData().getTransactionIDVelocity() == doubleShortEntry.getValue()) {
                            setVerticalTransaction((Double) ((Map.Entry) doubleShortEntry).getKey());
                            getLastVelocityVertical().clear();
                        }
                    }
                }

                if (user.getVelocityData().getVelocityTicks() < 5) {
                    for (Map.Entry<Double, Short> doubleShortEntry : getLastVelocityHorizontal().entrySet()) {

                        if (user.getMiscData().getTransactionIDVelocity() == doubleShortEntry.getValue()) {
                            setHorizontalTransaction((Double) ((Map.Entry) doubleShortEntry).getKey());
                            getLastVelocityHorizontal().clear();
                        }
                    }
                }
            }

            if (type.equalsIgnoreCase(Packet.Client.USE_ENTITY)) {
                WrappedInUseEntityPacket useEntityPacket = new WrappedInUseEntityPacket(packet, user.getPlayer());
                if (useEntityPacket.getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK || user.getMovementData().isLastSprint()) {
                    velocityX *= 0.6F;
                    velocityZ *= 0.6F;
                }
            }

            if (type.equalsIgnoreCase(Packet.Client.TRANSACTION)) {
                WrappedInTransactionPacket wrappedInTransactionPacket = new WrappedInTransactionPacket(packet, user.getPlayer());

                short id = wrappedInTransactionPacket.getAction();
                short currentIDVelocity = user.getMiscData().getTransactionIDVelocity();


                if (id == currentIDVelocity) {
                    user.getLagProcessor().setVelocityPing((System.currentTimeMillis() - user.getLagProcessor().getHitTime()));
                    lastVelocityHorizontal.put(MathUtil.hypot(velocityX, velocityZ), currentIDVelocity);
                    lastVelocityVertical.put(vertical, currentIDVelocity);
                    user.getVelocityData().setVelocityTicks(0);
                    ticksSinceVelocity = 100;
                }
            }
        }
    }
}
