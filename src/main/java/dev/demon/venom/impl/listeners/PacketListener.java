package dev.demon.venom.impl.listeners;


import dev.demon.venom.Venom;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.event.Listen;
import dev.demon.venom.api.event.Listener;
import dev.demon.venom.api.tinyprotocol.api.NMSObject;
import dev.demon.venom.api.tinyprotocol.api.Packet;
import dev.demon.venom.api.tinyprotocol.packet.in.*;
import dev.demon.venom.api.tinyprotocol.packet.outgoing.*;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.PacketEvent;
import dev.demon.venom.impl.events.inevents.*;
import dev.demon.venom.impl.events.outevents.*;
import org.bukkit.Bukkit;

public class PacketListener extends Listener {

    @Listen
    public void onPacket(PacketEvent e) {
        User user = Venom.getInstance().getUserManager().getUser(e.getPlayer().getUniqueId());

        user.getMovementProcessor().update(e.getPacket(), e.getType());

        user.getCombatProcessor().update(e.getPacket(), e.getType());

        user.getLagProcessor().update(e.getPacket(), e.getType());

        user.getVelocityProcessor().update(e.getPacket(), e.getType());

        user.getEntityActionProcessor().update(e.getPacket(), e.getType());


        user.getOptifineProcessor().onPacket(e.getPacket(), e.getType(), user);

        //user.getOtherProcessor().update(e.getPacket(), e.getType());
        user.getPredictionProcessor().update(e);

        AnticheatEvent event = e;

      //  Bukkit.broadcastMessage(""+e.getType());

        switch (e.getType()) {

            /** Client **/
            case Packet.Client.POSITION:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.LOOK:
            case Packet.Client.FLYING: {
                WrappedInFlyingPacket packet = new WrappedInFlyingPacket(e.getPacket(), e.getPlayer());
                event = new FlyingInEvent(packet.getX(), packet.getY(), packet.getZ(), packet.getPitch(), packet.getYaw(), packet.isGround(), packet.isPos(), packet.isLook());
                break;
            }

            case Packet.Client.KEEP_ALIVE: {
                event = new KeepAliveInEvent();
                break;
            }

            case Packet.Client.USE_ENTITY: {
                WrappedInUseEntityPacket packet = new WrappedInUseEntityPacket(e.getPacket(), e.getPlayer());
                event = new UseEntityEvent(packet.getEntity(), packet.getAction());
                break;
            }

            case Packet.Client.TRANSACTION: {
                WrappedInTransactionPacket packet = new WrappedInTransactionPacket(e.getPacket(), e.getPlayer());
                event = new TransactionEvent(packet.getId(), packet.getAction(), packet.isAccept());
                break;
            }

            case Packet.Client.ARM_ANIMATION: {
                event = new ArmAnimationEvent();
                break;
            }

            case Packet.Client.ENTITY_ACTION: {
                WrappedInEntityActionPacket packet = new WrappedInEntityActionPacket(e.getPacket(), e.getPlayer());
                event = new PlayerActionEvent(packet.getAction());
                break;
            }

            case Packet.Client.BLOCK_PLACE: {
                WrappedInBlockPlacePacket packet = new WrappedInBlockPlacePacket(e.getPacket(), e.getPlayer());
                event = new BlockPlaceEvent(packet.getVecX(), packet.getVecY(), packet.getVecZ(), packet.getFace(), packet.getPosition(), packet.getItemStack());
                break;
            }

            case Packet.Client.BLOCK_DIG: {
                WrappedInBlockDigPacket packet = new WrappedInBlockDigPacket(e.getPacket(), e.getPlayer());
                event = new BlockDigEvent(packet.getAction(), packet.getDirection(), packet.getPosition());
                break;
            }

            case Packet.Client.ABILITIES: {
                WrappedInAbilitiesPacket packet = new WrappedInAbilitiesPacket(e.getPacket(), e.getPlayer());
                event = new AbilityInEvent(packet.isAllowedFlight(), packet.isFlying(), packet.isCreativeMode(), packet.isInvulnerable(), packet.getFlySpeed(), packet.getWalkSpeed());
                break;
            }

            case Packet.Client.HELD_ITEM_SLOT: {
                WrappedInHeldItemSlotPacket packet = new WrappedInHeldItemSlotPacket(e.getPacket(), e.getPlayer());
                event = new HeldItemSlotInEvent(packet.getSlot());
                break;
            }

            case Packet.Client.CLOSE_WINDOW: {
                WrappedInCloseWindowPacket packet = new WrappedInCloseWindowPacket(e.getPacket(), e.getPlayer());
                event = new CloseWindowInEvent(packet.getId());
                break;
            }

            case Packet.Client.WINDOW_CLICK: {
                WrappedInWindowClickPacket packet = new WrappedInWindowClickPacket(e.getPacket(), e.getPlayer());
                event = new ClickWindowInEvent(packet.getId(), packet.getAction(), packet.getButton(), packet.getCounter(), packet.getItem(), packet.getMode(), packet.getSlot());
                break;
            }

            case Packet.Client.STEER_VEHICLE: {
                event = new SteerVehicleInEvent();
                break;
            }

            case Packet.Client.CUSTOM_PAYLOAD: {
                WrappedInCustomPayloadPacket packet = new WrappedInCustomPayloadPacket(e.getPacket(), user.getPlayer());
                event = new CustomPayLoadInEvent(packet.getChannel(), packet.getData());
                break;
            }


            /** Server **/
            case Packet.Server.ENTITY_VELOCITY: {
                WrappedOutVelocityPacket packet = new WrappedOutVelocityPacket(e.getPacket(), e.getPlayer());
                event = new VelocityOutEvent(packet.getId(), packet.getX(), packet.getY(), packet.getZ());
                break;
            }


            case Packet.Server.ABILITIES: {
                WrappedOutAbilitiesPacket packet = new WrappedOutAbilitiesPacket(e.getPacket(), e.getPlayer());
                event = new AbilityOutEvent(packet.isAllowedFlight(), packet.isFlying(), packet.isCreativeMode(), packet.isInvulnerable(), packet.getFlySpeed(), packet.getWalkSpeed());
                break;
            }

            case NMSObject.Server.ENTITY_EFFECT: {
                WrappedOutEntityEffectPacket packet = new WrappedOutEntityEffectPacket(e.getPacket(), e.getPlayer());
                event = new EntityEffectOutEvent(packet.effectId);
                break;
            }

            case Packet.Server.HELD_ITEM: {
                WrappedOutHeldItemSlot packet = new WrappedOutHeldItemSlot(e.getPacket(), e.getPlayer());
                event = new HeldItemSlotOutEvent(packet.getSlot());
                break;
            }

            case Packet.Server.CLOSE_WINDOW: {
                WrappedOutCloseWindowPacket packet = new WrappedOutCloseWindowPacket(e.getPacket(), e.getPlayer());
                event = new CloseWindowOutEvent(packet.id);
                break;
            }

            case Packet.Server.RESPAWN: {
                event = new RespawnOutEvent();
                break;
            }

            case Packet.Server.CUSTOM_PAYLOAD: {
                WrappedOutCustomPayloadPacket packet = new WrappedOutCustomPayloadPacket(e.getPacket(), user.getPlayer());
                event = new CustomPayLoadOutEvent(packet.getChannel(), packet.getData());
            }

        }


        AnticheatEvent finalEvent = event;
        user.checks.stream().filter(check -> check.enabled).forEach(check -> check.onHandle(user, finalEvent));
    }
}
