package dev.demon.venom.impl.checks.other.badpackets;

import dev.demon.venom.Venom;
import dev.demon.venom.api.check.Check;
import dev.demon.venom.api.check.CheckInfo;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.BlockPlaceEvent;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;
import dev.demon.venom.utils.time.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

@CheckInfo(name = "BadPackets", type = "W", banvl = 100)
public class BadPacketsW extends Check {

    private long lastPing;

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof FlyingInEvent) {
            if (user.getLagProcessor().getLastPing() >= 750) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "kick " + user.getPlayer().getName() + " Timed out.");
                    }
                }.runTask(Venom.getInstance());
            }
            long keepAlivePing = user.getLagProcessor().getCurrentPing();

            if (Math.abs(keepAlivePing - lastPing) == 0) {
                if (violation++ > 300) {
                    alert(user, false, "PD -> "+Math.abs(keepAlivePing - lastPing) + " LKA -> "+user.getLagProcessor().getLastKeepAlive());
                }
            } else {
                violation = 0;
            }
            lastPing = keepAlivePing;
        }
    }
}