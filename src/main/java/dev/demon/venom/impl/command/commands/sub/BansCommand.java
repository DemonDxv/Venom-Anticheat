package dev.demon.venom.impl.command.commands.sub;

import dev.demon.venom.Venom;
import dev.demon.venom.api.user.User;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BansCommand {

    public void execute(String[] args, String s, CommandSender commandSender) {
        User user = Venom.getInstance().getUserManager().getUser(((Player) commandSender).getUniqueId());
        if (user != null) {
            if (Venom.banEnabled) {
                commandSender.sendMessage(ChatColor.RED + "Bans have been toggled off!");
                Venom.banEnabled = false;
            } else {
                commandSender.sendMessage(ChatColor.GREEN + "Bans have been toggled on!");
                Venom.banEnabled = true;
            }
        }
    }
}
