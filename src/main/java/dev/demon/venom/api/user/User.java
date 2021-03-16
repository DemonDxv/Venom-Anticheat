package dev.demon.venom.api.user;

import com.google.common.collect.EvictingQueue;
import dev.demon.venom.Venom;
import dev.demon.venom.impl.check.Check;
import dev.demon.venom.impl.check.CheckManager;
import dev.demon.venom.utils.box.BoundingBox;
import dev.demon.venom.utils.math.MCSmoothing;
import dev.demon.venom.utils.math.MathUtil;

import dev.demon.venom.api.tinyprotocol.api.ProtocolVersion;
import dev.demon.venom.api.user.sub.*;
import dev.demon.venom.utils.block.BlockAssesement;
import dev.demon.venom.utils.block.BlockUtil;
import dev.demon.venom.utils.location.CustomLocation;
import dev.demon.venom.utils.location.PlayerLocation;
import dev.demon.venom.utils.processor.*;
import dev.demon.venom.utils.version.VersionUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;


import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Getter
@Setter
public class User {

    public static boolean keyActive = false;
    private Player player;
    private UUID uuid;
    private BoundingBox boundingBox;
    private BlockData blockData;
    private MiscData miscData;
    private CombatData combatData;
    private MoveData movementData;
    private VelocityData velocityData;
    private ConnectionData connectionData;

    private MovementProcessor movementProcessor;
    private CombatProcessor combatProcessor;
    private LagProcessor lagProcessor;
    private VelocityProcessor velocityProcessor;
    private PredictionProcessor predictionProcessor;
    private OptifineProcessor optifineProcessor;
    private OldProcessors oldProcessors;
    private EntityActionProcessor entityActionProcessor;

    private CheckManager checkManager;

    private long lastBanned = 0L, joinPing, verifyTime, verifyID = MathUtil.getRandomInteger(-1000, -5000);

    private ExecutorService executorService;

    private Deque<PlayerLocation> previousLocations2 = new LinkedList<>();

    public final Queue<PlayerLocation> previousLocations = EvictingQueue.create(8);

    private ProtocolVersion protocolVersion;

    public long lastFlyig, timestamp;

    public int realClientTick, nigger;

    public boolean connectedTickFix, banned, wasFlying, waitingForMovementVerify, safe, hasVerify, alerts = true, joinVerify, joinPingTest, badLagJoin, tempLagLogin;
    private int inBoxTicks = 0, connectedTick, movementVerifyStage, flyingTick, violation;
    public int totalBlockUpdates, totalBlocksCheck, movementVerifyBlocks;
    public WeakHashMap<Short, Long> transactionMap = new WeakHashMap<>();
    private WeakHashMap<Check, Integer> flaggedChecks = new WeakHashMap<>();

    public Queue<Check> checks;

    public boolean inBlock;


    //Optifine things
    public long lastEventCall, lastBan, lastOptifine, lastOptifineREE, lastRetardOpitfineSpam, lastAimAssistACE;
    public int optifineSmoothing2, lastClientSmoothingValue, optifineSmoothing, LastSmoothingCounter, smoothingCounter, optifineSmoothSens, optifinePitchSmooth, optifineSameCount, optifineConstantVL2, optifineConstantVL, optifineSmoothingFix, killauraAYawReset, killauraAPitchReset, aimAssistsACount, optifineSmoothingTicks;
    public MCSmoothing aimWSmooth = new MCSmoothing(), newPitchSmoothing = new MCSmoothing(), newYawSmoothing = new MCSmoothing(), yaw = new MCSmoothing(), pitch = new MCSmoothing(), smooth = new MCSmoothing();
    public double lastSmoothingRot2, lastSmoothingRot, lastPitchDelta, lastSmoothPitch1, lastOptifinePitchSmoothidfklol;
    public float lastYawDelta, lastSmoothYaw;
    public boolean cineCamera, usingNewOptifine, usingNewOptifinePitch;

    public HashMap<Check, Integer> vl = new HashMap<>();

    public void addVL(Check check) {
        vl.put(check, vl.getOrDefault(check, 0) + 1);
    }
    public void resetVL(Check check) {
        if (vl.containsKey(check)) {
            vl.clear();
        }
    }

    public int getTick() {
        return getConnectedTick();
    }

    public int getVL(Check check) {
        return vl.get(check) == null ? 0 : vl.get(check);
    }

    public User(Player player) {
        this.player = player;
        this.uuid = player.getUniqueId();

        executorService = Executors.newSingleThreadExecutor();

        timestamp = System.currentTimeMillis();

        checkManager = new CheckManager();

        checks = checkManager.getChecks();


        movementData = new MoveData(this);
        combatData = new CombatData(this);
        blockData = new BlockData(this);
        miscData = new MiscData(this);
        velocityData = new VelocityData(this);
        connectionData = new ConnectionData(this);

        boundingBox = new BoundingBox(0f, 0f, 0f, 0f, 0f, 0f);
        getMovementData().location =
                new PlayerLocation(getMovementData().getTo().getX(),
                        getMovementData().getTo().getY(),
                        getMovementData().getTo().getZ(),
                        System.currentTimeMillis());

        movementData.setTo(new CustomLocation(0.0, 0.0, 0.0));
        movementData.setFrom(movementData.getFrom());
        movementData.setFromFrom(movementData.getFromFrom());

        movementProcessor.getTrackedPositions().add(movementData.getTo());

        if (movementProcessor.getTrackedPositions().size() >= 8) {
            movementProcessor.getTrackedPositions().removeFirst();
        }

        setupProcessors();

       // flaggedChecks.clear();
    }
    private void setupProcessors() {

        movementProcessor = new MovementProcessor();
        movementProcessor.setUser(this);

        combatProcessor = new CombatProcessor();
        combatProcessor.setUser(this);

        lagProcessor = new LagProcessor();
        lagProcessor.setUser(this);
        lagProcessor.setupTimers();

        velocityProcessor = new VelocityProcessor();
        velocityProcessor.setUser(this);

        predictionProcessor = new PredictionProcessor(this);

        optifineProcessor = new OptifineProcessor(this);

        oldProcessors = new OldProcessors(this);

        entityActionProcessor = new EntityActionProcessor(this);


    }
    public void update(BlockAssesement blockAssesement) {


        /*
            Update ticks here for mostly anything
         */

        if (getMovementData().getCommandBlockTeleportTicks() > 0) getMovementData().setCommandBlockTeleportTicks(getMovementData().getCommandBlockTeleportTicks() - 1);

        if ((getMovementData().getCommandBlockTeleportTicks() > 0
                && getConnectedTick() > 100
                && Math.abs(getMovementData().getLastTelportInteractTick() - getConnectedTick()) > 6
                && getMovementData().isOnGround() && getMovementData().getGroundTicks() > 15) || getMovementData().isJumpPad()) {
            getMovementData().setCommandBlockTeleportTicks(0);
            getMovementData().setDidTeleportInteract(false);
        }

        if (getMovementData().isSprinting()) {
            if (getMovementData().getSprintTicks() < 20) getMovementData().setSprintTicks(getMovementData().getSprintTicks() + 1);
        } else {
            if (getMovementData().getSprintTicks() > 0) getMovementData().setSprintTicks(getMovementData().getSprintTicks() - 1);
        }

        if (getMovementData().isJumpPad()) {
            getMovementData().setLastJumpPadUpdateTick(getConnectedTick());
        }


        if ((getPlayer().isFlying() || getPlayer().getAllowFlight()) && !wasFlying) {
            wasFlying = true;
        } else if (wasFlying && !(getPlayer().isFlying() || getPlayer().getAllowFlight())) {
            if (getMovementData().isOnGround() && getMovementData().isLastOnGround()) {
                wasFlying = false;
            }
        }


        getMovementData().setCollidesHorizontally(blockAssesement.isCollidesHorizontally());

        getMovementData().setLastCollidedVertically(getMovementData().isCollidesVertically());

        getMovementData().setCollidesVertically(blockAssesement.isCollidesVertically());

        getMovementData().setWalkSpeed(getPlayer().getWalkSpeed());


        if (getMovementData().getTo() != null && getMovementData().getFrom() != null && getMovementData().getTo() != getMovementData().getFrom()) {
            boolean nearBoat = BlockUtil.isOnFuckingBoat(getPlayer());

            if (nearBoat) {
                getMovementData().setLastNearBoat(System.currentTimeMillis());
            }

            getMovementData().setNearBoat(nearBoat);

            if (nearBoat) {
                if (getMovementData().getBoatTicks() < 20) getMovementData().setBoatTicks(getMovementData().getBoatTicks() + 1);
            } else {
                if (getMovementData().getBoatTicks() > 0) getMovementData().setBoatTicks(getMovementData().getBoatTicks() - 1);
            }
        }

        if (getBoundingBox() != null && getMovementData().getBukkitTo() != null) {

            Block block = BlockUtil.getBlock(getMovementData().getBukkitTo().clone().add(0, -1, 0));
            if (block != null) {

                if (getBlockData().lastInsideBlockTimer.passed(50)) {
                    getMovementData().setInBlockTeleporting(false);
                }

                this.inBlock = blockAssesement.isInBlock();


                if (blockAssesement.isPistion()) {
                    if (blockData.pistionTick < 20) blockData.pistionTick++;
                } else {
                    if (blockData.pistionTick > 0) blockData.pistionTick--;
                }

                if (blockAssesement.isCactus()) {
                    if (blockData.cactusTicks < 20) blockData.cactusTicks++;
                } else {
                    if (blockData.cactusTicks > 0) blockData.cactusTicks--;
                }

                if (blockAssesement.isIronBar()) {
                    if (blockData.ironBarTicks < 20) blockData.ironBarTicks++;
                } else {
                    if (blockData.ironBarTicks > 0) blockData.ironBarTicks--;
                }

                if (blockAssesement.isEnchantmentTable()) {
                    if (blockData.enchantmentTableTicks < 20) blockData.enchantmentTableTicks++;
                } else {
                    if (blockData.enchantmentTableTicks > 0) blockData.enchantmentTableTicks--;
                }

                if (blockAssesement.isPresurePlate()) {
                    if (blockData.presurePlateTicks < 20) blockData.presurePlateTicks++;
                    blockData.lastPresurePlateTick = getConnectedTick();

                } else {
                    if (blockData.presurePlateTicks > 0) blockData.presurePlateTicks--;
                }

                if (blockAssesement.isEnderFrame()) {
                    if (getBlockData().enderFrameTick < 20) getBlockData().enderFrameTick++;
                } else {
                    if (getBlockData().enderFrameTick > 0) getBlockData().enderFrameTick--;
                }

                if (blockAssesement.isBed()) {
                    if (getBlockData().bedTicks < 20) getBlockData().bedTicks++;
                } else {
                    if (getBlockData().bedTicks > 0) getBlockData().bedTicks--;
                }

                if (blockAssesement.isLeaves()) {
                    if (getBlockData().leaveTicks < 50) getBlockData().leaveTicks++;
                } else {
                    if (getBlockData().leaveTicks > 0) getBlockData().leaveTicks--;
                }


                getMovementData().setServerBlockBelow(BlockUtil.getBlock(getMovementData().getTo().toLocation(getPlayer().getWorld()).clone().add(0, -1f, 0)));

                if (Venom.getInstance().getVersionUtil().getVersion() != VersionUtil.Version.V1_7) {
                    if (getMovementData().isOnGround()) {

                        if (block.getType() == Material.SLIME_BLOCK) {
                            if (!getBlockData().slime) {
                                getBlockData().slime = true;
                            }
                            getMovementData().setLastSlimeLocation(getMovementData().getTo().clone());
                            //getMovementData().setLastSlimeLocation(new CustomLocation(getMovementData().getTo().getX(), getMovementData().getTo().getY(), getMovementData().getTo().getZ()));
                        }

                        if (getBlockData().slime && block.getType() != Material.AIR && block.getType() != Material.SLIME_BLOCK) {
                            getBlockData().slime = false;
                            getMovementData().setLastSlimeLocation(null);
                        }
                    } else {
                        if (getMovementData().getLastSlimeLocation() != null && getBlockData().slime) {
                            Location loc = getMovementData().getLastSlimeLocation().toLocation(getPlayer().getWorld()), currentLoc = getMovementData().getTo().toLocation(getPlayer().getWorld()).clone();
                            loc.setY(0.0f);
                            currentLoc.setY(0.0f);

                            double distance = loc.distance(currentLoc);

                            if (distance > 5 && Math.abs(getMovementData().getTo().getY() - getMovementData().getFrom().getY()) == 0.0) {
                                getBlockData().slime = false;
                                getMovementData().setLastSlimeLocation(null);
                            }

                            if (distance > 10) {
                                getBlockData().slime = false;
                                getMovementData().setLastSlimeLocation(null);
                            }
                        }
                    }
                }
            }
        }

        if (blockAssesement.isChests()) {
            if (getBlockData().chestTicks < 20) getBlockData().chestTicks++;
            blockData.lastChestTick = getConnectedTick();
        } else {
            if (getBlockData().chestTicks > 0) getBlockData().chestTicks--;
        }

        if (blockAssesement.isTrapDoor()) {
            if (getBlockData().trapDoorTicks < 20) getBlockData().trapDoorTicks++;
        } else {
            if (getBlockData().trapDoorTicks > 0) getBlockData().trapDoorTicks--;
        }

        if (getMiscData().getMountedTicks() > 0) {
            getMiscData().setLastMoutUpdate(System.currentTimeMillis());
        }

        if (getPlayer().getVehicle() != null) {
            getMiscData().setLastMount(System.currentTimeMillis());
            if (getMiscData().getMountedTicks() < 20) getMiscData().setMountedTicks(getMiscData().getMountedTicks() + 2);
        } else {
            if (getMiscData().getMountedTicks() > 0) getMiscData().setMountedTicks(getMiscData().getMountedTicks() - 1);
        }

        boolean hasSpeed = getPlayer().hasPotionEffect(PotionEffectType.SPEED), hasJump = getPlayer().hasPotionEffect(PotionEffectType.JUMP);

        getMiscData().setSpeedPotionEffectLevel(MathUtil.getPotionEffectLevel(getPlayer(), PotionEffectType.SPEED));

        getMiscData().setHasSpeedPotion(hasSpeed);
        getMiscData().setHasJumpPotion(hasJump);

        if (hasJump) {
            if (getMiscData().getJumpPotionTicks() < 20) getMiscData().setJumpPotionTicks(getMiscData().getJumpPotionTicks() + 1);
            getMiscData().setJumpPotionMultiplyer(MathUtil.getPotionEffectLevel(getPlayer(), PotionEffectType.JUMP));
        } else {
            if (getMiscData().getJumpPotionTicks() > 0) getMiscData().setJumpPotionTicks(getMiscData().getJumpPotionTicks() - 1);
        }

        if (hasSpeed) {
            if (getMiscData().getSpeedPotionTicks() < 50) getMiscData().setSpeedPotionTicks(getMiscData().getSpeedPotionTicks() + 1);
        } else {
            if (getMiscData().getSpeedPotionTicks() > 0) getMiscData().setSpeedPotionTicks(getMiscData().getSpeedPotionTicks() - 1);
        }


        if (getMovementData().isCollidedGround()) {
            if (getMovementData().getCollidedGroundTicks() < 20) getMovementData().setCollidedGroundTicks(getMovementData().getCollidedGroundTicks() + 1);
        } else {
            if (getMovementData().getCollidedGroundTicks() > 0) getMovementData().setCollidedGroundTicks(getMovementData().getCollidedGroundTicks() - 1);
        }

        int groundTicks = getMovementData().getGroundTicks();
        int airTicks = getMovementData().getAirTicks();

        if (blockAssesement.isHopper()) {
            if (getBlockData().hopperTicks < 50) getBlockData().hopperTicks++;
        } else {
            if (getBlockData().hopperTicks > 0) getBlockData().hopperTicks--;
        }

        if (blockAssesement.isWall()) {
            if (getBlockData().wallTicks < 20) getBlockData().wallTicks++;
        } else {
            if (getBlockData().wallTicks > 0) getBlockData().wallTicks--;
        }


        if (blockAssesement.isLillyPad()) {
            if (getBlockData().lillyPadTicks < 50) getBlockData().lillyPadTicks++;
        } else {
            if (getBlockData().lillyPadTicks > 0) getBlockData().lillyPadTicks--;
        }

        if (blockAssesement.isOnGround()) {

            if (blockAssesement.isAnvil()) {
                if (getBlockData().anvilTicks < 20) getBlockData().anvilTicks++;
            } else {
                if (getBlockData().anvilTicks > 0) getBlockData().anvilTicks--;
            }

            if (groundTicks < 20) groundTicks++;
            airTicks = 0;
        } else {
            if (airTicks < 20) airTicks++;
            groundTicks = 0;
        }

        if (blockAssesement.isBlockAbove()) {
            if (blockData.blockAboveTicks < 20) blockData.blockAboveTicks++;
            blockData.lastBlockAbove = System.currentTimeMillis();
            blockData.lastBlockAboveTick = getConnectedTick();
        } else {
            if (blockData.blockAboveTicks > 0) blockData.blockAboveTicks--;
        }

        if (blockAssesement.isSnow()) {
            if (blockData.snowTicks < 20) blockData.snowTicks++;
        } else {
            if (blockData.snowTicks > 0) blockData.snowTicks--;
        }

        getBlockData().climable = blockAssesement.isClimbale();

        if (blockAssesement.isClimbale()) {
            blockData.lastClimbableTimer.reset();
            if (blockData.climbableTicks < 20) blockData.climbableTicks+=2;
        } else {
            if (blockData.climbableTicks > 0)  blockData.climbableTicks--;
        }

        if (blockAssesement.isWeb()) {
            if (blockData.webTicks < 20) blockData.webTicks++;
        } else {
            if (blockData.webTicks > 0) blockData.webTicks--;
        }

        if (blockAssesement.isSoulSand()) {
            if (blockData.soulSandTicks < 20) blockData.soulSandTicks++;
            getBlockData().lastSoulSand = System.currentTimeMillis();
        } else {
            if (blockData.soulSandTicks > 0) blockData.soulSandTicks--;
        }

        if (blockAssesement.isHalfblock()) {
            if (blockData.halfBlockTicks < 20) blockData.halfBlockTicks++;
            blockData.lastHalfBlockTick = getConnectedTick();
        } else {
            if (blockData.halfBlockTicks > 0) blockData.halfBlockTicks--;
        }

        if (blockAssesement.isLiquid()) {
            if (blockData.liquidTicks < 20) blockData.liquidTicks++;
        } else {
            if (blockData.liquidTicks > 0) blockData.liquidTicks--;
        }

        if (blockAssesement.isCarpet()) {
            if (blockData.carpetTick < 20) blockData.carpetTick++;
        } else {
            if (blockData.carpetTick > 0) blockData.carpetTick--;
        }

        if (blockAssesement.isRedstone()) {
            if (blockData.redstoneTick < 20) blockData.redstoneTick+=2;
        } else {
            if (blockData.redstoneTick > 0) blockData.redstoneTick--;
        }

        getBlockData().ice = blockAssesement.isOnIce();

        if (blockAssesement.isOnIce() || blockAssesement.isOnNearIce()) {
            if (blockData.iceTicks < 20) blockData.iceTicks++;
            blockData.lastIce = System.currentTimeMillis();
            blockData.lastIceTick = getConnectedTick();
        } else {
            if (blockData.iceTicks > 0) blockData.iceTicks--;
        }

        if (blockAssesement.isStair()) {
            if (blockData.stairTicks < 20) blockData.stairTicks++;
            blockData.lastStairTicks = getConnectedTick();
        } else {
            if (blockData.stairTicks > 0) blockData.stairTicks--;
        }

        if (blockAssesement.isSlab()) {
            if (blockData.slabTicks < 20) blockData.slabTicks++;
            blockData.lastSlabTick = getConnectedTick();
        } else {
            if (blockData.slabTicks > 0) blockData.slabTicks--;
        }

        if (blockAssesement.isFence()) {
            if (blockData.fenceTicks < 20) blockData.fenceTicks++;
        } else {
            if (blockData.fenceTicks > 0) blockData.fenceTicks--;
        }


        if (blockAssesement.isRail()) {
            if (blockData.railTicks < 20) blockData.railTicks++;
        } else {
            if (blockData.railTicks > 0) blockData.railTicks--;
        }

        if (blockAssesement.isSlime()) {
            blockData.lastSline = System.currentTimeMillis();
            if (blockData.slimeTicks < 20) blockData.slimeTicks++;
            blockData.lastSlimeTick = getConnectedTick();
        } else {
            if (blockData.slimeTicks > 0) blockData.slimeTicks--;
        }


        if (blockAssesement.isHalfGlass()) {
            if (blockData.glassPaneTicks < 20) blockData.glassPaneTicks++;
        } else {
            if (blockData.glassPaneTicks > 0) blockData.glassPaneTicks--;
        }

        if (blockAssesement.isDoor()) {
            if (blockData.doorTicks < 20) blockData.doorTicks++;
        } else {
            if (blockData.doorTicks > 0) blockData.doorTicks--;
        }


        getMovementData().setAirTicks(airTicks);
        getMovementData().setGroundTicks(groundTicks);

        if (getMovementData().isClientGround()) {
            if (getMovementData().getClientGroundTicks() < 20) getMovementData().setClientGroundTicks(getMovementData().getClientGroundTicks() + 1);
            getMovementData().setClientAirTicks(0);
        } else {
            if (getMovementData().getClientAirTicks() < 20) getMovementData().setClientAirTicks(getMovementData().getClientAirTicks() + 1);
            getMovementData().setClientGroundTicks(0);
        }

    }

    public boolean generalCancel() {
        return wasFlying || getPlayer().isFlying() || getPlayer().getAllowFlight() || getPlayer().getGameMode().equals(GameMode.CREATIVE) || (Venom.getInstance().getVersionUtil().getVersion() == VersionUtil.Version.V1_8 && getPlayer().getGameMode().equals(GameMode.SPECTATOR));
    }
}