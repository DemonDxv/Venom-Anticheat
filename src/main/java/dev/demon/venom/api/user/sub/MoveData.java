package dev.demon.venom.api.user.sub;

import dev.demon.venom.api.user.User;
import dev.demon.venom.api.user.User;
import dev.demon.venom.utils.location.CustomLocation;
import dev.demon.venom.utils.location.PlayerLocation;
import dev.demon.venom.utils.time.EventTimer;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.block.Block;

@Getter
@Setter
public class MoveData {
    private User user;
    private Block serverBlockBelow;
    private Location bukkitTo, bukkitFrom;
    private CustomLocation lastGroundLocation, lastSlimeLocation = new CustomLocation(0, 0, 0), to = new CustomLocation(0, 0, 0), from = to, fromFrom = from, toPacket = new CustomLocation(0, 0, 0), fromPacket = toPacket;
    private boolean invalidMovement, inBlockTeleporting, lastSprint, testGround, breakingOrPlacingBlock, didUnknownTeleport, isExplode, clientGround, lastClientGround, collidedGround, nearBoat, lastCollidedVertically, lastCollidedHorizontally, didTeleportInteract, chunkLoaded, onGround, worldLoaded, collidesHorizontally, collidesVertically, lastOnGround, sprinting, jumpPad, sneaking;
    private int lastTeleportTick, boatTicks, lastServerPostionFull, lastTelportInteractTick, commandBlockTeleportTicks, sprintTicks, lastServerPostion, clientAirTicks, clientGroundTicks, lastJumpPadUpdateTick, lastCheckBlockTick, lastBlockGroundTick, velocityTicks, mouseDeltaX, mouseDeltaY, unknownTeleportTick, afkMovementTotalBlocks, totalSlimeBlocksMoved, collidedGroundTicks, airTicks, groundTicks;
    private long lastTeleportInBlock, lastDig, lastBlockJump, lastPos, lastBlockFall, lastCollidedGround, lastFullBlockMoved, LastJunpPadUpdate, lastJumpPadSet, breakingOrPlacingTime, lastUnknownTeleport, lastTeleport, lastFullTeleport, lastExplode, lastEnderpearl, lastFallDamage, lastNearBoat;
    private double prediction, deltaXZ, lastGroundPrediction, lastFallJumpPrediction, groundYPredict, walkSpeed, movementSpeed;
    public PlayerLocation location2, location, previousLocation, previousPreviousLocation;
    private float pitchDelta, yawDelta, yawDeltaClamped;
    public EventTimer lastBlockDigTimer, lastBlockMoveTimer, lastLagTPReset, lastServerVelocity, jumpPadUpdateTimer, sufficationTimer, lastTeleportTimer, serverPositionTimer, blockJumpTimer, blockFallTimer, projectileTimer, enderPearlTimer, commandBlockTPTimer, jumpPadResetTimer, blockPlacedOnFenceTimer, fallDamageTimer, lastChunkNotLoaded, lastMovementTimer;

    public MoveData(User user) {
        this.user = user;

        lastTeleportTimer = new EventTimer(15, user);
        serverPositionTimer = new EventTimer(6, user);
        blockJumpTimer = new EventTimer(7, user);
        blockFallTimer = new EventTimer(7, user);
        projectileTimer = new EventTimer(20, user);
        enderPearlTimer = new EventTimer(20, user);
        commandBlockTPTimer = new EventTimer(20, user);
        jumpPadResetTimer = new EventTimer(20, user);
        blockPlacedOnFenceTimer = new EventTimer(20, user);
        fallDamageTimer = new EventTimer(20, user);
        lastChunkNotLoaded = new EventTimer(20, user);
        lastMovementTimer = new EventTimer(20, user);
        sufficationTimer = new EventTimer(20, user);
        jumpPadUpdateTimer = new EventTimer(20, user);
        lastServerVelocity = new EventTimer(20, user);
        lastLagTPReset = new EventTimer(100, user);
        lastBlockMoveTimer = new EventTimer(400, user);
        lastBlockDigTimer = new EventTimer(20, user);

        lastServerVelocity.setExtendWithLag(true);
    }
}