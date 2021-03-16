package dev.demon.venom.utils.location;

import dev.demon.venom.utils.math.MathUtil;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

@Getter @Setter
public class CustomLocation {
    public double x;
    public double y;
    public double z;
    public double maxX;
    public double maxZ;
    public double minX;
    public double minZ;

    public float yaw, pitch;
    private long timeStamp;

    @Setter(AccessLevel.NONE)
    private World world;
    private boolean clientGround;

    public CustomLocation(World world, double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;

        timeStamp = System.currentTimeMillis();
    }

    public CustomLocation(double x, double y, double z) {
        this.minX = x - 0.3;
        this.maxX = x + 0.3;
        this.x = x;
        this.y = y;
        this.minZ = z - 0.3;
        this.maxZ = z + 0.3;
        this.z = z;

        timeStamp = System.currentTimeMillis();
    }

    public CustomLocation(double x, double y, double z, float yaw, float pitch) {
        this.minX = x - 0.3;
        this.maxX = x + 0.3;
        this.x = x;
        this.y = y;
        this.minZ = z - 0.3;
        this.maxZ = z + 0.3;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;

        timeStamp = System.currentTimeMillis();
    }

    public CustomLocation(double x, double y, double z, float yaw, float pitch, World world) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.world = world;

        timeStamp = System.currentTimeMillis();
    }

    public CustomLocation(double x, double y, double z, float yaw, float pitch, World world, boolean clientGround) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.world = world;
        this.clientGround = clientGround;

        timeStamp = System.currentTimeMillis();
    }

    public CustomLocation(double x, double y, double z, float yaw, float pitch, long timeStamp) {
        this.minX = x - 0.3;
        this.maxX = x + 0.3;
        this.x = x;
        this.y = y;
        this.minZ = z - 0.3;
        this.maxZ = z + 0.3;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.timeStamp = timeStamp;
    }

    public CustomLocation(Location loc) {
        this.x = loc.getX();
        this.y = loc.getY();
        this.z = loc.getZ();
        this.yaw = loc.getYaw();
        this.pitch = loc.getPitch();

        this.timeStamp = System.currentTimeMillis();
    }

    public double getDistanceSquared(CustomLocation location, CustomLocation lastLocation) {
        double dx = Math.min(Math.abs(location.x - minX), Math.abs(lastLocation.x - maxX));
        double dz = Math.min(Math.abs(location.z - minZ), Math.abs(lastLocation.z - maxZ));

        return Math.sqrt(Math.pow(dx, 2.0) + Math.pow(dz, 2.0));
    }

    public CustomLocation clone() {
        return new CustomLocation(x, y, z, yaw, pitch, timeStamp);
    }

    public CustomLocation add(double x, double y, double z) {
        return new CustomLocation(this.x + x, this.y + y, this.z + z, this.yaw, this.pitch);
    }

    public Location toLocation(World world) {
        return new Location(world, x, y, z, yaw, pitch);
    }

    public float getYawClamped() {
        return MathUtil.yawTo180F(yaw);
    }

    public Vector toVector() {
        return new Vector(x, y, z);
    }

    public double distance(@NonNull CustomLocation o) {
        return Math.sqrt(this.distanceSquared(o));
    }

    public double distanceSquared(@NonNull CustomLocation o) {
        if (o.world != null && world != null && o.world== world) {
            return NumberConversions.square(this.x - o.getX()) + NumberConversions.square(this.y - o.getY()) + NumberConversions.square(this.z - o.getZ());
        }
        return 0.0;
    }
}