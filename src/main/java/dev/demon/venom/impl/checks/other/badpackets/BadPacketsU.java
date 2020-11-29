package dev.demon.venom.impl.checks.other.badpackets;

import dev.demon.venom.api.check.Check;
import dev.demon.venom.api.check.CheckInfo;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.tinyprotocol.packet.in.WrappedInBlockDigPacket;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.BlockDigEvent;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;
import dev.demon.venom.impl.events.inevents.TransactionEvent;
import dev.demon.venom.utils.time.TimeUtils;

@CheckInfo(name = "BadPackets", type = "U", banvl = 5)
public class BadPacketsU extends Check {

    private long lastDig;

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof FlyingInEvent) {
            if (user.getLagProcessor().isLagging()) {
                lastDig = 1000L;
                violation = 0;
                return;
            }
            if (TimeUtils.elapsed(lastDig) <= 5L && user.getMiscData().isSword(user.getPlayer().getItemInHand())) {
                if (violation++ > 10) {
                    alert(user, false, "Spamming Dig Packets");
                }
            } else violation = 0;
        }
        if (e instanceof BlockDigEvent) {
            if (((BlockDigEvent) e).getAction() == WrappedInBlockDigPacket.EnumPlayerDigType.RELEASE_USE_ITEM) {
                lastDig = System.currentTimeMillis();
            }
        }
    }
}