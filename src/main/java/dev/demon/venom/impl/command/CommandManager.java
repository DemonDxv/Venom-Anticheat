package dev.demon.venom.impl.command;


import dev.demon.venom.impl.command.commands.MainCommand;
import dev.demon.venom.utils.command.CommandUtils;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class CommandManager {

    private List<Command> commandList = new ArrayList<>();

    public CommandManager() {
        addCommand(new Command(new MainCommand("venom"), "venom", null, "Main command.", true));
        addCommand(new Command(new MainCommand("venom"), "venom info", "/Venom info <player>", "Shows information about a player.", true));
        addCommand(new Command(new MainCommand("venom"), "venom alerts", "/Venom alerts", "Toggle on, and off alerts.", true));
        addCommand(new Command(new MainCommand("venom"), "venom check", "/Venom check <player>", "Shows development info about selected player.", true));
        addCommand(new Command(new MainCommand("venom"), "venom impl", "/Venom impl <check> <type>", "Toggle on, and off impl.", true));
        addCommand(new Command(new MainCommand("venom"), "venom bans", "/Venom bans", "Toggle on, and off bans.", true));
    }

    private void addCommand(Command... commands) {
        for (Command command : commands) {
            commandList.add(command);
            if (command.isEnabled()) CommandUtils.registerCommand(command);
        }
    }
}

