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
        addCommand(new Command(new MainCommand("venom"), "venom info", "/Venom info <player>", "Information command.", true));
        addCommand(new Command(new MainCommand("venom"), "venom alerts", "/Venom alerts", "Alerts command.", true));
        addCommand(new Command(new MainCommand("venom"), "venom check", "/Venom check <player>", "[New]", true));
    }

    private void addCommand(Command... commands) {
        for (Command command : commands) {
            commandList.add(command);
            if (command.isEnabled()) CommandUtils.registerCommand(command);
        }
    }
}

