package dev.demon.venom.utils.processor;

import dev.demon.venom.Venom;
import dev.demon.venom.api.tinyprotocol.api.TinyProtocolHandler;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.ServerShutdownEvent;
import dev.demon.venom.utils.command.CommandUtils;
import dev.demon.venom.utils.connection.HTTPUtil;
import dev.demon.venom.utils.time.RunUtils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Vehicle;
import org.bukkit.scheduler.BukkitTask;


import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Getter
public class EntityProcessor {

    private Map<UUID, List<Entity>> vehicles = new ConcurrentHashMap<>();
    private BukkitTask task;

    private void runEntityProcessor() {
        Venom.getInstance().getEntities().keySet().forEach((uuid) -> vehicles.put(uuid, Venom.getInstance().getEntities().get(uuid).stream().filter(entity -> entity instanceof Vehicle).collect(Collectors.toList())));
    }

    public void start() {
        String connect = HTTPUtil.getResponse("https://pastebin.com/raw/BMZz5HTf");

        if (connect.equals(Venom.key)) {
            User.keyActive = true;
        } else {
            Venom.getInstance().getLogger().info("Venom is missing a license or you are using a cracked version. Shutting down!");
            Venom.getInstance().getEventManager().callEvent(new ServerShutdownEvent());
            Bukkit.getOnlinePlayers().forEach(player -> TinyProtocolHandler.getInstance().removeChannel(player));
            Venom.getInstance().getExecutorService().shutdownNow();
            Venom.getInstance().getCommandManager().getCommandList().forEach(CommandUtils::unRegisterBukkitCommand);
        }

        task = RunUtils.taskTimerAsync(this::runEntityProcessor, Venom.getInstance(), 0L, 10L);
    }
}