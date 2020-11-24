package dev.demon.venom.impl.checks.other.badpackets;

import dev.demon.venom.Venom;
import dev.demon.venom.api.check.Check;
import dev.demon.venom.api.check.CheckInfo;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.inevents.FlyingInEvent;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

@CheckInfo(name = "BadPackets", type = "B1", banvl = 100)
public class BadPacketsB1 extends Check {

    private long lastTransaction;

    @Override
    public void onHandle(User user, AnticheatEvent e) {
        if (e instanceof FlyingInEvent) {
            if (user.getLagProcessor().getLastTransaction() >= 750) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "kick " + user.getPlayer().getName() + " Timed out.");
                    }
                }.runTask(Venom.getInstance());
            }
            long transactionPing = user.getLagProcessor().getLastTransaction();

            if (Math.abs(transactionPing - lastTransaction) == 0) {
                if (violation++ > 300) {
                    alert(user, false, "PD -> " + Math.abs(transactionPing - lastTransaction) + " LT -> "+user.getLagProcessor().getLastTransaction());
                }
            } else {
                violation = 0;
            }
            lastTransaction = transactionPing;
        }
    }
}