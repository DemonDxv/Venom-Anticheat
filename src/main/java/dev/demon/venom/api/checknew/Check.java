package dev.demon.venom.api.checknew;

import dev.demon.venom.Venom;
import dev.demon.venom.api.checknew.CheckManager;
import dev.demon.venom.api.event.AnticheatEvent;
import dev.demon.venom.api.event.AnticheatListener;
import dev.demon.venom.api.user.User;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class Check implements AnticheatListener {

    public double violation;
    public boolean enabled, experimental;
    public String checkname, checktype;
    private int banVL;

    public Check(String checkname, String checktype, boolean experimental, int banVL, boolean enabled) {
        this.enabled = enabled;
        this.checkname = checkname;
        this.checktype = checktype;
        this.banVL = banVL;
        this.experimental = experimental;
    }

    public abstract void onHandle(User user, AnticheatEvent e);

    protected void silentDetection(User user, String message) {
        if (!enabled) return;
        user.getPlayer().sendMessage(ChatColor.RED + "[Alert] -> " + ChatColor.WHITE + message + " [Message is hidden]");
    }


    public void handleDetection(User user, String... strings) {
        if (user.isConnectedTickFix() || !enabled) {
            return;
        }

        StringBuilder dataStr = new StringBuilder();
        for (String s : strings) {
            dataStr.append(s).append((strings.length == 1 ? "" : ", "));
        }

        String alert = Venom.alertsMessage.replace("%player%", user.getPlayer().getName()).replace("%check%", checkname).replace("%type%", checktype).replace("%vl%", String.valueOf(user.getVL(this))).replace("&", "§") + (experimental ? ChatColor.RED + " *" : "");

        if (user.getFlaggedChecks().containsKey(this)) {
            user.getFlaggedChecks().put(this, user.getFlaggedChecks().get(this) + 1);
        } else user.getFlaggedChecks().put(this, 1);


        if (Venom.enableDebug && !user.isBanned()) {
            TextComponent textComponent = new TextComponent(alert);

            if (dataStr.length() > 0) {
                textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.RED + dataStr.toString()).create()));
            }
            Venom.getInstance().getUserManager().userMapToList().stream().parallel().filter(staff -> (staff.getPlayer().hasPermission(Venom.permissionAlert) && staff.isAlerts())).forEach(staff -> staff.getPlayer().spigot().sendMessage(textComponent));
        } else if (!user.isBanned()) {
            Venom.getInstance().getUserManager().userMapToList().stream().parallel().filter(staff -> (staff.getPlayer().hasPermission(Venom.permissionAlert) && staff.isAlerts())).forEach(staff -> staff.getPlayer().sendMessage(alert));
        }

        if (Venom.banEnabled && user.getVL(this) >= banVL && !user.isBanned() && !experimental) {
            user.resetVL( this);
            user.setBanned(true);
            new BukkitRunnable() {
                @Override
                public void run() {
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), Venom.banCommand.replace("%player%", user.getPlayer().getName()).replace("&", "§"));
                }
            }.runTask(Venom.getInstance());

            if (Venom.banMessageEnabled) {
                Bukkit.broadcastMessage("\n" + Venom.banMessage.replace("&", "§").replace("%player%", user.getPlayer().getName()) + " \n ");
            }
        }


        if (!experimental && !user.isBanned()) {
            user.addVL( this);
        }

    }
}