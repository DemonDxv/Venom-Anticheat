package dev.demon.venom.api.user.sub;

import dev.demon.venom.api.user.User;
import dev.demon.venom.api.user.User;
import dev.demon.venom.utils.time.EventTimer;
import dev.demon.venom.utils.time.TimeUtils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Entity;


import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
public class CombatData {
    private long lastPoisonDamage, lastDeath, lastRandomDamage, lastUseEntityPacket, lastEntityDamage, lastEntityDamageAttack, lastRespawn, lastFireDamage, lastBowDamage;
    private Entity lastEntityAttacked;
    public int lastBowDamageTick, lastAttackedTick, hitDelay, lastUseEntityTick, cps, lastBowStrength, noDamageTicks, cancelTicks, constantEntityTicks;
    private boolean breakingBlock, respawn;
    private User targetUser;
    private double lastVelocitySqr;
    private int transactionHits, maxSamples, movements;
    protected final List<Integer> delays = new ArrayList<>(maxSamples);
    private EventTimer shootTimer, deathTimer, respawnTimer, bowDamageTimer, entitySwitchTimer, useEntityTimer, connectionVelocitySentTimer, randomDamageTimer, attackedTimer, fireDamageTimer;


    private User user;

    public CombatData(User user) {
        this.user = user;
        respawnTimer = new EventTimer(20, user);
        shootTimer = new EventTimer(20, user);
        deathTimer = new EventTimer(20, user);
        bowDamageTimer = new EventTimer(20, user);
        entitySwitchTimer = new EventTimer(20, user);
        useEntityTimer = new EventTimer(20, user);
        connectionVelocitySentTimer = new EventTimer(20, user);
        randomDamageTimer = new EventTimer(20, user);
        attackedTimer = new EventTimer(20, user);
        fireDamageTimer = new EventTimer(20, user);
    }

    public boolean hasBowBoosted() {
        return (lastBowStrength > 0 && TimeUtils.secondsFromLong(lastBowDamage) < 3L);
    }

    public boolean didAttack(int tick) {
        return user.getConnectedTick() > tick && Math.abs(user.getConnectedTick() - this.lastUseEntityTick) < tick;
    }

    public boolean wasAttacked(int tick) {
        return user.getConnectedTick() > tick && Math.abs(user.getConnectedTick() - this.lastUseEntityTick) < tick;
    }

    public boolean isInCombo() {
        return hitDelay != 20;
    }
}
