package dev.demon.venom.impl.check.impl.badpackets;

import dev.demon.venom.impl.check.Check;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.CustomPayLoadInEvent;

public class BadPacketsC extends Check {
    public BadPacketsC(String checkname, String checktype, boolean experimental, int banVL, boolean enabled) {
        super(checkname, checktype, experimental, banVL, enabled);
    }

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof CustomPayLoadInEvent) {
            String channel = ((CustomPayLoadInEvent) e).getChannel();

            if (channel.equalsIgnoreCase("LOLIMAHCKER")) {

                handleDetection(user, "Vape Cracked PME");

            } else if (channel.equalsIgnoreCase("L0LIMAHCKER")) {

                handleDetection(user, "Vape Cracked PME");

            } else if (channel.equalsIgnoreCase("customGuiOpenBspkrs")) {

                handleDetection(user, "Bspkrs Core Client PME");

            } else if (channel.equalsIgnoreCase("0SO1Lk2KASxzsd")) {

                handleDetection(user, "Bspkrs Core Client PME");

            } else if (channel.equalsIgnoreCase("mincraftpvphcker")) {

                handleDetection(user, "Bspkrs Core Client PME");

            } else if (channel.equalsIgnoreCase("lmaohax")) {

                handleDetection(user, "Incognito Cracked PME");

            } else if (channel.equalsIgnoreCase("gc")) {

                handleDetection(user, "Ghost Client PME");

            } else if (channel.equalsIgnoreCase("ethylene")) {

                handleDetection(user, "Ethylene Client PME");

            } else if (channel.equalsIgnoreCase("mergeclient")) {

                handleDetection(user, "Vape Cracked PME");

            } else if (channel.equalsIgnoreCase("wigger")) {

                handleDetection(user, "Vape Cracked PME");

            } else if (channel.equalsIgnoreCase("Schematica")) {

                handleDetection(user, "Schematica Reach PME");

            } else if (channel.equalsIgnoreCase("timechanger")) {

                handleDetection(user, "Time Changer Client PME");

            }  else if (channel.equalsIgnoreCase("MCnetHandler")) {

                handleDetection(user, "Old Time Changer Client PME");


            } else if (channel.equalsIgnoreCase("TcpNoDelayMod-2.0")) {

                handleDetection(user, "TcpNoDelay Client PME");
            }
        }
    }
}
