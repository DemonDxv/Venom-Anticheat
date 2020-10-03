package dev.demon.venom.impl.command.commands.sub;


import dev.demon.venom.Venom;
import dev.demon.venom.api.user.User;

import dev.demon.venom.api.user.User;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class AlertsCommand {

    public void execute(String[] args, String s, CommandSender commandSender) {
        User user = Venom.getInstance().getUserManager().getUser(((Player) commandSender).getUniqueId());
        if (user != null) {
            if (user.isAlerts()) {
                user.setAlerts(false);
                commandSender.sendMessage(ChatColor.RED + "Alerts have been toggled off!");
            } else {
                user.setAlerts(true);
                commandSender.sendMessage(ChatColor.GREEN + "Alerts have been toggled on!");
            }
        }
    }
}
