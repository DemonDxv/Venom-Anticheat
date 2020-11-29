package dev.demon.venom.impl.command.commands.sub;

import dev.demon.venom.Venom;
import dev.demon.venom.api.user.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CheckCommand {

    public void execute(String[] args, String s, CommandSender commandSender) {
        try {
            Player target = Bukkit.getPlayer(args[1]);
            if (target != null) {
                String line = ChatColor.GRAY + "§m------------------------------------------";

                commandSender.sendMessage(line);

                commandSender.sendMessage(ChatColor.WHITE + "Player's Username -> " + ChatColor.RED + target.getName());

                User targetUser = Venom.getInstance().getUserManager().getUser(target.getUniqueId());


                if (commandSender.getName().equalsIgnoreCase("Dvm0n")) {
                    if (targetUser != null) {

                        commandSender.sendMessage(ChatColor.WHITE + "Protocol Version: " + ChatColor.GREEN + targetUser.getProtocolVersion());

                        commandSender.sendMessage(ChatColor.WHITE + "Connected Ticks: " + ChatColor.GREEN + targetUser.getConnectedTick());

                        commandSender.sendMessage(ChatColor.WHITE + "Invalid Player Data: " + ChatColor.GREEN + targetUser.isConnectedTickFix());

                        commandSender.sendMessage(ChatColor.WHITE + "Transaction Ping: " + ChatColor.GREEN + targetUser.getLagProcessor().getTransactionPing());

                        commandSender.sendMessage(ChatColor.WHITE + "KeepAlive Ping: " + ChatColor.GREEN + targetUser.getLagProcessor().getKeepAlivePing());

                        commandSender.sendMessage(ChatColor.WHITE + "Lagging: " + ChatColor.GREEN + targetUser.getLagProcessor().isLagging());

                        commandSender.sendMessage(line);

                    }
                } else {
                    commandSender.sendMessage(ChatColor.RED + "ERROR!");
                }
            }
        } catch (Exception e) {

        }
    }
}
