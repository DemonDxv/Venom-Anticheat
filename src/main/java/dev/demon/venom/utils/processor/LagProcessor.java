package dev.demon.venom.utils.processor;

import dev.demon.venom.Venom;
import dev.demon.venom.api.tinyprotocol.api.Packet;
import dev.demon.venom.api.tinyprotocol.api.TinyProtocolHandler;
import dev.demon.venom.api.tinyprotocol.packet.in.WrappedInKeepAlivePacket;
import dev.demon.venom.api.tinyprotocol.packet.in.WrappedInTransactionPacket;
import dev.demon.venom.api.tinyprotocol.packet.outgoing.WrappedOutKeepAlivePacket;
import dev.demon.venom.api.tinyprotocol.packet.outgoing.WrappedOutTransaction;
import dev.demon.venom.api.user.User;
import dev.demon.venom.utils.time.EventTimer;
import dev.demon.venom.utils.time.RunUtils;
import dev.demon.venom.utils.time.TimeUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created on 05/01/2020 Package me.jumba.sparky.util.processor
 */
@Getter
@Setter
public class LagProcessor {
    private User user;

    private int kickBullshit, ignoreTransactionThreshold, maxDrop = 100, keepAlivePing,
            transactionPing, lastLagTick, lastSkippedTick,
            skippedPackets, lastLastTransaction, lastLastKeepAlive,
            lastConnectionDropTick, lastConnectionDrop,
            lastKeepAliveConnectionDrop, lastKeepaliveDrop;

    private boolean isLagging;

    private long lastFastTransaction, sentKP, kpTime, hitTime, velocityPing, lastServerKeepalive, lastFlyingPacket, lastClientKeepAlive, lastClientTransaction, keepAliveTrasactionDifference;

    private short transactionID = 500;

    private Map<Short, Long> transactionMap = new WeakHashMap<>();

    private long lastPosPing, posPing, lastPosFlyingLag, posLagPingPrediction, lastKeepAlivePosPing, keepAlivePosPing;
    private int magicLagValue, timeOutBuffer, lastKPCheckTick, totalSkippedPackets;


    private int lastTickTaken, badPacketCounter;

    private boolean expectingC03;
    private long sentKPTest;

    private EventTimer smallPacketDropTimer, connectionDropTimer;

    public void setupTimers() {
        smallPacketDropTimer = new EventTimer(600 , user);
        connectionDropTimer = new EventTimer(200, user);
    }

    public void startPingCheck() {
        Venom.getInstance().getExecutorService().scheduleAtFixedRate(() ->
                Venom.getInstance().getUserManager().userMapToList().parallelStream().forEach(users -> {

                    if ((users.getTick() - users.getLagProcessor().lastConnectionDropTick) < 100
                            && (users.getTick() - users.getLagProcessor().lastKeepaliveDrop) < 100
                            && (users.getLagProcessor().skippedPackets > 5 || (users.getTick() > 80 && (users.getTick() -
                            users.getLagProcessor().lastSkippedTick) < 80))) {
                        users.getLagProcessor().lastLagTick = users.getTick();
                    }

                    if (!users.getConnectionData().getDelayedJoinTimer().hasNotPassedWithLag()) {
                        users.getLagProcessor().isLagging = (users.getTick() > 50 && (users.getTick() - users.getLagProcessor().lastLagTick) < 50);
                    }

                    if (!users.getLagProcessor().longTermLag()) {

                    }
                }), 60L, 60L, TimeUnit.MILLISECONDS);
    }

    public void update(Object packet, String type) {
        if (user != null) {


            if (type.equalsIgnoreCase(Packet.Client.POSITION)
                    || type.equalsIgnoreCase(Packet.Client.POSITION_LOOK)
                    || type.equalsIgnoreCase(Packet.Client.LOOK)
                    || type.equalsIgnoreCase(Packet.Client.FLYING)) {

                if (expectingC03) {
                    expectingC03 = false;

                    long ping = (System.currentTimeMillis() - sentKPTest);
                    //user.debug("" + ping);
                }

                long shit = (System.currentTimeMillis() - user.lastFlyig);

                if (TimeUtils.secondsFromLong(user.getMiscData().getLastRespawnTicksSet()) > 15L) {
                    if (user.getMiscData().getRespawnLagTicks() > 0) {
                        user.getMiscData().setRespawnLagTicks(user.getMiscData().getRespawnLagTicks() - 1);
                    }
                }

                if (shit > 38L) {
                    user.realClientTick++;

                    if (user.getMiscData().getRespawnLagTicks() > 0) {
                        user.getMiscData().setRespawnLagTicks(user.getMiscData().getRespawnLagTicks() - 1);
                    }
                }

                user.lastFlyig = System.currentTimeMillis();

                if (type.equalsIgnoreCase(Packet.Client.POSITION)
                        || type.equalsIgnoreCase(Packet.Client.POSITION_LOOK)
                        || type.equalsIgnoreCase(Packet.Client.FLYING)) {

                    if (user.isBadLagJoin()) {
                        user.setTempLagLogin(user.getTick() < 500);
                    } else {
                        user.setTempLagLogin(false);
                    }

                    lastPosPing = posPing;
                    posPing = (System.currentTimeMillis() - lastPosFlyingLag);
                    long predictedLag = (Math.abs(posPing - lastPosPing));

                    posLagPingPrediction = (int) predictedLag;
                    lastPosFlyingLag = System.currentTimeMillis();

                    lastKeepAlivePosPing = keepAlivePosPing;
                    keepAlivePosPing = (System.currentTimeMillis() - lastServerKeepalive);

                    if (Math.abs(keepAlivePosPing - lastKeepAlivePosPing) < 20L) {
                        if (magicLagValue < 30) magicLagValue++;
                    } else {
                        if (magicLagValue > 0) magicLagValue--;
                    }

                    lastPosFlyingLag = System.currentTimeMillis();

                }

                long lastFlying = (System.currentTimeMillis() - this.lastFlyingPacket);

                if (lastFlying < 25L) {
                    if (this.skippedPackets < 20) this.skippedPackets += 5;

                    this.lastSkippedTick = user.getTick();
                    totalSkippedPackets++;
                } else {
                    if (totalSkippedPackets > 0) totalSkippedPackets--;

                    if (this.skippedPackets > 0) this.skippedPackets--;
                }

                //

                this.lastFlyingPacket = System.currentTimeMillis();


            }

            if (type.equalsIgnoreCase(Packet.Server.KEEP_ALIVE)) {

                WrappedOutKeepAlivePacket wrappedOutKeepAlivePacket = new WrappedOutKeepAlivePacket(packet, user.getPlayer());

                int id = (int) wrappedOutKeepAlivePacket.getTime();
                if (user.getEntityActionProcessor().getDataMap().containsKey(id)) return;

                sentKPTest = System.currentTimeMillis();
                expectingC03 = true;

                this.checkPingKick();

                this.transactionMap.put(this.transactionID, System.currentTimeMillis());

                this.lastServerKeepalive = System.currentTimeMillis();

                TinyProtocolHandler.sendPacket(user.getPlayer(), new WrappedOutTransaction(0, this.transactionID, false).getObject());

                sentKP = System.currentTimeMillis();

            }

            if (type.equalsIgnoreCase(Packet.Client.KEEP_ALIVE)) {

                WrappedInKeepAlivePacket wrappedInKeepAlivePacket = new WrappedInKeepAlivePacket(packet, user.getPlayer());

                if (!user.isJoinPingTest() && wrappedInKeepAlivePacket.getTime() == user.getVerifyID()) {
                    user.setJoinPingTest(true);

                    user.setJoinPing((System.currentTimeMillis() - user.getVerifyTime()));

                    if (user.getJoinPing() > 500L) {
                        // user.debug("delayed check startup");
                        user.getConnectionData().getDelayedJoinTimer().reset();
                        user.getLagProcessor().setLagging(true);
                        user.setBadLagJoin(true);
                        user.setTempLagLogin(true);
                    }
                }

                this.kpTime = (System.currentTimeMillis() - this.sentKP);

                this.lastLastKeepAlive = this.keepAlivePing;
                this.keepAlivePing = (int) (System.currentTimeMillis() - this.lastServerKeepalive);

                int drop = Math.abs(this.keepAlivePing - this.lastLastKeepAlive);

                if (drop > this.maxDrop) {
                    this.lastKeepaliveDrop = user.getTick();
                }

                this.lastKeepAliveConnectionDrop = drop;

                this.keepAliveTrasactionDifference = (this.lastClientKeepAlive - this.lastClientTransaction);

              /* if (this.keepAliveTrasactionDifference > 500 && !this.isLagging && (user.getConnectedTick() - this.lastLagTick) > 100) {
                     if (this.ignoreTransactionThreshold++ > 5) {
                         RunUtils.task(() -> user.getPlayer().kickPlayer("Timed out."));
                     }
                } else {
                    this.ignoreTransactionThreshold = 0;
                }*/

                this.lastClientKeepAlive = System.currentTimeMillis();

            }

            if (type.equalsIgnoreCase(Packet.Client.TRANSACTION)) {


                WrappedInTransactionPacket wrappedInTransactionPacket = new WrappedInTransactionPacket(packet, user.getPlayer());

                short ID = wrappedInTransactionPacket.getAction();

                if (this.transactionMap.containsKey(ID)) {

                    this.lastLastTransaction = this.transactionPing;

                    long difference = (System.currentTimeMillis() - this.lastServerKeepalive);

                    this.transactionPing = (int) difference;

                    int connectionDrop = this.lastConnectionDrop = Math.abs(this.transactionPing - this.lastLastTransaction);

                    if (connectionDrop > 35) {
                        this.smallPacketDropTimer.reset();
                    }

                    if (connectionDrop > this.maxDrop) {
                        this.lastConnectionDropTick = user.getTick();
                        this.connectionDropTimer.reset();
                    }

                    this.transactionMap.remove(ID);

                    if (this.transactionID > 0) {
                        this.transactionID--;
                    } else {
                        this.transactionID = 500;
                    }

                    this.lastClientTransaction = System.currentTimeMillis();
                }

            }
        }
    }

    private void checkPingKick() {

        if (user.getTick() > 150) {

            int maxPing = 750;

            if (user.getVelocityData().getVelocityTicks() < 20) {
                kickBullshit = Math.min(kickBullshit, 1);
            }


        }
    }

    public boolean longTermLag() {
        return isLagging() || (user.getTick() - getLastLagTick()) < 100;
    }
}
