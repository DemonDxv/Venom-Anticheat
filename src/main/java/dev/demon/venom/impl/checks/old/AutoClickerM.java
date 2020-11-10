package dev.demon.venom.impl.checks.old;

import dev.demon.venom.api.check.Check;
import dev.demon.venom.api.check.CheckInfo;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.tinyprotocol.packet.in.WrappedInBlockDigPacket;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.ArmAnimationEvent;
import dev.demon.venom.impl.events.inevents.BlockDigEvent;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;
import dev.demon.venom.utils.math.GraphUtil;


import java.util.ArrayList;
import java.util.List;

@CheckInfo(name = "AutoClicker", type = "M", banvl = 5)
public class AutoClickerM extends Check {

    private double ticks, averageSpeed;
    private List<Double> autoClickerMList = new ArrayList<>();

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof ArmAnimationEvent || e instanceof BlockDigEvent) {
            double ticks = this.ticks;
            this.ticks = 0.0;

            if (ticks > 5) return;

            double speed = ticks * 50.0;
            averageSpeed = ((averageSpeed * 2) + speed) / 3;

            double clicksPerSecond = Math.abs(1000 / averageSpeed);

            if (clicksPerSecond >= 8.0) {
                autoClickerMList.add(clicksPerSecond);
            }

            if (autoClickerMList.size() >= 100) {
                GraphUtil.GraphResult graphedList = GraphUtil.getGraph(autoClickerMList);

                int positive = graphedList.getPositives(), negative = graphedList.getNegatives();

                if (positive > negative && negative > 10) {
                    alert(user, true, "P -> " + positive + " N -> " + negative);
                }

                if (positive >= 100) {
                    alert(user, false, "P -> "+positive + " N -> "+negative + " [1]");
                }


                autoClickerMList.clear();
            }

        } else if (e instanceof FlyingInEvent) {
            ticks++;
        }

        if (e instanceof BlockDigEvent) {
            if (((BlockDigEvent) e).getAction() == WrappedInBlockDigPacket.EnumPlayerDigType.START_DESTROY_BLOCK) {
                violation = 0.0;
            }
        }
    }
}
