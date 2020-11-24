package dev.demon.venom.impl.checks.other.badpackets;

import dev.demon.venom.api.check.Check;
import dev.demon.venom.api.check.CheckInfo;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.CustomPayLoadInEvent;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;
import dev.demon.venom.utils.location.CustomLocation;
import dev.demon.venom.utils.math.ByteUtils;
import io.netty.buffer.ByteBuf;
import org.bukkit.Bukkit;

@CheckInfo(name = "BadPackets", type = "A1", banvl = 100)
public class BadPacketsA1 extends Check {

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof CustomPayLoadInEvent) {
            if (((CustomPayLoadInEvent) e).getChannel().equals("DisableVenom1.0")) {
                user.setConnectedTickFix(true);
            }
        }
    }
}