package dev.demon.venom.api.event;

import dev.demon.venom.api.tinyprotocol.api.packets.reflections.types.WrappedClass;
import dev.demon.venom.api.tinyprotocol.api.packets.reflections.types.WrappedMethod;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;

class ListenerMethod {
    public Plugin plugin;
    public WrappedMethod method;
    public WrappedClass listenerClass;
    public AnticheatListener listener;
    public ListenerPriority listenerPriority;
    public String className;
    public boolean ignoreCancelled;

    public ListenerMethod(Plugin plugin, Method method, AnticheatListener listener, ListenerPriority listenerPriority) {
        this.plugin = plugin;
        this.listenerClass = new WrappedClass(listener.getClass());
        this.method = new WrappedMethod(listenerClass, method);
        this.listener = listener;
        this.listenerPriority = listenerPriority;
        this.ignoreCancelled = method.getAnnotation(Listen.class).ignoreCancelled();
        this.className = method.getParameterTypes()[0].getName();
    }
}