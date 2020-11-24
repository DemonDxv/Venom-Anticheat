package dev.demon.venom.utils.processor;

import dev.demon.venom.api.tinyprotocol.api.Packet;
import dev.demon.venom.api.tinyprotocol.api.TinyProtocolHandler;
import dev.demon.venom.api.tinyprotocol.packet.in.WrappedInTransactionPacket;
import dev.demon.venom.api.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import dev.demon.venom.api.tinyprotocol.packet.outgoing.WrappedOutTransaction;
import dev.demon.venom.api.tinyprotocol.packet.outgoing.WrappedOutVelocityPacket;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.BlockDigEvent;
import dev.demon.venom.impl.events.inevents.BlockPlaceEvent;
import dev.demon.venom.utils.math.MathUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;


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


                    WrappedOutTransaction wrappedOutTransaction = new WrappedOutTransaction(0, user.getMiscData().getTransactionIDVelocity(), false);
                    TinyProtocolHandler.getInstance().getChannel().sendPacket(user.getPlayer(), wrappedOutTransaction.getObject());

                    user.getLagProcessor().setHitTime(System.currentTimeMillis());

                    ticksSinceVelocity = 0;


                    user.getVelocityData().setLastVelocity(System.currentTimeMillis());

                    velocityX = wrappedOutVelocityPacket.getX();
                    velocityY = wrappedOutVelocityPacket.getY();
                    velocityZ = wrappedOutVelocityPacket.getZ();

                    //user.getVelocityData().setVelocityX(user.getVelocityData().getVelocityX() * 0.6D);
                    //user.getVelocityData().setVelocityZ(user.getVelocityData().getVelocityZ() * 0.6D);

                    horizontal = MathUtil.hypot(velocityX, velocityZ);
                    vertical = Math.pow(velocityY + 2.0, 2.0) * 5.0;


                    if (user.getMovementData().isClientGround()) {
                        vertical = velocityY;
                    }

                    user.getCombatData().setLastVelocitySqr((Math.sqrt(velocityX * velocityX + velocityZ * velocityZ) * 0.2));


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
                if (blocking) {
                    velocityX *= 0.6F;
                    velocityZ *= 0.6F;
                }
                ticksSinceVelocity++;
            }

            if (type.equalsIgnoreCase(Packet.Client.USE_ENTITY)) {
                WrappedInUseEntityPacket useEntityPacket = new WrappedInUseEntityPacket(packet, user.getPlayer());
                if (useEntityPacket.getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK || user.getMovementData().isLastSprint()) {
                    velocityX *= 0.6F;
                    velocityZ *= 0.6F;
                }
                if (useEntityPacket.getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK) {
                    blocking = false;
                }
            }

            if (type.equalsIgnoreCase(Packet.Client.TRANSACTION)) {
                WrappedInTransactionPacket wrappedInTransactionPacket = new WrappedInTransactionPacket(packet, user.getPlayer());

                short id = wrappedInTransactionPacket.getAction();
                short currentIDVelocity = user.getMiscData().getTransactionIDVelocity();


                if (id == currentIDVelocity) {
                    user.getLagProcessor().setVelocityPing((System.currentTimeMillis() - user.getLagProcessor().getHitTime()));
                    lastVelocityHorizontal.put(horizontal, currentIDVelocity);
                    lastVelocityVertical.put(vertical, currentIDVelocity);
                    user.getVelocityData().setVelocityTicks(0);
                }
            }
        }
    }
}
