package dev.demon.venom.impl.command.commands;

import dev.demon.venom.Venom;
import dev.demon.venom.impl.command.commands.sub.*;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class MainCommand extends BukkitCommand {

    private String line = ChatColor.GRAY + "§m------------------------------------------";

    private InformationCommand informationCommand = new InformationCommand();
    private AlertsCommand alertsCommand = new AlertsCommand();
    private CheckCommand checkCommand = new CheckCommand();
    private ChecksCommand checksCommand = new ChecksCommand();
    private BansCommand bansCommand = new BansCommand();


    public MainCommand(String name) {
        super(name);
        this.description = "Anticheat command.";
        this.usageMessage = "/" + name;
        this.setAliases(new ArrayList<>());
    }

    @Override
    public boolean execute(CommandSender commandSender, String commandLabel, String[] args) {
        if (commandLabel.equalsIgnoreCase("venom")) {
            if (commandSender.isOp() || commandSender.hasPermission("venom.command")) {
                if (args.length < 1) {
                    commandSender.sendMessage(ChatColor.RED + "Venom" + ChatColor.GRAY + " - " + ChatColor.RED + Venom.getInstance().getDescription().getVersion());
                    commandSender.sendMessage(line);

                    Player player = (Player) commandSender;

                    Venom.getInstance().getCommandManager().getCommandList().forEach(command -> {
                        TextComponent textComponent = new TextComponent(ChatColor.GRAY + "» " + ChatColor.WHITE + "/" + command.getCommand() + ChatColor.GRAY + " - " + ChatColor.RED + command.getDescription());
                        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder((command.getUsage() != null ? ChatColor.RED + command.getUsage() : ChatColor.WHITE + "No usage found.")).create()));
                        player.spigot().sendMessage(textComponent);
                    });

                    commandSender.sendMessage(line);
                } else {
                    String s = args[0];
                    boolean found = false;

                    if (s.equalsIgnoreCase("info")) {
                        found = true;
                        informationCommand.execute(args, s, commandSender);
                    } else if (s.equalsIgnoreCase("alerts")) {
                        found = true;
                        alertsCommand.execute(args, s, commandSender);
                    } else if (s.equalsIgnoreCase("check")) {
                        found = true;
                        checkCommand.execute(args, s, commandSender);
                    } else if (s.equalsIgnoreCase("bans")) {
                        found = true;
                        bansCommand.execute(args, s, commandSender);
                    } else if (s.equalsIgnoreCase("impl")) {
                        found = true;
                        checksCommand.execute(args, s, commandSender);
                    }

                    if (!found) commandSender.sendMessage(ChatColor.RED + "Sub command doesn't exist!");
                }
            } else {
                commandSender.sendMessage(ChatColor.RED + "You don't have permission to execute this command.");
            }
        }
        return false;
    }
}
