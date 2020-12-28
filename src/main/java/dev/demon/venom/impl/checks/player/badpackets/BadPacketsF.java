package dev.demon.venom.impl.checks.player.badpackets;

import dev.demon.venom.api.checknew.Check;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.tinyprotocol.packet.in.WrappedInBlockDigPacket;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.BlockDigEvent;
import dev.demon.venom.impl.events.inevents.BlockPlaceEvent;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;
import dev.demon.venom.utils.math.MathUtil;
import dev.demon.venom.utils.time.TimeUtils;
import org.bukkit.Bukkit;

public class BadPacketsF extends Check {
    public BadPacketsF(String checkname, String checktype, boolean experimental, int banVL, boolean enabled) {
        super(checkname, checktype, experimental, banVL, enabled);
    }

    private long lastDigging;

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof FlyingInEvent) {
            if (user.getLagProcessor().isLagging()) {
                lastDigging = 1000L;
                violation = 0;
                return;
            }
            boolean item = TimeUtils.elapsed(user.getMiscData().getLastBlockPlace()) < 100L
                    || user.getMiscData().isSword(user.getPlayer().getItemInHand());

            if (TimeUtils.elapsed(lastDigging) <= 5L && item) {
                if (violation++ > 10) {
                    handleDetection(user, "Sent release use item action late");
                }
            } else violation = 0;
        }
        if (e instanceof BlockDigEvent) {
            if (((BlockDigEvent) e).getAction() == WrappedInBlockDigPacket.EnumPlayerDigType.RELEASE_USE_ITEM) {
                lastDigging = System.currentTimeMillis();
            }
        }
    }
}
