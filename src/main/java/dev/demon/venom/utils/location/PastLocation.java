package dev.demon.venom.utils.location;


import dev.demon.venom.utils.math.MathUtil;
import lombok.Getter;

import org.bukkit.Location;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class PastLocation {
    @Getter
    public List<CustomLocation> previousLocations = new CopyOnWriteArrayList<>();

    public CustomLocation getPreviousLocation(long time) {
        long timeStamp = System.currentTimeMillis() - time;
        return (this.previousLocations.stream()
                .min(Comparator.comparing((loc) -> MathUtil.getDelta(timeStamp, loc.getTimeStamp())))
                .orElse(this.previousLocations.get(0)));
    }

    public List<CustomLocation> getEstimatedLocation(long time, long ping, long delta) {
        return this.previousLocations
                .stream()
                .filter(loc -> time - loc.getTimeStamp() > 0 && time - loc.getTimeStamp() < ping + delta)
                .collect(Collectors.toList());
    }

    public List<CustomLocation> getEstimatedLocation(long time, long delta) {
        long prevTimeStamp = System.currentTimeMillis() - time;
        return this.previousLocations
                .stream()
                .filter(loc -> MathUtil.getDelta(prevTimeStamp, loc.getTimeStamp()) < delta)
                .collect(Collectors.toList());
    }

    public List<CustomLocation> getPreviousRange(long delta) {
        long stamp = System.currentTimeMillis();

        return this.previousLocations.stream()
                .filter(loc -> stamp - loc.getTimeStamp() < delta)
                .collect(Collectors.toList());
    }

    public void addLocation(Location location) {
        if (previousLocations.size() >= 20) {
            previousLocations.remove(0);
        }

        previousLocations.add(new CustomLocation(location));
    }

    public void addLocation(CustomLocation location) {
        if (previousLocations.size() >= 20) {
            previousLocations.remove(0);
        }

        previousLocations.add(location.clone());
    }
}