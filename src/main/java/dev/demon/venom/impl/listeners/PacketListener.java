package dev.demon.venom.impl.listeners;


import com.sun.xml.internal.bind.v2.runtime.reflect.Lister;
import dev.demon.venom.Venom;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.event.AnticheatListener;
import dev.demon.venom.api.event.Listen;
import dev.demon.venom.api.tinyprotocol.api.NMSObject;
import dev.demon.venom.api.tinyprotocol.api.Packet;
import dev.demon.venom.api.tinyprotocol.packet.in.*;
import dev.demon.venom.api.tinyprotocol.packet.outgoing.*;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.*;
import dev.demon.venom.impl.events.inevents.*;
import dev.demon.venom.impl.events.outevents.*;
import org.bukkit.Bukkit;

public class PacketListener implements AnticheatListener {

    @Listen
    public void onPacket(PacketEvent e) {
        User user = Venom.getInstance().getUserManager().getUser(e.getPlayer().getUniqueId());
        if (user != null) {

            user.getMovementProcessor().update(e.getPacket(), e.getType());

            user.getCombatProcessor().update(e.getPacket(), e.getType());

            user.getLagProcessor().update(e.getPacket(), e.getType());

            user.getVelocityProcessor().update(e.getPacket(), e.getType());

            user.getOptifineProcessor().onPacket(e.getPacket(), e.getType(), user);

            //user.getOtherProcessor().update(e.getPacket(), e.getType());
            //user.getPredictionProcessor().update(e);

            AnticheatEvent event = e;

            //Client
            if (e.isPacketMovement()) {
                WrappedInFlyingPacket packet = new WrappedInFlyingPacket(e.getPacket(), e.getPlayer());
                event = new FlyingInEvent(packet.getX(), packet.getY(), packet.getZ(), packet.getPitch(), packet.getYaw(), packet.isGround(), packet.isPos(), packet.isLook());
            } else if (e.getType().equalsIgnoreCase(Packet.Client.KEEP_ALIVE)) {
                event = new KeepAliveInEvent();
            } else if (e.getType().equalsIgnoreCase(Packet.Client.USE_ENTITY)) {
                WrappedInUseEntityPacket packet = new WrappedInUseEntityPacket(e.getPacket(), e.getPlayer());
                event = new UseEntityEvent(packet.getEntity(), packet.getAction());
            } else if (e.getType().equalsIgnoreCase(Packet.Client.TRANSACTION)) {
                WrappedInTransactionPacket packet = new WrappedInTransactionPacket(e.getPacket(), e.getPlayer());
                event = new TransactionEvent(packet.getId(), packet.getAction(), packet.isAccept());
            } else if (e.getType().equalsIgnoreCase(Packet.Client.ARM_ANIMATION)) {
                event = new ArmAnimationEvent();
            } else if (e.getType().equalsIgnoreCase(Packet.Client.ENTITY_ACTION)) {
                WrappedInEntityActionPacket packet = new WrappedInEntityActionPacket(e.getPacket(), e.getPlayer());
                event = new PlayerActionEvent(packet.getAction());
            } else if (e.getType().equalsIgnoreCase(Packet.Client.BLOCK_PLACE)) {
                WrappedInBlockPlacePacket packet = new WrappedInBlockPlacePacket(e.getPacket(), e.getPlayer());
                event = new BlockPlaceEvent(packet.getVecX(), packet.getVecY(), packet.getVecZ(), packet.getFace(), packet.getPosition(), packet.getItemStack());
            } else if (e.getType().equalsIgnoreCase(Packet.Client.BLOCK_DIG)) {
                WrappedInBlockDigPacket packet = new WrappedInBlockDigPacket(e.getPacket(), e.getPlayer());
                event = new BlockDigEvent(packet.getAction(), packet.getDirection(), packet.getPosition());
            } else if (e.getType().equalsIgnoreCase(Packet.Client.ABILITIES)) {
                WrappedInAbilitiesPacket packet = new WrappedInAbilitiesPacket(e.getPacket(), e.getPlayer());
                event = new AbilityInEvent(packet.isAllowedFlight(), packet.isFlying(), packet.isCreativeMode(), packet.isInvulnerable(), packet.getFlySpeed(), packet.getWalkSpeed());
            } else if (e.getType().equalsIgnoreCase(Packet.Client.HELD_ITEM_SLOT)) {
                WrappedInHeldItemSlotPacket packet = new WrappedInHeldItemSlotPacket(e.getPacket(), e.getPlayer());
                event = new HeldItemSlotInEvent(packet.getSlot());
            } else if (e.getType().equalsIgnoreCase(Packet.Client.CLOSE_WINDOW)) {
                WrappedInCloseWindowPacket packet = new WrappedInCloseWindowPacket(e.getPacket(), e.getPlayer());
                event = new CloseWindowInEvent(packet.getId());
            } else if (e.getType().equalsIgnoreCase(Packet.Client.WINDOW_CLICK)) {
                WrappedInWindowClickPacket packet = new WrappedInWindowClickPacket(e.getPacket(), e.getPlayer());
                event = new ClickWindowInEvent(packet.getId(), packet.getAction(), packet.getButton(), packet.getCounter(), packet.getItem(), packet.getMode(), packet.getSlot());
            } else if (e.getType().equalsIgnoreCase(Packet.Client.STEER_VEHICLE)) {
                event = new SteerVehicleInEvent();
            } else if (e.getType().equalsIgnoreCase(Packet.Client.CUSTOM_PAYLOAD)) {
                WrappedInCustomPayloadPacket packet = new WrappedInCustomPayloadPacket(e.getPacket(), user.getPlayer());
                event = new CustomPayLoadInEvent(packet.getChannel(), packet.getData());

                //Server

            } else if (e.getType().equalsIgnoreCase(Packet.Server.ENTITY_VELOCITY)) {
                WrappedOutVelocityPacket packet = new WrappedOutVelocityPacket(e.getPacket(), e.getPlayer());
                event = new VelocityOutEvent(packet.getId(), packet.getX(), packet.getY(), packet.getZ());
            } else if (e.getType().contains(NMSObject.Server.ENTITY) || e.getType().contains(NMSObject.Server.REL_LOOK) || e.getType().contains(NMSObject.Server.REL_POSITION_LOOK) || e.getType().contains(NMSObject.Server.REL_POSITION)) {
                WrappedOutRelativePosition packet = new WrappedOutRelativePosition(e.getPacket(), e.getPlayer());
                event = new RelMoveOutEvent(packet.getX(), packet.getY(), packet.getZ(), packet.getPitch(), packet.getYaw(), packet.isGround(), packet.isPos(), packet.isLook());
            } else if (e.getType().equalsIgnoreCase(Packet.Server.ABILITIES)) {
                WrappedOutAbilitiesPacket packet = new WrappedOutAbilitiesPacket(e.getPacket(), e.getPlayer());
                event = new AbilityOutEvent(packet.isAllowedFlight(), packet.isFlying(), packet.isCreativeMode(), packet.isInvulnerable(), packet.getFlySpeed(), packet.getWalkSpeed());
            } else if (e.getType().contains(NMSObject.Server.ENTITY_EFFECT)) {
                WrappedOutEntityEffectPacket packet = new WrappedOutEntityEffectPacket(e.getPacket(), e.getPlayer());
                event = new EntityEffectOutEvent(packet.effectId);
            } else if (e.getType().equalsIgnoreCase(Packet.Server.HELD_ITEM)) {
                WrappedOutHeldItemSlot packet = new WrappedOutHeldItemSlot(e.getPacket(), e.getPlayer());
                event = new HeldItemSlotOutEvent(packet.getSlot());
            } else if (e.getType().equalsIgnoreCase(Packet.Server.CLOSE_WINDOW)) {
                WrappedOutCloseWindowPacket packet = new WrappedOutCloseWindowPacket(e.getPacket(), e.getPlayer());
                event = new CloseWindowOutEvent(packet.id);
            } else if (e.getType().equalsIgnoreCase(Packet.Server.RESPAWN)) {
                event = new RespawnOutEvent();
            } else if (e.getType().equalsIgnoreCase(Packet.Server.CUSTOM_PAYLOAD)) {
                WrappedOutCustomPayloadPacket packet = new WrappedOutCustomPayloadPacket(e.getPacket(), user.getPlayer());
                event = new CustomPayLoadOutEvent(packet.getChannel(), packet.getData());
            }


            AnticheatEvent finalEvent = event;
            user.checks.stream().filter(check -> check.enabled
                    && !check.freeze).forEach(check -> check.onHandle(user, finalEvent));
        }
    }
}
