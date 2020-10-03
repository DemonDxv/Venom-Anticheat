package dev.demon.venom.api.event;

public interface Cancellable {
    boolean isCancelled();

    void setCancelled(boolean var1);
}

