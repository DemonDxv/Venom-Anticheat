package dev.demon.venom.utils.processor;

import dev.demon.venom.api.tinyprotocol.api.Packet;
import dev.demon.venom.api.tinyprotocol.api.TinyProtocolHandler;
import dev.demon.venom.api.tinyprotocol.packet.in.WrappedInTransactionPacket;
import dev.demon.venom.api.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import dev.demon.venom.api.tinyprotocol.packet.outgoing.WrappedOutTransaction;
import dev.demon.venom.api.tinyprotocol.packet.outgoing.WrappedOutVelocityPacket;
import dev.demon.venom.api.user.User;
import dev.demon.venom.utils.math.MathUtil;
import dev.demon.venom.utils.time.TimeUtils;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class VelocityProcessor {
    private User user;
    public double velocityX, velocityY, velocityZ, horizontal, vertical;

    public void update(Object packet, String type) {
        if (user != null) {
            if (type.equalsIgnoreCase(Packet.Server.ENTITY_VELOCITY)) {
                WrappedOutVelocityPacket wrappedOutVelocityPacket = new WrappedOutVelocityPacket(packet, user.getPlayer());

                if (wrappedOutVelocityPacket.getId() == user.getPlayer().getEntityId()) {

                    user.getLagProcessor().setHitTime(System.currentTimeMillis());


                    user.getVelocityData().setLastVelocity(System.currentTimeMillis());

                    velocityX = wrappedOutVelocityPacket.getX();
                    velocityY = wrappedOutVelocityPacket.getY();
                    velocityZ = wrappedOutVelocityPacket.getZ();

                    //user.getVelocityData().setVelocityX(user.getVelocityData().getVelocityX() * 0.6D);
                    //user.getVelocityData().setVelocityZ(user.getVelocityData().getVelocityZ() * 0.6D);

                    horizontal = MathUtil.hypot(velocityX, velocityZ);
                    vertical = Math.pow(velocityY + 2.0, 2.0) * 5.0;


                    if (user.getMovementData().isOnGround() && user.getPlayer().getLocation().getY() % 1.0 == 0.0) {
                        vertical = velocityY;
                    }

                    user.getCombatData().setLastVelocitySqr((Math.sqrt(velocityX * velocityX + velocityZ * velocityZ) * 0.2));

                    WrappedOutTransaction wrappedOutTransaction = new WrappedOutTransaction(0, user.getMiscData().getTransactionIDVelocity(), false);
                    TinyProtocolHandler.getInstance().getChannel().sendPacket(user.getPlayer(), wrappedOutTransaction.getObject());


                }
            }

            if (type.equalsIgnoreCase(Packet.Client.USE_ENTITY)) {
                WrappedInUseEntityPacket use = new WrappedInUseEntityPacket(packet, user.getPlayer());
                if (use.getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK
                        || user.getMovementData().isLastSprint()) {

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
                    user.getVelocityData().getLastVelocityHorizontal().put(horizontal, currentIDVelocity);
                    user.getVelocityData().getLastVelocityVertical().put(vertical, currentIDVelocity);
                    user.getVelocityData().setVelocityTicks(0);
                }
            }
        }
    }
}
