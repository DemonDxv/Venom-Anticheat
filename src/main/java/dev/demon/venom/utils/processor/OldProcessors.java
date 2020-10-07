package dev.demon.venom.utils.processor;

import dev.demon.venom.api.user.User;
import dev.demon.venom.utils.math.MathUtil;
import dev.demon.venom.utils.math.evicting.EvictingList;
import dev.demon.venom.utils.time.TickTimer;
import lombok.Getter;

import java.util.List;

@Getter
public class OldProcessors {

    private User user;

    private double offset = Math.pow(2.0, 24.0);

    private double pitchDelta, yawDelta, lastDeltaYaw, lastDeltaPitch, pitchMode, yawMode, sensXPercent, deltaX, deltaY, sensYPercent, lastSensX, lastSensY, sensitivityX, sensitivityY, lastDeltaX, lastDeltaY;

    public long pitchGCD, yawGCD;

    public List<Double> pitchGcdList = new EvictingList(40), yawGcdList = new EvictingList(40);

    private TickTimer timer = new TickTimer(5);

    private double mouseX, mouseY;

    public OldProcessors(User user) {
        this.user = user;
    }

    public void updateOldPrediction() {

        if (user.getMovementData().getTo() != null && user.getMovementData().getFrom() != null) {


            //Credit: Dawson

            lastDeltaPitch = pitchDelta;
            lastDeltaYaw = yawDelta;
            yawDelta = Math.abs(user.getMovementData().getTo().getYaw() - user.getMovementData().getFrom().getYaw());
            pitchDelta = user.getMovementData().getTo().getPitch() - user.getMovementData().getFrom().getPitch();


            yawGCD = MathUtil.gcd((long) (yawDelta * offset), (long) (lastDeltaYaw * offset));
            pitchGCD = MathUtil.gcd((long) (Math.abs(pitchDelta) * offset), (long) (Math.abs(lastDeltaPitch) * offset));

            double yawGcd = yawGCD / offset;
            double pitchGcd = pitchGCD / offset;

            mouseX = (int) (Math.abs((user.getMovementData().getTo().getYaw()
                    - user.getMovementData().getFrom().getYaw())) / yawGcd);

            mouseY = (int) (Math.abs((user.getMovementData().getTo().getPitch()
                    - user.getMovementData().getFrom().getPitch())) / pitchGCD);

            if (yawGCD > 90000 && yawGCD < 2E7 && yawGcd > 0.01f && yawDelta < 8) yawGcdList.add(yawGcd);

            if (pitchGCD > 90000 && pitchGCD < 2E7 && Math.abs(pitchDelta) < 8) pitchGcdList.add(pitchGcd);

            if (yawGcdList.size() > 3 && pitchGcdList.size() > 3) {

                if (timer.hasPassed()) {
                    timer.reset();
                    yawMode = MathUtil.getMode(yawGcdList);
                    pitchMode = MathUtil.getMode(pitchGcdList);
                    sensXPercent = MathUtil.sensToPercent(sensitivityX = MathUtil.getSensitivityFromYawGCD(yawMode));
                    sensYPercent = MathUtil.sensToPercent(sensitivityY = MathUtil.getSensitivityFromPitchGCD(pitchMode));
                }

                lastDeltaX = deltaX;
                lastDeltaY = deltaY;
                deltaX = MathUtil.getDeltaX(yawDelta, (float) yawMode);
                deltaY = MathUtil.getDeltaY(pitchDelta, (float) pitchMode);
            }
        }
    }
}