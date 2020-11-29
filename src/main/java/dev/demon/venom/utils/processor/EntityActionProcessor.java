package dev.demon.venom.utils.processor;

import dev.demon.venom.api.tinyprotocol.api.Packet;
import dev.demon.venom.api.tinyprotocol.api.TinyProtocolHandler;
import dev.demon.venom.api.tinyprotocol.packet.in.WrappedInKeepAlivePacket;
import dev.demon.venom.api.tinyprotocol.packet.outgoing.WrappedOutKeepAlivePacket;
import dev.demon.venom.api.user.User;
import dev.demon.venom.utils.math.MathUtil;
import lombok.Getter;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Created on 05/09/2020 Package me.jumba.sparky.util.processor
 */
public class EntityActionProcessor {

    @Getter
    private Map<Integer, Data> dataMap = new WeakHashMap<>();

    private Map<ActionType, Long> timeMap = new WeakHashMap<>();

    private User user;

    public EntityActionProcessor(User user) {
        this.user = user;
    }

    private boolean didTakeFallDamage = false;
    private boolean wasAttacked = false;
    private boolean skipActionsTest = true;

    private boolean checkForUnfixedVars;

    public void update(Object packet, String type) {

        //Fixes velocity being fucked randomly
        if (checkForUnfixedVars && (type.equalsIgnoreCase(Packet.Client.FLYING)
                || type.equalsIgnoreCase(Packet.Client.POSITION)
                || type.equalsIgnoreCase(Packet.Client.POSITION_LOOK)
                || type.equalsIgnoreCase(Packet.Client.LOOK))) {
            if (user.getMovementData().getLastServerVelocity().hasNotPassed(6)
                    && (didTakeFallDamage
                    || wasAttacked)) {
                didTakeFallDamage = false;
                wasAttacked = false;
                checkForUnfixedVars = false;
            }
        }

        if (type.equalsIgnoreCase(Packet.Client.KEEP_ALIVE)) {
            WrappedInKeepAlivePacket wrappedInKeepAlivePacket = new WrappedInKeepAlivePacket(packet, user.getPlayer());

            int id = (int) wrappedInKeepAlivePacket.getTime();

            if (this.dataMap.containsKey(id)) {
                Data data = this.dataMap.get(id);

                this.dataMap.remove(id);

                //Fixes falses with laggy players and packets being sent back faster
                if (data != null) {
                    switch (data.getActionType()) {

                        case RESPAWN: {
                            user.getCombatData().getRespawnTimer().reset();
                            break;
                        }

                        case FALL_DAMAGE: {
                            didTakeFallDamage = true;
                            data.getUser().getMovementData().getFallDamageTimer().reset();
                            break;
                        }

                        case ATTACKED: {
                            wasAttacked = true;
                            data.getUser().getCombatData().getAttackedTimer().reset();
                            break;
                        }

                        case BOW: {
                            data.getUser().getCombatData().getBowDamageTimer().reset();
                            break;
                        }

                        case FIRE: {
                            user.getCombatData().getFireDamageTimer().reset();
                            break;
                        }

                        case VELOCITY: {

                            //Doing this so the data be be synced to exactly the clients
                            if (!skipActionsTest) {
                                if (!didTakeFallDamage
                                        // && !wasAttacked
                                        && data.getUser().getCombatData().getAttackedTimer().passed(2)) {
                                    user.getMovementData().getLastServerVelocity().reset();
                                }
                            } else {
                                user.getMovementData().getLastServerVelocity().reset();
                                skipActionsTest = false;
                            }

                            didTakeFallDamage = false;
                            wasAttacked = false;
                            checkForUnfixedVars = true;
                            break;
                        }
                    }
                }
            }
        }
    }


    public enum ActionType {
        FALL_DAMAGE,
        ENTITY_ATTACK,
        ATTACKED,
        BOW,
        FIRE,
        VELOCITY,
        RESPAWN
    }

    @Getter
    public static class Data {

        private User user;
        private ActionType actionType;
        private int tick;
        private long timestamp;

        public Data(User user, ActionType actionType) {
            this.user = user;
            this.actionType = actionType;
            this.tick = user.getTick();
            this.timestamp = System.currentTimeMillis();
        }
    }

    public void addAction(ActionType actionType) {
        int time = MathUtil.getRandomInteger(100, 1000) + user.getTick();
        this.timeMap.put(actionType, System.currentTimeMillis());

        this.dataMap.put(time, new Data(user, actionType));
        TinyProtocolHandler.sendPacket(user.getPlayer(), new WrappedOutKeepAlivePacket(time).getObject());
    }

    public void addAction(ActionType actionType, boolean bukkitEvent) {

        boolean send = true;

        if (bukkitEvent) {
            if (this.timeMap.containsKey(actionType)) {
                if ((System.currentTimeMillis() - this.timeMap.get(actionType)) < 60L) {
                    send = false;
                }
            }

            this.timeMap.put(actionType, System.currentTimeMillis());
        }

        if (send) {
            int time = MathUtil.getRandomInteger(100, 1000) + user.getTick();
            this.dataMap.put(time, new Data(user, actionType));
            TinyProtocolHandler.sendPacket(user.getPlayer(), new WrappedOutKeepAlivePacket(time).getObject());
        }
    }


    /*private boolean lagFallDamage;
    private boolean lagSkipCheck = true;

    private void runLagCheck(ActionType actionType) {

        //Fixes if the player is lagging and they have velocity set but the keepalive didn't come back in time

        if (user.getLagProcessor().isLagging()) {

            if (actionType == ActionType.FALL_DAMAGE) {
                lagFallDamage = true;
           //     user.getMovementData().getFallDamageTimer().reset();
            }

            if (actionType == ActionType.VELOCITY) {
                if (!lagFallDamage || lagSkipCheck) {
                  //  user.getMovementData().getLastServerVelocity().reset();
                //    user.debug("set velocity lagging");
                }

                lagSkipCheck = false;
                lagFallDamage = false;
            }
        }
    }*/
}
