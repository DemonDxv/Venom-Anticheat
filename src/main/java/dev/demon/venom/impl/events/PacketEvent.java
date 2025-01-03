package dev.demon.venom.impl.events;

import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.event.Cancellable;
import dev.demon.venom.api.tinyprotocol.api.Packet;
import dev.demon.venom.api.user.User;
import dev.demon.venom.utils.location.CustomLocation;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

@Getter
public class PacketEvent extends AnticheatEvent implements Cancellable {

    private Player player;

    @Setter
    private Object packet;

    @Setter
    private boolean cancelled;

    private String type;

    private Direction direction;

    private User user;

    private CustomLocation to, from;

    private long timeStamp;

    public PacketEvent(Player player, Object packet, String type, Direction direction, User user) {
        this.player = player;
        this.packet = packet;
        this.type = type;
        this.direction = direction;
        this.user = user;
        this.to = user.getMovementData().getTo();
        this.from = user.getMovementData().getFrom();

        timeStamp = System.currentTimeMillis();
    }

    public PacketEvent(Player player, Object packet, String type, Direction direction) {
        this.player = player;
        this.packet = packet;
        this.type = type;
        this.direction = direction;

        timeStamp = System.currentTimeMillis();
    }

    public boolean isPacketMovement() {
        return (type.equalsIgnoreCase(Packet.Client.POSITION) || type.equalsIgnoreCase(Packet.Client.FLYING) || type.equalsIgnoreCase(Packet.Client.POSITION_LOOK) || type.equalsIgnoreCase(Packet.Client.LOOK));
    }

    public enum Direction {
        CLIENT,
        SERVER
    }
}
