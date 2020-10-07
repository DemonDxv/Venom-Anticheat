package dev.demon.venom;

import dev.demon.venom.api.event.EventManager;
import dev.demon.venom.api.tinyprotocol.api.TinyProtocolHandler;
import dev.demon.venom.api.tinyprotocol.api.packets.reflections.Reflections;
import dev.demon.venom.api.tinyprotocol.api.packets.reflections.types.WrappedField;
import dev.demon.venom.api.user.User;
import dev.demon.venom.api.user.UserManager;
import dev.demon.venom.impl.command.CommandManager;
import dev.demon.venom.impl.events.ServerShutdownEvent;
import dev.demon.venom.impl.listeners.BukkitListeners;
import dev.demon.venom.impl.listeners.ListenerManager;
import dev.demon.venom.utils.block.BlockUtil;
import dev.demon.venom.utils.box.BlockBoxManager;
import dev.demon.venom.utils.box.impl.BoundingBoxes;
import dev.demon.venom.utils.command.CommandUtils;
import dev.demon.venom.utils.connection.HTTPUtil;
import dev.demon.venom.utils.math.MathUtil;
import dev.demon.venom.utils.processor.EntityProcessor;
import dev.demon.venom.utils.reflection.CraftReflection;
import dev.demon.venom.utils.time.RunUtils;
import dev.demon.venom.utils.version.VersionUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;

@Getter
@Setter
public class Venom extends JavaPlugin {

    @Getter
    private static Venom instance;

    private TinyProtocolHandler tinyProtocolHandler;
    private UserManager userManager;
    private ScheduledExecutorService executorService;
    private BlockBoxManager blockBoxManager;
    private BoundingBoxes boxes;
    private String bukkitVersion;
    private VersionUtil versionUtil;
    private BukkitListeners bukkitListener;
    private EventManager eventManager;
    private CommandManager commandManager;

    private int currentTicks, lagStartCheck;
    private long lastServerTick, lastServerLag, lastServerStart;
    private boolean isLagging;

    private EntityProcessor entityProcessor = new EntityProcessor();
    private Map<UUID, List<Entity>> entities = new ConcurrentHashMap<>();
    private WrappedField entityList = Reflections.getNMSClass("World").getFieldByName("entityList");

    public static int banVL;

    public static String banMessage, banCommand, alertsMessage, alertsDev, permissionAlert, permissionPING,
            permissionCMD, permissionINFO, key;

    public static Boolean banEnabled, alertsEnabled, banMessageEnabled, enableDebug;

    private File cfile;

    @Override
    public void onEnable() {
        instance = this;

        cfile = new File(getDataFolder(), "config.yml");
        saveDefaultConfig();
        loadConfiguration();

        bukkitVersion = Bukkit.getServer().getClass().getPackage().getName().substring(23);

        Venom.getInstance().getEntityProcessor().start();

        tinyProtocolHandler = new TinyProtocolHandler();

        executorService = Executors.newSingleThreadScheduledExecutor();

        userManager = new UserManager();

        versionUtil = new VersionUtil();

        eventManager = new EventManager();

        commandManager = new CommandManager();

        Bukkit.getOnlinePlayers().forEach(player -> TinyProtocolHandler.getInstance().addChannel(player));


        RunUtils.taskTimer(() -> {
            for (World world : Bukkit.getWorlds()) {
                Object vWorld = CraftReflection.getVanillaWorld(world);

                List<Object> vEntities = Collections.synchronizedList(Venom.getInstance().getEntityList().get(vWorld));

                List<Entity> bukkitEntities = vEntities.parallelStream().map(CraftReflection::getBukkitEntity).collect(Collectors.toList());

                Venom.getInstance().getEntities().put(world.getUID(), bukkitEntities);
            }
        }, 2L, 2L);



        new BlockUtil();
        new ListenerManager();
        new MathUtil();

        Venom.getInstance().getServer().getPluginManager().registerEvents(new BukkitListeners(), Venom.getInstance());


        this.blockBoxManager = new BlockBoxManager();
        this.boxes = new BoundingBoxes();

        getLogger().info("Venom Anticheat has been Successfully Loaded.");

        if (!User.keyActive) {
            getLogger().info("Venom is missing a license or you are using a cracked version. Shutting down!");
            getEventManager().callEvent(new ServerShutdownEvent());
            Bukkit.getOnlinePlayers().forEach(player -> TinyProtocolHandler.getInstance().removeChannel(player));
            executorService.shutdownNow();
            commandManager.getCommandList().forEach(CommandUtils::unRegisterBukkitCommand);
        }

        super.onEnable();
    }

    @Override
    public void onDisable() {
        getEventManager().callEvent(new ServerShutdownEvent());
        Bukkit.getOnlinePlayers().forEach(player -> TinyProtocolHandler.getInstance().removeChannel(player));
        executorService.shutdownNow();
        commandManager.getCommandList().forEach(CommandUtils::unRegisterBukkitCommand);
    }


    private void loadConfiguration() {
        //Bans
        banMessage = instance.getConfig().getString("Bans.message");
        banCommand = instance.getConfig().getString("Bans.command");
        banEnabled = instance.getConfig().getBoolean("Bans.enabled");
        banVL = instance.getConfig().getInt("Bans.max-vl");
        banMessageEnabled = instance.getConfig().getBoolean("Bans.message-enabled");

        //Alerts
        alertsMessage = instance.getConfig().getString("Alerts.message");
        enableDebug = instance.getConfig().getBoolean("Alerts.debug");


        //Permission
        permissionAlert = instance.getConfig().getString("Permissions.alert");
        permissionPING = instance.getConfig().getString("Permissions.ping");
        permissionCMD = instance.getConfig().getString("Permissions.command");
        permissionINFO = instance.getConfig().getString("Permissions.info");


        key = instance.getConfig().getString("License.key");

    }
}
