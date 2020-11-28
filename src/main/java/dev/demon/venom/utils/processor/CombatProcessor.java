package dev.demon.venom.utils.processor;

import dev.demon.venom.Venom;
import dev.demon.venom.api.tinyprotocol.api.Packet;
import dev.demon.venom.api.tinyprotocol.api.TinyProtocolHandler;
import dev.demon.venom.api.tinyprotocol.packet.in.WrappedInTransactionPacket;
import dev.demon.venom.api.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import dev.demon.venom.api.tinyprotocol.packet.outgoing.WrappedOutTransaction;
import dev.demon.venom.api.user.User;

import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;

@Setter
public class CombatProcessor {
    private User user;

    public void update(Object packet, String type) {
        if (user != null) {

            if (type.equalsIgnoreCase(Packet.Client.USE_ENTITY)) {

                WrappedInUseEntityPacket wrappedInUseEntityPacket = new WrappedInUseEntityPacket(packet, user.getPlayer());

                if (wrappedInUseEntityPacket.getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK && wrappedInUseEntityPacket.getEntity() != null) {

                    if (wrappedInUseEntityPacket.getEntity() instanceof Player) {
                        User attackedUser = Venom.getInstance().getUserManager().getUser(wrappedInUseEntityPacket.getEntity().getUniqueId());
                        if (attackedUser != null) user.getCombatData().setTargetUser(attackedUser);

                        WrappedOutTransaction wrappedOutTransaction = new WrappedOutTransaction(0, (short) 69, false);
                        TinyProtocolHandler.getInstance().getChannel().sendPacket(user.getPlayer(), wrappedOutTransaction.getObject());
                    }

                    if (user.getCombatData().getLastEntityAttacked() != null) {
                        if (user.getCombatData().getLastEntityAttacked() != wrappedInUseEntityPacket.getEntity()) {
                            user.getCombatData().constantEntityTicks = 0;
                        } else {
                            user.getCombatData().constantEntityTicks++;
                        }
                        user.getCombatData().setLastUseEntityTick(user.getConnectedTick());
                    }

                    if (wrappedInUseEntityPacket.getEntity() instanceof Player || wrappedInUseEntityPacket.getEntity() instanceof Villager) {
                        user.getCombatData().setLastEntityAttacked(wrappedInUseEntityPacket.getEntity());
                        user.getCombatData().setLastUseEntityPacket(System.currentTimeMillis());
                    }
                }
            }

            if (type.equalsIgnoreCase(Packet.Client.TRANSACTION)) {
                WrappedInTransactionPacket transactionPacket = new WrappedInTransactionPacket(packet, user.getPlayer());

                if (transactionPacket.getAction() == 69) {
                    user.getCombatData().setTransactionHits(0);
                }
            }
        }
    }
}