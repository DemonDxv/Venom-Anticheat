package dev.demon.venom.api.check;

import dev.demon.venom.Venom;
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
    public boolean enabled, experimental, ban;

    private boolean isTestServer = false;

    public Check() {
        enabled = true;
        ban = false;
    }

    public String getName() {
        return CheckManager.getCheckInfo(this).name();
    }

    public String getType() {
        return CheckManager.getCheckInfo(this).type();
    }

    public abstract void onHandle(User user, AnticheatEvent e);

    protected void alert(User user, String... strings) {

        StringBuilder dataStr = new StringBuilder();
        for (String s : strings) {
            dataStr.append(s).append((strings.length == 1 ? "" : ", "));
        }

        String alert = Venom.alertsMessage.replace("%player%", user.getPlayer().getName()).replace("%check%", getName()).replace("%type%", getType()).replace("%vl%", String.valueOf(user.getViolation())).replace("&", "§");
     //   String alert = ChatColor.DARK_GRAY + "[" + ChatColor.RED +  "Xan" + ChatColor.DARK_GRAY + "]" + ChatColor.RED + " // " + ChatColor.WHITE + user.getPlayer().getName() + ChatColor.GRAY + " has flagged " + ChatColor.WHITE + getName() + ChatColor.WHITE + " " + ChatColor.WHITE + getType() + ChatColor.RED + " (x" + user.getViolation() + ")";


        if (user.getFlaggedChecks().containsKey(this)) {
            user.getFlaggedChecks().put(this, user.getFlaggedChecks().get(this) + 2);
        } else user.getFlaggedChecks().put(this, 1);


        if (Venom.enableDebug) {
            TextComponent textComponent = new TextComponent(alert);

            if (dataStr.length() > 0) {
                textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.RED + dataStr.toString()).create()));
            }

            Venom.getInstance().getUserManager().getUsers().stream().parallel().filter(staff -> (staff.getPlayer().hasPermission(Venom.permissionAlert) && staff.isAlerts())).forEach(staff -> staff.getPlayer().spigot().sendMessage(textComponent));
        }else {
            Venom.getInstance().getUserManager().getUsers().stream().parallel().filter(staff -> (staff.getPlayer().hasPermission(Venom.permissionAlert) && staff.isAlerts())).forEach(staff -> staff.getPlayer().sendMessage(alert));
        }

        if (Venom.banEnabled && user.getViolation() >= Venom.banVL && !user.isBanned() && !user.getPlayer().isOp()) {
            user.setViolation(0);
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


        user.setViolation(user.getViolation() + 1);
    }
}