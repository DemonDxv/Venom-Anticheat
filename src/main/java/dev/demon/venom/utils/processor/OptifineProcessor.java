package dev.demon.venom.utils.processor;

import dev.demon.venom.api.tinyprotocol.api.Packet;
import dev.demon.venom.api.tinyprotocol.packet.in.WrappedInFlyingPacket;
import dev.demon.venom.api.user.User;
import dev.demon.venom.utils.math.MCSmoothing;
import dev.demon.venom.utils.math.MathUtil;
import dev.demon.venom.utils.math.MouseFilter;
import dev.demon.venom.utils.time.TimeUtils;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created on 21/10/2019 Package me.jumba.processor
 */
public class OptifineProcessor {

    //Cancer ik stfu and deal with it

    private long last;

    private float smoothMouseY, smoothMouseX, smoothYaw, smoothPitch;

    private MCSmoothing x = new MCSmoothing(), y = new MCSmoothing();

    private MCSmoothing pitchSmooth = new MCSmoothing(), yawSmooth = new MCSmoothing();

    private float lCinematicYaw;
    public float lCinematicPitch;
    private float cinematicYaw;
    public float cinematicPitch;
    private float cDeltaYaw;
    private float cDeltaPitch;

    public boolean smoothX, smoothY;

    private float smoothCamFilterX, smoothCamFilterY, smoothCamYaw, smoothCamPitch, lastPitch, lastYaw;
    private MouseFilter mouseFilterXAxis = new MouseFilter(), mouseFilterYAxis = new MouseFilter(), mouseFilterY = new MouseFilter(), mouseFilterX = new MouseFilter();

    public int optfineTicks;

    private long lastYawUpdate, lastPitchUpdate;

    public long lastOptifine;

    private double lastShiit;
    private int test;

    public OptifineProcessor(User user) {
    }


    public void onPacket(Object packet, String type, User user) {
        if (type.equalsIgnoreCase(Packet.Client.POSITION_LOOK)
                || type.equalsIgnoreCase(Packet.Client.POSITION)
                || type.equalsIgnoreCase(Packet.Client.LOOK)
                || type.equalsIgnoreCase(Packet.Client.FLYING)) {

            WrappedInFlyingPacket wrappedInFlyingPacket = new WrappedInFlyingPacket(packet, user.getPlayer());


            updateOpti(user);
            processSmoother(user);
            updateOptifine(user);

            if (user.getMiscData().isHasSetClientSensitivity()) {
                if (wrappedInFlyingPacket.isLook()) {
                    float f = (float) (user.getMovementProcessor().getSensitivityX() * 0.6000000238418579 + 0.20000000298023224);
                    float f2 = f * f * f * 8.0f;

                    this.smoothCamFilterX = this.mouseFilterXAxis.smooth(this.smoothCamYaw, 0.05f * f2);
                    this.smoothCamFilterY = this.mouseFilterYAxis.smooth(this.smoothCamPitch, 0.05f * f2);

                    float f3 = (float) user.getMovementProcessor().getDeltaX() * f2;
                    float f4 = (float) user.getMovementProcessor().getDeltaY() * f2;

                    this.smoothCamYaw += f3;
                    this.smoothCamPitch += f4;

                    f3 = this.smoothCamFilterX * 0.5f;
                    f4 = this.smoothCamFilterY * 0.5f;

                    float yaw = user.getMovementData().getFrom().getYawClamped() + f3 * 0.15f;
                    float ppitch = user.getMovementData().getFrom().getPitch() - f4 * 0.15f;

                    float yawDelta = Math.abs(Math.abs(yaw) - Math.abs(user.getMovementData().getFrom().getYawClamped()));
                    float pitchDelta = Math.abs(Math.abs(ppitch) - Math.abs(user.getMovementData().getFrom().getPitch()));

                    float yAcell = Math.abs(this.lastYaw - user.getMovementData().getYawDelta());
                    float fuck = Math.abs(this.lastPitch - user.getMovementData().getPitchDelta());

                    if (yawDelta > ((yAcell > 0.0f)
                            ? ((yAcell > 10.0f) ? 3.0 : 0.5) : 0.1) || pitchDelta > ((fuck > 0.0f) ? ((fuck > 10.0f) ? 3 : 2) : 0.1)) {
                        resetFilter();
                    } else {

                        boolean optifine = false;

                        if (yAcell < 0.50 && yawDelta > (yAcell > 0.0 ? (yAcell > 0.099 ? 0.1 : 0.077) : 0.005)) {
                            if (yawDelta > 0.2) {
                                long last = (System.currentTimeMillis() - lastYawUpdate);
                                if (last < 150L) {
                                    //TODO: make this work with optifine boolean
                                    //optifine = true;
                                }
                                lastYawUpdate = System.currentTimeMillis();
                            }
                        }

                        if (pitchDelta > (fuck > 0.0 ? (fuck > 0.069 ? 0.087 : 0.095) : 0.049) && Math.abs(pitchDelta - fuck) < 5) {
                            long time = (System.currentTimeMillis() - lastPitchUpdate);


                            if (time > 30L && time < 100L) {

                                if (fuck < 1) {
                                    if (fuck > 0.02 && fuck < 0.3) {
                                        optifine = true;
                                    }
                                }
                            }

                            lastPitchUpdate = System.currentTimeMillis();

                        } else {
                            if (pitchDelta > 0.0 && fuck > 0.0 && pitchDelta < 0.099 && fuck < 0.9) {
                                long time = (System.currentTimeMillis() - lastPitchUpdate);


                                if (time < 100L) {
                                    optifine = true;
                                }

                                lastPitchUpdate = System.currentTimeMillis();
                            }
                        }

                        if (optifine && user.getMovementData().getPitchDelta() < 6) {
                            if (optfineTicks < 55) optfineTicks += 3;
                        } else {
                            optfineTicks -= (optfineTicks > 0 ? (optfineTicks > 20 ? 3 : 1) : 0);
                        }

                        if (optfineTicks > 17) {
                            lastOptifine = System.currentTimeMillis();
                        }

                     //   user.debug("" + optifine + " " + optfineTicks + " " + user.getMovementData().getPitchDelta());
                    }
                }

                this.lastPitch = user.getMovementData().getPitchDelta();
                this.lastYaw = user.getMovementData().getYawDelta();
            }



            lCinematicYaw = cinematicYaw;
            lCinematicPitch = cinematicPitch;
            cinematicYaw = findClosestCinematicYaw(user.getMovementData().getTo().getYaw(), user.getMovementData().getFrom().getYaw());
            cinematicPitch = findClosestCinematicPitch(user.getMovementData().getTo().getPitch(), user.getMovementData().getFrom().getPitch());

            cDeltaYaw = MathUtil.getAngleDelta(cinematicYaw, lCinematicYaw);
            cDeltaPitch = MathUtil.getAngleDelta(cinematicPitch, lCinematicPitch);

            smoothX = MathUtil.getDelta(cDeltaYaw, user.getMovementData().getYawDelta()) < (Math.abs(user.getMovementData().getYawDelta())
                    > 20 ? 2 : 0.51);

            smoothY =
                    MathUtil.getDelta(cDeltaPitch, user.getMovementData().getPitchDelta())
                            < (user.getMovementData().getPitchDelta() > 12 ? 1.1 : (user.getMovementData().getYawDelta() > 15 ? 0.55f : 0.31))
                            && Math.abs(user.getMovementData().getPitchDelta()) > 1E-7;


            if (Float.isNaN(cinematicPitch) || Float.isNaN(cinematicYaw)) {
                yawSmooth.reset();
                pitchSmooth.reset();
            }
        }
    }


    public void resetFilter() {
        this.smoothCamFilterX = 0.0f;
        this.smoothCamFilterY = 0.0f;
        this.mouseFilterXAxis.reset();
        this.mouseFilterYAxis.reset();
        final float n = 0.0f;
        this.smoothCamPitch = n;
        this.smoothCamYaw = n;
    }


    public void updateNew(User user) {
        if (isPreOptifine(user)) {

            float f = (float) user.getMovementProcessor().getSensitivityX() * 0.6f + .2f;
            float f1 = f * f * f * 8;

            float f2 = (float) (user.getMovementProcessor().getDeltaX() * f1);
            float f3 = (float) (user.getMovementProcessor().getDeltaY() * f1);

            this.smoothRotations(f1);

            smoothYaw = f2;
            smoothPitch = f3;

            f2 = smoothMouseX * 0.5f;
            f3 = smoothMouseY * 0.5f;

            user.usingNewOptifine = isSmooth(user, f2, f3);
        } else {
            this.resetOptifine(user);
        }
    }

    private void smoothRotations(float f1) {
        smoothMouseX = x.smooth(smoothYaw, .05f * f1);
        smoothMouseY = y.smooth(smoothPitch, .05f * f1);
    }

    public void updateOpti(User user) {
        if ((System.currentTimeMillis() - user.lastOptifine) > 2000L) {
            if (user.optifineSmoothing2 > 0) user.optifineSmoothing2--;
            user.optifineSmoothing = 0;
        }
        if ((System.currentTimeMillis() - user.getMovementData().getLastFullBlockMoved()) >= 1000L) {
            user.optifineSameCount = 0;
            user.optifineSmoothing2 = 0;
            user.optifineSmoothing = 0;
        }
    }

    private void resetOptifine(User user) {
        x.reset();
        y.reset();
        user.usingNewOptifine = false;
    }


    private boolean isPreOptifine(User user) {
        return ((user.getMovementProcessor().pitchGCD < 1E5 || user.getMovementProcessor().yawGCD < 1E5) && smoothMouseY < 1E6 && smoothMouseX < 1E6 && (Math.abs(user.getMovementData().getTo().getYawClamped() - user.getMovementData().getFrom().getYawClamped()) > 0.0 || Math.abs(user.getMovementData().getTo().getPitch() - user.getMovementData().getFrom().getPitch()) > 0.0));
    }


    public void processSmoother(User user) {

        float yawDelta = MathUtil.getDelta(user.getMovementData().getTo().getYawClamped(), user.getMovementData().getFrom().getYawClamped()), pitchDelta = MathUtil.getDelta(user.getMovementData().getTo().getPitch(), user.getMovementData().getFrom().getPitch());
        float smooth = user.newYawSmoothing.smooth(yawDelta, convertToMouseDelta(user.lastYawDelta)), smooth2 = user.newPitchSmoothing.smooth(pitchDelta, convertToMouseDelta(pitchDelta));
        double yaw = Math.abs(smooth - user.lastSmoothYaw), pitch = Math.abs(smooth2 - user.lastSmoothPitch1);

        if ((yaw > 0.0 && yaw < 0.09) || (pitch > 0.0 && pitch < 0.12)) {
            if (user.optifineConstantVL < 30) user.optifineConstantVL++;
            if (user.optifineSmoothingFix < 30) user.optifineSmoothingFix++;
        } else {
            if (user.optifineConstantVL > 0) user.optifineConstantVL--;
            if (user.optifineSmoothingFix > 0) user.optifineSmoothingFix -= 5;
        }
        if (user.optifineConstantVL > 9) {
            if (user.optifineConstantVL2 < 30) user.optifineConstantVL2++;
        } else {
            if (user.optifineConstantVL2 > 0) user.optifineConstantVL2--;
        }
        if (user.optifineConstantVL2 > 5) {
            user.lastOptifineREE = System.currentTimeMillis();
        }
        user.lastOptifinePitchSmoothidfklol = convertToMouseDelta(Math.abs(smooth - user.lastSmoothYaw));
        user.lastSmoothPitch1 = smooth2;
        user.lastSmoothYaw = smooth;
        user.lastYawDelta = yawDelta;
        user.lastPitchDelta = pitchDelta;
        user.newPitchSmoothing.reset();
        user.newYawSmoothing.reset();
    }

    private boolean isSmooth(User user, float f2, float f3) {
        return (Math.abs((user.getMovementData().getFrom().getYawClamped() + f2 * .15f) 
                - user.getMovementData().getFrom().getYawClamped())
                < (Math.abs(user.getMovementProcessor().getDeltaX()) 
                > 50 ? 3 : 1) && Math.abs((user.getMovementData().getFrom().getPitch()
                + f3 * .15f) - user.getMovementData().getTo().getPitch()) < (Math.abs(user.getMovementProcessor().getDeltaY()) > 30 ? 2 : 1));
    }

    public void updateOptifine(User user) {
        int ace = (user.killauraAYawReset > 2 ? (user.killauraAPitchReset * user.killauraAYawReset) : user.killauraAYawReset);
        if (ace > 3) {
            user.lastAimAssistACE = System.currentTimeMillis();
        }

        if (TimeUtils.elapsed(user.lastAimAssistACE) <= 100L) {
            if (user.aimAssistsACount > 0) user.aimAssistsACount--;
        }

        float yawDelta = MathUtil.getDelta(user.getMovementData().getTo().getYawClamped(), user.getMovementData().getFrom().getYawClamped()), pitchDelta = MathUtil.getDelta(user.getMovementData().getTo().getPitch(), user.getMovementData().getFrom().getPitch());
        float yawShit = convertToMouseDelta(yawDelta), pitchShit = convertToMouseDelta(pitchDelta);
        float smooth = user.yaw.smooth(yawShit, yawShit * 0.05f), smooth2 = user.pitch.smooth(pitchShit, pitchShit * 0.05f);

        user.cineCamera = MathUtil.getDelta(smooth, yawShit) < 0.08f || MathUtil.getDelta(smooth2, pitchShit) < 0.04f;

        if (user.cineCamera) {
            user.optifineSmoothingTicks++;
        } else if (user.optifineSmoothingTicks > 0) {
            user.optifineSmoothingTicks--;
        }

        if (user.getMovementData().getTo().getPitch() == user.getMovementData().getFrom().getPitch()) {
            user.killauraAPitchReset++;
        } else {
            user.killauraAPitchReset = 0;
        }

        if (user.getMovementData().getTo().getYawClamped() == user.getMovementData().getFrom().getYawClamped() || Math.abs(user.getMovementData().getTo().getYawClamped() - user.getMovementData().getFrom().getYawClamped()) == 0.0) {
            if (user.killauraAYawReset < 20) user.killauraAYawReset++;
        } else {
            if (user.killauraAYawReset > 0) user.killauraAYawReset--;
        }

        float yawDelta1 = Math.abs(user.getMovementData().getFrom().getYawClamped()
                - user.getMovementData().getTo().getYawClamped()), pitchDelta1 = Math.abs(user.getMovementData().getFrom().getYawClamped() - user.getMovementData().getTo().getYawClamped());

        float yaw = convertToMouseDelta(yawDelta1), pitch = convertToMouseDelta(pitchDelta1);

        float smoothing = ((float) Math.cbrt((yawDelta1 / 0.15f) / 8f) - 0.2f) / .6f;

        float smoothingpitchDelta = ((float) Math.cbrt((yawDelta1 / 0.15f) / 8f) - 0.2f) / .6f;


        float smooth1 = user.smooth.smooth(yaw, pitch * 0.05f);
        if ((Math.abs(user.lastSmoothingRot - smoothingpitchDelta) < 1F)) {
            if (user.optifinePitchSmooth < 20) user.optifinePitchSmooth++;
        } else {
            user.optifinePitchSmooth = 0;
        }

        boolean smoothing1 = (Math.abs(user.lastSmoothingRot - smoothing) < 0.1);

        boolean smoothing2 = (Math.abs(smooth1 - smoothing) > 0.2 && smoothing > 1.2);

        if ((smoothing1 || smoothing2) && MathUtil.looked(user.getMovementData().getFrom(), user.getMovementData().getTo())) {

            if (MathUtil.looked(user.getMovementData().getFrom(),
                    user.getMovementData().getTo()) && user.getMovementData().getFrom().getYawClamped()
                    != user.getMovementData().getTo().getYawClamped() && user.getMovementData().getFrom().getPitch()
                    != user.getMovementData().getTo().getPitch()) {
                if (user.optifineSmoothSens < 20) user.optifineSmoothSens++;
            } else {
                if (user.optifineSmoothSens > 0) user.optifineSmoothSens--;
            }
        } else {
            if (user.optifineSmoothSens > 0) user.optifineSmoothSens--;
        }

        if (Math.abs(user.smoothingCounter - user.LastSmoothingCounter) > 0) {
            user.smoothingCounter = 0;
        }

        if (user.optifineSameCount > 2) {
            user.optifineSmoothing = 0;
        }

        if (user.smoothingCounter > 4) {
            if (user.lastClientSmoothingValue == user.smoothingCounter) {
                user.optifineSameCount += 2;
            } else {
                if (user.optifineSameCount > 0) user.optifineSameCount--;
            }
            user.lastClientSmoothingValue = user.smoothingCounter;
            user.lastOptifine = System.currentTimeMillis();
            if (user.optifineSmoothing < 30) user.optifineSmoothing++;
        } else {
            if (user.optifineSmoothing > 0) user.optifineSmoothing -= 2;
        }

        if (smoothing1 || smoothing2) {
            if (MathUtil.looked(user.getMovementData().getFrom(), user.getMovementData().getTo()) && user.getMovementData().getFrom().getYawClamped() != user.getMovementData().getTo().getYawClamped() && user.getMovementData().getFrom().getPitch() != user.getMovementData().getTo().getPitch()) {
                user.smoothingCounter++;
            } else {
                if (user.smoothingCounter > 0) user.smoothingCounter--;
            }
            user.LastSmoothingCounter = user.smoothingCounter;
        } else {
            user.smoothingCounter = 0;
        }

        user.lastSmoothingRot = smoothing;
        user.lastSmoothingRot2 = smoothingpitchDelta;

    }


    private List<Float> sensitivities = Arrays.asList(.0f, .1f, .25f, .35f, .5f, .62f, .75f, 0.9f, 1f);


    private float findClosestCinematicYaw(float yaw, float lastYaw) {
        float value = sensitivities.stream().min(Comparator.comparing(val -> {
            float f = val * 0.6f + .2f;
            float f1 = (f * f * f) * 8f;
            float smooth = mouseFilterX.smooth(lastYaw, 0.05f * f1);
            mouseFilterX.reset();
            return MathUtil.getDelta(MathUtil.yawTo180F(yaw), MathUtil.yawTo180F(smooth));
        }, Comparator.naturalOrder())).orElse(1f);

        float f = value * 0.6f + .2f;
        float f1 = (f * f * f) * 8f;
        return yawSmooth.smooth(lastYaw, 0.05f * f1);
    }

    private float findClosestCinematicPitch(float pitch, float lastPitch) {
        float value = sensitivities.stream().min(Comparator.comparing(val -> {
            float f = val * 0.6f + .2f;
            float f1 = (f * f * f) * 8f;
            float smooth = mouseFilterY.smooth(lastPitch, 0.05f * f1);
            mouseFilterY.reset();
            return MathUtil.getDelta(pitch, smooth);
        }, Comparator.naturalOrder())).orElse(1f);

        float f = value * 0.6f + .2f;
        float f1 = (f * f * f) * 8f;
        return pitchSmooth.smooth(lastPitch, 0.05f * f1);
    }


    private float convertToMouseDelta(float value) {
        return ((float) Math.cbrt((value / 0.15f) / 8f) - 0.2f) / .6f;
    }

    private static float shit(float value) {
        return ((float) Math.cbrt((value / 0.15f) / 8f) - 0.2f) / .6f;
    }
}
