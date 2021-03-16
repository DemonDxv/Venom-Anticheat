package dev.demon.venom.utils.box.simple;
import dev.demon.venom.api.tinyprotocol.packet.types.WrappedEnumParticle;
import dev.demon.venom.utils.box.simple.types.SimpleCollisionBox;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;

public interface CollisionBox {
    boolean isCollided(CollisionBox other);
    void draw(WrappedEnumParticle particle, Collection<? extends Player> players);
    CollisionBox copy();
    CollisionBox offset(double x, double y, double z);
    void downCast(List<SimpleCollisionBox> list);
    boolean isNull();
}