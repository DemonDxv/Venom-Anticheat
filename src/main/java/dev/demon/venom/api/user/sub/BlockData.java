package dev.demon.venom.api.user.sub;

import dev.demon.venom.api.user.User;
import dev.demon.venom.api.user.User;
import dev.demon.venom.utils.time.EventTimer;


public class BlockData {
    public double carpetTick, redstoneTick, pistionTick, cactusTicks, enchantmentTableTicks, ironBarTicks, lastHalfBlockTick, lastSlimeTick, lastChestTick, presurePlateTicks, lastPresurePlateTick, lastStairTicks, lastSlabTick, lastBlockAboveTick, lastIceTick, enderFrameTick, bedTicks, leaveTicks, chestTicks, trapDoorTicks, hopperTicks, lillyPadTicks, solidLiquidTicks, anvilTicks, wallTicks, doorTicks, glassPaneTicks, halfBlockTicks, soulSandTicks, snowTicks, webTicks, liquidTicks, blockAboveTicks, iceTicks, stairTicks, slabTicks, fenceTicks, railTicks, slimeTicks, climbableTicks;
    public long lastAnyBlockWithLiquid, lastBlockAbove, lastSoulSand, lastSline, lastIce;
    public boolean ice, climable, slime, isGroundWater, inBlock;
    public EventTimer lastInsideBlockTimer, lastClimbableTimer;

    public BlockData(User user) {
        lastIceTick = lastBlockAboveTick = lastSlabTick = lastPresurePlateTick = lastChestTick = lastSlimeTick = lastHalfBlockTick = user.getConnectedTick();
        lastInsideBlockTimer = new EventTimer(20, user);
        lastClimbableTimer = new EventTimer(50, user);
    }
}
