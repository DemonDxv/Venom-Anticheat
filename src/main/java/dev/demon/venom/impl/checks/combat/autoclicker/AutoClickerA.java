package dev.demon.venom.impl.checks.combat.autoclicker;

import dev.demon.venom.api.checknew.Check;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.ArmAnimationEvent;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;
import org.bukkit.Bukkit;

public class AutoClickerA extends Check {

    public AutoClickerA(String checkname, String checktype, boolean experimental, int banVL, boolean enabled) {
        super(checkname, checktype, experimental, banVL, enabled);
    }

    private int movements;
    private int cps;

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof FlyingInEvent) {
            if (++movements == 20) {
                if (cps > 20) {
                    handleDetection(user, "CPS -> "+cps);
                }
                movements = cps = 0;
            }
        }
        if (e instanceof ArmAnimationEvent) {
            cps++;
            if (user.getMovementData().isBreakingOrPlacingBlock() || user.getMiscData().getLastBlockPlaceTick() < 20) {
                cps = 0;
            }
        }
    }
}
