package dev.demon.venom.utils.runnables;

import dev.demon.venom.Venom;
import dev.demon.venom.api.tinyprotocol.api.TinyProtocolHandler;
import dev.demon.venom.api.tinyprotocol.packet.outgoing.WrappedOutTransaction;
import dev.demon.venom.utils.time.RunUtils;
import org.bukkit.scheduler.BukkitTask;

public class TransactionRunnable implements Runnable {

    private BukkitTask bukkitTask;

    public TransactionRunnable() {
        start();
    }


    private short transactionID = Short.MIN_VALUE;

    @Override
    public void run() {

        if (++this.transactionID >= Short.MAX_VALUE - 3) {
            this.transactionID = Short.MIN_VALUE;
        }

        WrappedOutTransaction wrappedOutTransaction = new WrappedOutTransaction(0,
                this.transactionID,
                false);

        Venom.getInstance().getUserManager().getUsers().forEach((uuid, user) -> {
            user.getLagProcessor().getTransactionMap().put(this.transactionID,
                    System.currentTimeMillis());
            user.getLagProcessor().setTransactionID(this.transactionID);

            TinyProtocolHandler.sendPacket(user.getPlayer(), wrappedOutTransaction.getObject());
        });
    }


    public void start() {
        if (bukkitTask == null) {
            bukkitTask = RunUtils.taskTimer(this, 8L, 8L);
        }
    }
}