package dev.demon.venom.impl.command.commands.sub;

import dev.demon.venom.Venom;
import dev.demon.venom.api.checknew.CheckManager;
import dev.demon.venom.api.checknew.Check;
import dev.demon.venom.api.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ChecksCommand {

    private List<String> disabledChecks = new ArrayList<>();

    public void execute(String[] args, String s, CommandSender commandSender) {
        if (args.length > 2) {
            User user = Venom.getInstance().getUserManager().getUser(((Player) commandSender).getUniqueId());
            if (user != null) {

                Check foundCHeck = user.getChecks().parallelStream().filter(check ->
                        check.checkname.equalsIgnoreCase(args[1])
                                && check.checktype.equalsIgnoreCase(args[2])).findAny().orElse(null);

                if (foundCHeck != null) {

                    String combined = args[1] + args[2];

                    String combined2 = args[1] + " " + args[2];

                    if (!disabledChecks.contains(combined)) {
                        disabledChecks.add(combined);
                        commandSender.sendMessage(  "Disabled check: " + ChatColor.RED + combined2);
                        this.update(new Data(args[1], args[2], false));
                    } else {
                        disabledChecks.remove(combined);
                        commandSender.sendMessage("Enabled check: " + ChatColor.GREEN + combined2);
                        this.update(new Data(args[1], args[2], true));
                    }
                } else {
                    commandSender.sendMessage(ChatColor.RED+"[ERROR] No check found.");
                }
            }
        }
    }


    private void update(Data data) {
        Venom.getInstance().getUserManager().userMapToList().parallelStream().forEach(user ->
                CheckManager.updateCheckFreezeState(new CheckManager.CheckData(data.name, data.type,
                data.bool, user)));
    }

    @Getter
    @AllArgsConstructor
    public static class Data {
        private String name, type;
        private boolean bool;
    }
}
