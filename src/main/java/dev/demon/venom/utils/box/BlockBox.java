package dev.demon.venom.utils.box;

import dev.demon.venom.api.user.User;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;

public interface BlockBox {
    List<BoundingBox> getCollidingBoxes(World world, BoundingBox box, User user);

    List<BoundingBox> getSpecificBox(Location location, User user);

    boolean isChunkLoaded(Location loc);

    boolean isUsingItem(Player player);

    boolean isRiptiding(LivingEntity entity);

    float getMovementFactor(Player player);

    @Deprecated
    int getTrackerId(Player player);

    float getAiSpeed(Player player);
}