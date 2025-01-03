package dev.demon.venom.utils.math;

import lombok.Getter;

import java.util.Arrays;

public class RollingAverageDouble {

    private final int size;
    private final double[] array;

    private int index;

    @Getter
    private double average;

    public RollingAverageDouble(int size, double initial) {
        this.size = size;
        array = new double[size];
        average = initial;
        initial /= size;
        Arrays.fill(array, initial);
    }

    public void add(double value) {
        value /= size;
        average -= array[index];
        average += value;
        array[index] = value;
        index = (index + 1) % size;
    }

    public void clearValues() {
        average = 0;
        Arrays.fill(array, 0);
    }
}