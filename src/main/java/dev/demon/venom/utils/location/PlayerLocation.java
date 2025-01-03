package dev.demon.venom.utils.location;

import dev.demon.venom.api.user.User;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;


import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
public class PlayerLocation {

    private double x, y, z;
    private float yaw, pitch;

    private double minX, maxX;
    private double minZ, maxZ;

    private long timeStamp;



    public PlayerLocation(double x, double y, double z, float yaw, float pitch) {
        this.x = x;
        minX = x - 0.3;
        maxX = x + 0.3;

        this.y = y;

        this.z = z;
        minZ = z - 0.3;
        maxZ = z + 0.3;

        this.yaw = yaw;
        this.pitch = pitch;

    }

    public PlayerLocation(double x, double y, double z, long timeStamp) {
        this.x = x;
        minX = x - 0.3;
        maxX = x + 0.3;

        this.y = y;

        this.z = z;
        minZ = z - 0.3;
        maxZ = z + 0.3;

        this.timeStamp = timeStamp;
    }

    public PlayerLocation add(double x, double y, double z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }



    public Queue<PlayerLocation> getEstimatedLocation(User user, long time, long delta) {
        Queue<PlayerLocation> locs = new LinkedList<>();

        user.getPreviousLocations().stream()
                .sorted(Comparator.comparingLong(loc -> Math.abs(loc.getTimeStamp() - (System.currentTimeMillis() - time))))
                .filter(loc -> Math.abs(loc.getTimeStamp() - (System.currentTimeMillis() - time)) < delta)
                .forEach(locs::add);
        return locs;
    }

    public Queue<PlayerLocation> getEstimatedLocation2(User user, long time, long ping, long delta) {
        Queue<PlayerLocation> locs = new LinkedList<>();

        user.getPreviousLocations()
                .stream()
                .filter(loc -> time - loc.getTimeStamp() > 0 && time - loc.getTimeStamp() < ping + delta)
                .forEach(locs::add);
        return locs;
    }

    public List<PlayerLocation> getEstimatedLocation(User user, long time, long ping, long delta) {
        return user.getPreviousLocations()
                .stream()
                .filter(loc -> time - loc.getTimeStamp() > 0 && time - loc.getTimeStamp() < ping + delta)
                .collect(Collectors.toList());
    }


    public double getDistanceSquared(PlayerLocation location, PlayerLocation lastLocation) {
        double dx = Math.min(Math.abs(location.x - minX), Math.abs(lastLocation.x - maxX));
        double dz = Math.min(Math.abs(location.z - minZ), Math.abs(lastLocation.z - maxZ));

        return Math.sqrt(Math.pow(dx, 2.0) + Math.pow(dz, 2.0));
    }


    public double getDistanceSquaredCentered(PlayerLocation hitbox, PlayerLocation lastLocation) {
        final double dx = Math.pow(Math.min(Math.abs(hitbox.x - this.minX), Math.abs(lastLocation.x - this.maxX)), 2.0);
        final double dz = Math.pow(Math.min(Math.abs(hitbox.z - this.minZ), Math.abs(lastLocation.z - this.maxZ)), 2.0);
        return dx + dz;
    }


    public double getDistanceSquared(PlayerLocation hitbox) {
        double dx = Math.min(Math.abs(hitbox.x - minX), Math.abs(hitbox.x - maxX));
        double dz = Math.min(Math.abs(hitbox.z - minZ), Math.abs(hitbox.z - maxZ));

        return dx * dx + dz * dz;
    }

    public Vector toVector() {
        return new Vector(x, y, z);
    }
}