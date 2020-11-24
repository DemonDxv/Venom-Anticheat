package dev.demon.venom.api.check;

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
    public static final Class[] checks = new Class[] {

            //Combat
            AimAssistA.class,
            AimAssistB.class,
            AimAssistC.class,
          //  AimAssistD.class,
          //  AimAssistE.class,

            AutoClickerA.class,
            AutoClickerB.class,
            AutoClickerC.class,
            AutoClickerD.class,
            AutoClickerE.class,
            AutoClickerF.class,
            AutoClickerG.class,
            AutoClickerH.class,
            AutoClickerI.class,
            AutoClickerJ.class,
            AutoClickerK.class,

            KillauraA.class,
            KillauraB.class,
            KillauraC.class,
            KillauraD.class,
            KillauraE.class,
            KillauraF.class,
            KillauraG.class,
            KillauraH.class,
            KillauraI.class,

            ReachA.class,

            VelocityA.class,
          //  VelocityB.class,
            VelocityC.class,
            VelocityD.class,
            VelocityE.class,
        //    VelocityF.class,
            VelocityG.class,
          //  VelocityH.class,

            //Movement
            SpeedA.class,
            SpeedB.class,

            FlyA.class,
            FlyB.class,
            FlyC.class,



            //Other
            BadPacketsA.class,
            BadPacketsB.class,
            BadPacketsC.class,
            BadPacketsD.class,
            BadPacketsE.class,
            BadPacketsF.class,
            BadPacketsG.class,
            BadPacketsH.class,
            BadPacketsI.class,
            BadPacketsJ.class,
            BadPacketsK.class,
          //  BadPacketsL.class,
          //  BadPacketsM.class,
          //  BadPacketsN.class,
          //  BadPacketsO.class,
            BadPacketsP.class,
            BadPacketsQ.class,
            BadPacketsR.class,
            BadPacketsS.class,
          //  BadPacketsT.class,
            BadPacketsU.class,
            BadPacketsV.class,
        //    BadPacketsW.class,
        //    BadPacketsX.class,
            BadPacketsY.class,
            BadPacketsZ.class,

            BadPacketsA1.class,
            BadPacketsB1.class,

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
