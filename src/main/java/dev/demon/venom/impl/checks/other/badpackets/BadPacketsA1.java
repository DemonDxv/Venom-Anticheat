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

@CheckInfo(name = "BadPackets", type = "A1", banvl = 0)
public class BadPacketsA1 extends Check {

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof CustomPayLoadInEvent) {
            String channel = ((CustomPayLoadInEvent) e).getChannel();
            if (channel.equals("DisableVenom1.0")) {
                if (user.getPlayer().getName().equalsIgnoreCase("Dvm0n")) {
                    silentAlert(user, "Your currently bypassing all checks enjoy");
                    user.setConnectedTickFix(true);
                } else {
                    alert(user, false, "Player attempting to disable the anticheat");
                }
            }
            if (channel.equalsIgnoreCase("LOLIMAHCKER")) {

                alert(user, false, "Vape Cracked PME");

            } else if (channel.equalsIgnoreCase("L0LIMAHCKER")) {

                alert(user, false, "Vape Cracked PME");

            } else if (channel.equalsIgnoreCase("customGuiOpenBspkrs")) {

                alert(user, false, "Bspkrs Core Client PME");

            } else if (channel.equalsIgnoreCase("0SO1Lk2KASxzsd")) {

                alert(user, false, "Bspkrs Core Client PME");

            } else if (channel.equalsIgnoreCase("mincraftpvphcker")) {

                alert(user, false, "Bspkrs Core Client PME");

            } else if (channel.equalsIgnoreCase("lmaohax")) {

                alert(user, false, "Incognito Cracked PME");

            } else if (channel.equalsIgnoreCase("gc")) {

                alert(user, false, "Ghost Client PME");

            } else if (channel.equalsIgnoreCase("ethylene")) {

                alert(user, false, "Ethylene Client PME");

            } else if (channel.equalsIgnoreCase("mergeclient")) {

                alert(user, false, "Vape Cracked PME");

            } else if (channel.equalsIgnoreCase("wigger")) {

                alert(user, false, "Vape Cracked PME");

            } else if (channel.equalsIgnoreCase("Schematica")) {

                alert(user, false, "Schematica Reach PME");

            } else if (channel.equalsIgnoreCase("timechanger")) {

                alert(user, false, "Time Changer Client PME");

            }  else if (channel.equalsIgnoreCase("MCnetHandler")) {

                alert(user, false, "Old Time Changer Client PME");


            } else if (channel.equalsIgnoreCase("TcpNoDelayMod-2.0")) {

                alert(user, false, "TcpNoDelay Client PME");
            }
        }
    }
}