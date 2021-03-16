package dev.demon.venom.utils.processor;

import dev.demon.venom.Venom;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.tinyprotocol.api.NMSObject;
import dev.demon.venom.api.tinyprotocol.api.Packet;
import dev.demon.venom.api.tinyprotocol.api.TinyProtocolHandler;
import dev.demon.venom.api.tinyprotocol.packet.in.WrappedInTransactionPacket;
import dev.demon.venom.api.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import dev.demon.venom.api.tinyprotocol.packet.outgoing.WrappedOutRelativePosition;
import dev.demon.venom.api.tinyprotocol.packet.outgoing.WrappedOutTransaction;
import dev.demon.venom.api.user.User;

import dev.demon.venom.utils.math.MathUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;

import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
public class CombatProcessor {
    private User user;
    public HashMap<Double, Short> relMoveLocation = new HashMap();
    private double relMoveX, relMoveZ, relTransZ, relTransX;
    public HashMap<Double, Short> confirmedX = new HashMap(), confirmedZ = new HashMap();

    public void update(Object packet, String type) {
        if (user != null) {


            switch (type) {

                case Packet.Client.USE_ENTITY: {

                    WrappedInUseEntityPacket wrappedInUseEntityPacket = new WrappedInUseEntityPacket(packet, user.getPlayer());

                    if (wrappedInUseEntityPacket.getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK && wrappedInUseEntityPacket.getEntity() != null) {

                        if (wrappedInUseEntityPacket.getEntity() instanceof Player) {
                            User attackedUser = Venom.getInstance().getUserManager().getUser(wrappedInUseEntityPacket.getEntity().getUniqueId());
                            if (attackedUser != null) {
                                user.getCombatData().setTargetUser(attackedUser);
                                attackedUser.getEntityActionProcessor().addAction(EntityActionProcessor.ActionType.ATTACKED);
                            }

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
                    break;
                }

                case Packet.Server.RESPAWN: {
                    user.getEntityActionProcessor().addAction(EntityActionProcessor.ActionType.RESPAWN);
                    break;
                }
            }
        }
    }
}