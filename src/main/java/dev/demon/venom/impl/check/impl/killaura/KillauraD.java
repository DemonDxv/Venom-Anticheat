package dev.demon.venom.impl.check.impl.killaura;

import dev.demon.venom.impl.check.Check;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.tinyprotocol.packet.in.WrappedInBlockDigPacket;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.BlockDigEvent;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;
import dev.demon.venom.utils.math.MathUtil;

public class KillauraD extends Check {

    public KillauraD(String checkname, String checktype, boolean experimental, int banVL, boolean enabled) {
        super(checkname, checktype, experimental, banVL, enabled);
    }

    private long lastFlying;

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof FlyingInEvent) {
            lastFlying = System.currentTimeMillis();
        }
        if (e instanceof BlockDigEvent) {
            if (((BlockDigEvent) e).getAction() == WrappedInBlockDigPacket.EnumPlayerDigType.RELEASE_USE_ITEM) {
                if (user.getPlayer().getItemInHand() != null
                        && user.getMiscData().isSword(user.getPlayer().getItemInHand())) {

                    if (MathUtil.isPost(lastFlying)) {
                        if (violation++ > 10) {
                            handleDetection(user, "Sent block dig packet late.");
                        }
                    } else {
                        violation = 0;
                    }
                }
            }
        }
    }
}
