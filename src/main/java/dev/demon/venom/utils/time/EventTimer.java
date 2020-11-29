package dev.demon.venom.utils.time;

import dev.demon.venom.api.user.User;
import lombok.Getter;
import lombok.Setter;

/**
 * Created on 22/07/2020 Package me.jumba.sparky.util.time
 */
@Getter
public class EventTimer {

    private int tick;
    private int max;

    @Setter
    private boolean extendWithLag;

    private User user;

    public EventTimer(int max, User user) {
        this.tick = 0;
        this.max = max;
        this.user = user;
    }

    public int getDelta() {
        return (this.user.getTick() - this.tick);
    }

    public boolean hasNotPassed(int ctick) {
        return (this.user.getTick() > ctick && (this.user.getTick() - tick) < ctick);
    }

    public boolean passed(int ctick) {
        return (this.user.getTick() > ctick && (this.user.getTick() - tick) > ctick);
    }

    public boolean hasNotPassed() {
        return (this.user.getTick() > this.max && (this.user.getTick() - tick) < this.max);
    }

    public boolean hasNotPassed(boolean noLimit) {
        return (noLimit ? ((this.user.getTick() - tick) < this.max) : hasNotPassed());
    }

    public boolean passed(int ctick, boolean useSkippedTicks) {
        return (this.user.getTick() > ctick
                + (useSkippedTicks
                ? (user.getLagProcessor().getSkippedPackets() / 2) : 0)
                && (this.user.getTick() - tick) > ctick + (useSkippedTicks
                ? (user.getLagProcessor().getSkippedPackets() / 2) : 0));
    }

    public boolean hasNotPassed(int ctick, boolean useSkippedTicks) {
        return (this.user.getTick() > ctick +
                (useSkippedTicks ? (user.getLagProcessor().getSkippedPackets() / 2) : 0)
                && (this.user.getTick() - tick) < ctick
                + (useSkippedTicks ? (user.getLagProcessor().getSkippedPackets() / 2) : 0));
    }

    //(user.getLagProcessor().getSkippedPackets() / 2)

    public boolean hasNotPassedWithLag() {
      //  return (this.user.getRealClientTick() > this.max && (this.user.getRealClientTick() - tick) < this.max);
        return hasNotPassed();
    }

    public boolean passed() {
        return (this.user.getTick() > this.max && (this.user.getTick() - tick) > this.max);
    }

    public void reset() {
        this.tick = this.user.getTick();
    }

    public boolean hasNotPassedSkipped(int tick) {

        if (user.getLagProcessor().isLagging()) {
            tick += (user.getLagProcessor().longTermLag()
                    ? (user.getLagProcessor().getSkippedPackets() / 2) : 0);
        }

        return (this.user.getTick() > tick && (this.user.getTick() - tick) < tick);
    }

    public boolean hasNotPassedSkipped() {

        int mt = max;

        if (user.getLagProcessor().isLagging() || user.getLagProcessor().longTermLag()) {
            mt += (user.getLagProcessor().longTermLag()
                    ? (user.getLagProcessor().getSkippedPackets() / 2) : 0);
        }

        return (this.user.getTick() > mt && (this.user.getTick() - tick) < mt);
    }
}
