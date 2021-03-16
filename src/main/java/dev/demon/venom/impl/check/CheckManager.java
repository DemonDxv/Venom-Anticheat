package dev.demon.venom.impl.check;

import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.check.impl.badpackets.*;
import dev.demon.venom.impl.check.impl.fly.*;
import dev.demon.venom.impl.check.impl.inventory.*;
import dev.demon.venom.impl.check.impl.reach.ReachA;
import dev.demon.venom.impl.check.impl.speed.*;
import dev.demon.venom.impl.check.impl.timer.*;
import dev.demon.venom.impl.check.impl.velocity.*;
import dev.demon.venom.utils.math.MathUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.swing.plaf.BorderUIResource;
import java.util.LinkedList;
import java.util.Queue;

public class CheckManager {

    @Getter
    private final Queue<Check> checks = new LinkedList<>();

    public CheckManager() {
        //Combat
        checks.add(new ReachA("Reach", "A", true, 10, true));

        checks.add(new VelocityA("Velocity", "A", false, 10, true));
        checks.add(new VelocityB("Velocity", "B", true, 10, true));
        checks.add(new VelocityC("Velocity", "C", true, 10, true));
        checks.add(new VelocityD("Velocity", "D", true, 10, true));
        checks.add(new VelocityE("Velocity", "E", true, 10, true));

        //Movement
        checks.add(new Speed("Speed", "A", true, 10, true));

        checks.add(new FlyA("Fly", "A", true, 10, true));
        checks.add(new FlyB("Fly", "B", true, 10, true));
        checks.add(new FlyC("Fly", "C", true, 10, true));

        //Other
        checks.add(new InventoryA("Inventory", "A", false, 10, true));
        checks.add(new InventoryB("Inventory", "B", false, 10, true));

        checks.add(new BadPacketsA("BadPackets", "A", false, 5, true));
        checks.add(new BadPacketsB("BadPackets", "B", false, 10, true));
        checks.add(new BadPacketsC("BadPackets", "C", false, 10, true));
        checks.add(new BadPacketsD("BadPackets", "D", false, 10, true));
        checks.add(new BadPacketsE("BadPackets", "E", false, 10, true));
        checks.add(new BadPacketsF("BadPackets", "F", false, 10, true));
        //checks.add(new BadPacketsG("BadPackets", "G", false, 10, true));
        checks.add(new BadPacketsI("BadPackets", "I", false, 10, true));
        checks.add(new BadPacketsJ("BadPackets", "J", false, 10, true));


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