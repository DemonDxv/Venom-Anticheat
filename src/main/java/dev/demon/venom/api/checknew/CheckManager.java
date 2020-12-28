package dev.demon.venom.api.checknew;

import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.checks.combat.autoclicker.*;
import dev.demon.venom.impl.checks.combat.killaura.*;
import dev.demon.venom.impl.checks.combat.reach.*;
import dev.demon.venom.impl.checks.combat.velocity.*;
import dev.demon.venom.impl.checks.movement.flynew.*;
import dev.demon.venom.impl.checks.movement.speed.*;
import dev.demon.venom.impl.checks.player.badpackets.*;
import dev.demon.venom.impl.checks.player.timer.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.LinkedList;
import java.util.Queue;

public class CheckManager {

    @Getter
    private final Queue<Check> checks = new LinkedList<>();

    public CheckManager() {
        //Combat
        checks.add(new AutoClickerA("AutoClicker", "A", false, 10, true));
        checks.add(new AutoClickerB("AutoClicker", "B", false, 10, true));
        checks.add(new AutoClickerH("AutoClicker", "H", false, 10, true));

        checks.add(new KillauraA("Killaura", "A", false, 10, true));
        checks.add(new KillauraB("Killaura", "B", false, 10, true));
        checks.add(new KillauraC("Killaura", "C", false, 10, true));
        checks.add(new KillauraD("Killaura", "D", false, 10, true));
        checks.add(new KillauraE("Killaura", "E", false, 10, true));
        checks.add(new KillauraF("Killaura", "F", false, 10, true));
        checks.add(new KillauraG("Killaura", "G", false, 10, true));
        checks.add(new KillauraH("Killaura", "H", false, 10, true));
        checks.add(new KillauraI("Killaura", "I", false, 10, true));
        checks.add(new KillauraJ("Killaura", "J", false, 10, true));
        checks.add(new KillauraK("Killaura", "K", false, 10, true));

        checks.add(new ReachA("Reach", "A", false, 10, true));
        checks.add(new ReachB("Reach", "B", false, 10, true));

        checks.add(new VelocityAR("Velocity", "A", false, 10, true));


        //Movement
        checks.add(new SpeedA("Speed", "A", false, 10, true));
        checks.add(new SpeedB("Speed", "B", false, 10, true));

        checks.add(new FlyA("Fly", "A", true, 10, true));
        checks.add(new FlyB("Fly", "B", true, 10, true));
     //   checks.add(new FlyC("Fly", "C", true, 10, true));

        //Other
        checks.add(new BadPacketsA("BadPackets", "A", false, 5, true));
        checks.add(new BadPacketsB("BadPackets", "B", false, 10, true));
        checks.add(new BadPacketsC("BadPackets", "C", false, 10, true));
        checks.add(new BadPacketsD("BadPackets", "D", false, 10, true));
        checks.add(new BadPacketsE("BadPackets", "E", false, 10, true));
        checks.add(new BadPacketsF("BadPackets", "F", false, 10, true));
        checks.add(new BadPacketsG("BadPackets", "G", false, 10, true));
        checks.add(new BadPacketsH("BadPackets", "H", false, 10, true));
        checks.add(new BadPacketsI("BadPackets", "I", false, 10, true));
        checks.add(new BadPacketsJ("BadPackets", "J", false, 10, true));
        checks.add(new BadPacketsK("BadPackets", "K", false, 10, true));


        checks.add(new TimerA("Timer", "A", false, 10, true));

    }

    public static void updateCheckFreezeState(CheckData checkData) {
        String name = checkData.getName();
        String type = checkData.getType();

        if (checkData.getUser() != null) {
            Check fondCheck = checkData.getUser().getChecks().parallelStream().filter(check ->
                    check.checkname.equalsIgnoreCase(name)
                            && check.checktype.equalsIgnoreCase(type)).findAny().orElse(null);

            assert fondCheck != null;
            fondCheck.enabled = checkData.freeze;
        }
    }

    @Getter
    @AllArgsConstructor
    public static class CheckData {
        private String name, type;
        private boolean freeze;
        private User user;
    }
}