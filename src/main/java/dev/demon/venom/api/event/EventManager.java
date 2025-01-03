package dev.demon.venom.api.event;

import dev.demon.venom.Venom;
import dev.demon.venom.api.event.exceptions.ListenParamaterException;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.PacketEvent;
import dev.demon.venom.impl.listeners.PacketListener;
import lombok.Getter;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Getter
public class EventManager {
    private final List<ListenerMethod> listenerMethods = new CopyOnWriteArrayList<>();
    public boolean paused = false;
    private List<Listener> listenersList = Collections.synchronizedList(new ArrayList<>());

    public void setup() {
        this.listenersList.add(new PacketListener());
    }

    public void callFag(PacketEvent packetEvent, User user) {
        user.getExecutorService().execute(() ->
                this.listenersList.forEach(listener ->
                        listener.onPacket(packetEvent)));
    }

    public void registerFag(Listener... listener) {
        this.listenersList.addAll(Arrays.asList(listener));
    }

    public void registerListener(Method method, AnticheatListener listener, Plugin plugin) throws ListenParamaterException {
        if(method.getParameterTypes().length == 1) {
            if(method.getParameterTypes()[0].getSuperclass().equals(AnticheatEvent.class)) {
                Listen listen = method.getAnnotation(Listen.class);
                ListenerMethod lm = new ListenerMethod(plugin, method, listener, listen.priority());

                if(!listen.priority().equals(ListenerPriority.NONE)) {
                    lm.listenerPriority = listen.priority();
                }

                listenerMethods.add(lm);
                listenerMethods.sort(Comparator.comparing(mth -> mth.listenerPriority.getPriority(), Comparator.reverseOrder()));
            } else {
                throw new ListenParamaterException("Method " + method.getDeclaringClass().getName() + "#" + method.getName() + "'s paramater: " + method.getParameterTypes()[0].getName() + " is not an instanceof " + AnticheatEvent.class.getSimpleName() + "!");
            }
        } else {
            throw new ListenParamaterException("Method " + method.getDeclaringClass().getName() + "#" + method.getName() + " has an invalid amount of paramters (count=" + method.getParameterTypes().length + ")!");
        }
    }

    public void clearAllRegistered() {
        listenerMethods.clear();
    }

    public void unregisterAll(Plugin plugin) {
        listenerMethods.stream()
                .filter(lm -> lm.plugin.equals(plugin))
                .forEach(listenerMethods::remove);
    }

    public void unregisterListener(AnticheatListener listener) {
        listenerMethods.stream().filter(lm -> lm.listener.equals(listener)).forEach(listenerMethods::remove);
    }

    public void registerListeners(AnticheatListener listener, Plugin plugin) {
        Arrays.stream(listener.getClass().getMethods()).filter(method -> method.isAnnotationPresent(Listen.class)).forEach(method -> {
            try {
                registerListener(method, listener, plugin);
            } catch(ListenParamaterException e) {
                e.printStackTrace();
            }
        });
    }

    public void callEvent(AnticheatEvent event) {
        if(!paused && event != null) {
       //     Atlas.getInstance().getProfile().start("event:" + event.getClass().getSimpleName());

            List<ListenerMethod> methods = listenerMethods.parallelStream()
                    .filter(lm -> lm.method.getParameters().get(0).equals(event.getClass()))
                    .sequential()
                    .sorted(Comparator.comparing(lm -> lm.listenerPriority.getPriority(), Comparator.reverseOrder()))
                    .collect(Collectors.toList());


            if(event instanceof Cancellable) {
                Cancellable cancellable = (Cancellable) event;
                for (ListenerMethod lm : methods) {
                    if(!cancellable.isCancelled() || !lm.ignoreCancelled) {
                        lm.method.invoke(lm.listener, cancellable);
                    }
                }
            }
           // Atlas.getInstance().getProfile().stop("event:" + event.getClass().getSimpleName());
        }
    }

    public void callEventAsync(AnticheatEvent event) {
        Venom.getInstance().getExecutorService().execute(() -> callEvent(event));
    }
}