package dev.demon.venom.api.check;

/*
import dev.demon.venom.impl.checks.combat.autoclicker.*;
import dev.demon.venom.impl.checks.combat.killaura.*;
import dev.demon.venom.impl.checks.combat.reach.*;
import dev.demon.venom.impl.checks.combat.velocity.*;
import dev.demon.venom.impl.checks.movement.flight.*;
import dev.demon.venom.impl.checks.movement.speed.*;
import dev.demon.venom.impl.checks.player.badpackets.*;
import dev.demon.venom.impl.checks.player.timer.*;*/

import dev.demon.venom.impl.checks.combat.aimassist.*;
import dev.demon.venom.impl.checks.combat.autoclicker.*;
import dev.demon.venom.impl.checks.combat.killaura.*;
import dev.demon.venom.impl.checks.combat.reach.*;
import dev.demon.venom.impl.checks.combat.velocity.*;
import dev.demon.venom.impl.checks.movement.fly.*;
import dev.demon.venom.impl.checks.movement.speed.*;
import dev.demon.venom.impl.checks.other.badpackets.*;
import dev.demon.venom.impl.checks.other.timer.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CheckManager {
    private static final Class[] checks = new Class[] {

            //Combat
            AimAssistA.class,
            AimAssistB.class,
            AimAssistC.class,
            AimAssistD.class,

            AutoClickerA.class,
            AutoClickerB.class,
            AutoClickerC.class,
            AutoClickerD.class,
            AutoClickerE.class,
            AutoClickerF.class,
            AutoClickerG.class,
            AutoClickerH.class,

            KillauraA.class,
            KillauraB.class,
            KillauraC.class,
            KillauraD.class,

            ReachA.class,
            ReachB.class,

            VelocityA.class,
            VelocityB.class,
            VelocityC.class,
            VelocityD.class,
            VelocityE.class,

            //Movement
            SpeedA.class,

            FlyA.class,
            FlyB.class,

            //Other
            BadPacketsA.class,
            BadPacketsB.class,
            BadPacketsC.class,
            BadPacketsD.class,

            TimerA.class,


    };

    public static List<Check> loadChecks() {
        List<Check> checklist = new ArrayList<>();
        Arrays.asList(checks).forEach(check -> {
            try {
                checklist.add((Check) check.getConstructor().newInstance());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return checklist;
    }

    public static CheckInfo getCheckInfo(Check check) {
        return check.getClass().getAnnotation(CheckInfo.class);
    }
}
