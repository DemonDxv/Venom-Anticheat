package dev.demon.venom.utils.time;


import dev.demon.venom.Venom;

public class TickTimer {
    private int ticks = Venom.getInstance().getCurrentTicks(), defaultPassed;

    private long lastTime;

    public TickTimer(int defaultPassed) {
        this.defaultPassed = defaultPassed;
        reset();
    }

    public void reset() {
        ticks = Venom.getInstance().getCurrentTicks();
    }

    public boolean hasPassed() {
        return Venom.getInstance().getCurrentTicks() - ticks > defaultPassed;
    }

    public boolean hasPassed(int amount) {
        return Venom.getInstance().getCurrentTicks() - ticks > amount;
    }

    public boolean hasNotPassed() {
        return Venom.getInstance().getCurrentTicks() - ticks <= defaultPassed;
    }

    public boolean hasNotPassed(int amount) {
        return Venom.getInstance().getCurrentTicks() - ticks <= amount;
    }


    public void reset2() {
        this.lastTime = System.currentTimeMillis();
    }
    public boolean hasReached(double miliseconds) {
        return ((System.currentTimeMillis() - lastTime) >= miliseconds);
    }

    public long getLastTime() {
        return lastTime;
    }

    public int getPassed() {
        return Venom.getInstance().getCurrentTicks() - ticks;
    }
}