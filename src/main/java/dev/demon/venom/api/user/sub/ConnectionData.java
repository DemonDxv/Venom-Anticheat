package dev.demon.venom.api.user.sub;

import dev.demon.venom.api.tinyprotocol.api.TinyProtocolHandler;
import dev.demon.venom.api.tinyprotocol.packet.outgoing.WrappedOutTransaction;
import dev.demon.venom.api.user.User;
import dev.demon.venom.utils.math.MathUtil;
import dev.demon.venom.utils.time.EventTimer;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class ConnectionData {
    private User user;

    private Map<Short, Long> velocityTransactions = new HashMap<>();

    private EventTimer delayedJoinTimer;

    public ConnectionData(User user) {
        this.user = user;

        this.setupTimers();
    }

    private void setupTimers() {
        delayedJoinTimer = new EventTimer(500, user);
    }

    public void sendTransaction() {
        short id = (short) (MathUtil.getRandomInteger(1, 1000));

        this.velocityTransactions.put(id, System.currentTimeMillis());

        TinyProtocolHandler.getInstance().getChannel().sendPacket(user.getPlayer(),
                new WrappedOutTransaction(0, id, false).getObject());
    }
}
