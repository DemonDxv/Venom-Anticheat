package dev.demon.venom.impl.checks.combat.killaura;

import dev.demon.venom.api.check.Check;
import dev.demon.venom.api.check.CheckInfo;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;
import dev.demon.venom.impl.events.inevents.UseEntityEvent;
import dev.demon.venom.utils.math.MathUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_7_R4.SpigotTimings;
import org.omg.PortableServer.POAPackage.AdapterAlreadyExists;

import java.util.Collections;
import java.util.Set;

@CheckInfo(name = "Killaura", type = "F", banvl = 20)
public class KillauraF extends Check {
    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof UseEntityEvent) {
            if (((UseEntityEvent) e).getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK) {

                double pitch = Math.abs(user.getMovementData().getTo().getPitch() - user.getMovementData().getFrom().getPitch());
                double yaw = Math.abs(user.getMovementData().getTo().getYaw() - user.getMovementData().getFrom().getYaw());

                Block blockAt = user.getPlayer().getTargetBlock(Collections.singleton(user.getPlayer().getEyeLocation().getBlock().getType()), 1);

                if (blockAt != null && blockAt.getType().isSolid() && yaw > 0.1 && pitch > 0.1) {
                    violation++;
                } else {
                    violation = 0;
                }

                if (violation > 4) {
                    alert(user, false, "Hit through a wall");
                }
            }
        }
    }
}
