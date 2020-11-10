package dev.demon.venom.impl.checks.other.badpackets;

import dev.demon.venom.api.check.Check;
import dev.demon.venom.api.check.CheckInfo;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.CloseWindowInEvent;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;

@CheckInfo(name = "BadPackets", type = "G", banvl = 10)
public class BadPacketsG extends Check {

    private int lastID;
    private int ticks;

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof CloseWindowInEvent) {
            if (((CloseWindowInEvent) e).getId() == lastID) {
                ticks++;

                if (ticks >= 2) {
                    alert(user, false, "T -> "+ticks);
                }
            }
            lastID = ((CloseWindowInEvent) e).getId();
        }

        if (e instanceof FlyingInEvent) {
            ticks = 0;
        }
    }
}
