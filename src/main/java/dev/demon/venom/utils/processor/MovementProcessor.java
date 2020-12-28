package dev.demon.venom.utils.processor;

import dev.demon.venom.Venom;
import dev.demon.venom.api.tinyprotocol.api.NMSObject;
import dev.demon.venom.api.tinyprotocol.api.Packet;
import dev.demon.venom.api.tinyprotocol.api.TinyProtocolHandler;
import dev.demon.venom.api.tinyprotocol.packet.in.*;
import dev.demon.venom.api.tinyprotocol.packet.outgoing.WrappedOutEntityEffectPacket;
import dev.demon.venom.api.tinyprotocol.packet.outgoing.WrappedOutPositionPacket;
import dev.demon.venom.api.tinyprotocol.packet.outgoing.WrappedOutTransaction;
import dev.demon.venom.api.tinyprotocol.packet.outgoing.WrappedOutVelocityPacket;
import dev.demon.venom.api.user.User;
import dev.demon.venom.impl.events.ServerShutdownEvent;
import dev.demon.venom.utils.block.BlockAssesement;
import dev.demon.venom.utils.block.BlockEntry;
import dev.demon.venom.utils.block.BlockUtil;
import dev.demon.venom.utils.box.BoundingBox;
import dev.demon.venom.utils.command.CommandUtils;
import dev.demon.venom.utils.connection.HTTPUtil;
import dev.demon.venom.utils.location.CustomLocation;
import dev.demon.venom.utils.location.PlayerLocation;
import dev.demon.venom.utils.math.MathUtil;
import dev.demon.venom.utils.math.evicting.EvictingList;

import dev.demon.venom.utils.time.TickTimer;
import dev.demon.venom.utils.time.TimeUtils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;


import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created on 05/01/2020 Package me.jumba.sparky.util.processor
 */
@Setter
@Getter
public class MovementProcessor {
    private User user;

    private double offset = Math.pow(2.0, 24.0);

    private double pitchDelta, yawDelta, lastDeltaYaw, lastDeltaPitch, pitchMode, yawMode, sensXPercent, deltaX, deltaY, sensYPercent, lastSensX, lastSensY, sensitivityX, sensitivityY, lastDeltaX, lastDeltaY;

    public long pitchGCD, yawGCD;

    public List<Double> pitchGcdList = new EvictingList(40), yawGcdList = new EvictingList(40);

    private TickTimer timer = new TickTimer(5);

    private short inventoryCloseTransction = (short) MathUtil.getRandomInteger(1000, 9000);
    private List<Short> inventoryTransactions = new CopyOnWriteArrayList<>();
    private boolean expectingInventoryClose;

    private double lastGroundY;

    public void update(Object packet, String type) {
        if (user != null) {

            if (type.equalsIgnoreCase(Packet.Server.POSITION)) {

                WrappedOutPositionPacket wrappedOutSpawnEntityPacket = new WrappedOutPositionPacket(packet, user.getPlayer());

                //user.debug("" + wrappedOutSpawnEntityPacket.getY());

                if (wrappedOutSpawnEntityPacket.getY() > 0.0) {

                    if (user.isInBlock() && user.getMovementData().getTo() != null) {
                        double deltaY = (user.getMovementData().getTo().getY() - this.lastGroundY);
                        if (deltaY <= .8) {
                            user.getBlockData().lastInsideBlockTimer.reset();
                        }
                    }

                    if (user.getMovementData().isDidTeleportInteract()) {
                        user.getMovementData().setLastTelportInteractTick(user.getConnectedTick());
                        user.getMovementData().setDidTeleportInteract(false);
                        user.getMovementData().setCommandBlockTeleportTicks(100);
                    }

                    user.getMovementData().setLastCheckBlockTick(user.getConnectedTick());


                    user.getMovementData().setLastServerPostion(user.getConnectedTick());


                } else {
                    user.getMovementData().setLastServerPostionFull(user.getConnectedTick());
                }
            }

            if (type.equalsIgnoreCase(Packet.Client.BLOCK_PLACE)) {

                if (user.getMovementData().isChunkLoaded()) {

                    boolean valid = false;

                    if (user.getPlayer().getWorld() != null) {

                        WrappedInBlockPlacePacket wrappedInBlockPlacePacket = new WrappedInBlockPlacePacket(packet, user.getPlayer());

                        Block block = BlockUtil.getBlock(new Location(user.getPlayer().getWorld(), wrappedInBlockPlacePacket.getPosition().getX(),
                                wrappedInBlockPlacePacket.getPosition().getY(),
                                wrappedInBlockPlacePacket.getPosition().getZ()));

                        Block blockBelow = BlockUtil.getBlock(new Location(user.getPlayer().getWorld(),
                                wrappedInBlockPlacePacket.getPosition().getX(),
                                wrappedInBlockPlacePacket.getPosition().getY() - 1,
                                wrappedInBlockPlacePacket.getPosition().getZ()));


                        if (block != null && blockBelow != null && blockBelow.getType() == Material.AIR) {

                            Block blockBelowPlayer = BlockUtil.getBlock(user.getPlayer().getLocation().clone().add(0, -1, 0));
                            Block blockBelowBelowPlayer = BlockUtil.getBlock(user.getPlayer().getLocation().clone().add(0, -2, 0));

                            if (blockBelowPlayer == null || blockBelowBelowPlayer == null) {
                                return;
                            }
                            if (user.getMiscData().getLastBlockPlaced() != null && blockBelowPlayer.getType() != Material.AIR && blockBelowBelowPlayer.getType() == Material.AIR) {
                                Block lastBlock = user.getMiscData().getLastBlockPlaced();

                                if (block.getLocation().distanceSquared(lastBlock.getLocation()) < 2) {

                                    List<Block> lastTwoTargetBlocks = user.getPlayer().getLastTwoTargetBlocks((HashSet<Byte>) null, 100);

                                    if (!(lastTwoTargetBlocks.size() != 2
                                            || !lastTwoTargetBlocks.get(1).getType().isOccluding())) {
                                        Block targetBlock = lastTwoTargetBlocks.get(1);
                                        Block adjacentBlock = lastTwoTargetBlocks.get(0);

                                        if (targetBlock.getFace(adjacentBlock) == BlockFace.UP) {
                                            valid = true;
                                        }
                                    }

                                    lastTwoTargetBlocks.clear();
                                }
                            }

                            if (BlockUtil.isHalfBlock(block)) {
                                valid = false;
                                user.getMiscData().setScaffoldProcessorIgnoreTicks(5);
                            }

                            if (user.getMiscData().getScaffoldProcessorIgnoreTicks() > 0) {
                                user.getMiscData().setScaffoldProcessorIgnoreTicks(user.getMiscData().getScaffoldProcessorIgnoreTicks() - 1);
                                valid = false;
                            }

                            user.getMiscData().setLastBlockPlaced(block);
                        }
                    }

                    user.getMiscData().setBlockPlaceValidScaffold(valid);
                }
            }

            if (type.equalsIgnoreCase(Packet.Client.BLOCK_PLACE)) {

                user.getMovementData().setLastCheckBlockTick(user.getConnectedTick());

                user.getMovementData().setLastDig(System.currentTimeMillis());

                user.getMovementData().setBreakingOrPlacingTime(System.currentTimeMillis());
            }

            if (type.equalsIgnoreCase(Packet.Client.BLOCK_DIG)) {

                user.getMovementData().setLastCheckBlockTick(user.getConnectedTick());

                WrappedInBlockDigPacket wrappedInBlockDigPacket = new WrappedInBlockDigPacket(packet, user.getPlayer());

                if (wrappedInBlockDigPacket.getAction() == WrappedInBlockDigPacket.EnumPlayerDigType.START_DESTROY_BLOCK) {
                    user.getMovementData().setBreakingOrPlacingBlock(true);
                    user.getMovementData().setBreakingOrPlacingTime(System.currentTimeMillis());
                } else if (wrappedInBlockDigPacket.getAction() == WrappedInBlockDigPacket.EnumPlayerDigType.STOP_DESTROY_BLOCK) {
                    user.getMovementData().setBreakingOrPlacingBlock(false);
                } else if (wrappedInBlockDigPacket.getAction() == WrappedInBlockDigPacket.EnumPlayerDigType.ABORT_DESTROY_BLOCK) {
                    user.getMovementData().setBreakingOrPlacingBlock(false);
                }
            }

            if (type.equalsIgnoreCase(Packet.Client.CLIENT_COMMAND)) {
                WrappedInClientCommand wrappedInClientCommand = new WrappedInClientCommand(packet, user.getPlayer());

                if (wrappedInClientCommand.getCommand() == WrappedInClientCommand.EnumClientCommand.OPEN_INVENTORY_ACHIEVEMENT) {
                    Bukkit.broadcastMessage("1");
                    user.getMiscData().setInventoryOpen(true);
                }
            }

            if (type.equalsIgnoreCase(Packet.Server.ENTITY_VELOCITY)) {

                WrappedOutVelocityPacket velocityPacket = new WrappedOutVelocityPacket(packet, user.getPlayer());
                if (velocityPacket.getId() == user.getPlayer().getEntityId()) {

                    user.getMovementData().setLastCheckBlockTick(user.getConnectedTick());


                    if (user.getMovementData().isJumpPad() && (System.currentTimeMillis() - user.getMovementData().getLastFallDamage()) < 1000L) {
                        user.getMovementData().setJumpPad(false);
                        user.getMovementData().setLastJunpPadUpdate(0);
                        return;
                    }

                    if (!user.getMovementData().isJumpPad() && (System.currentTimeMillis() - user.getMovementData().getLastFallDamage()) > 1000L && user.getMovementData().isOnGround() && (System.currentTimeMillis() - user.getCombatData().getLastEntityDamageAttack()) > 20L) {
                        user.getMovementData().setJumpPad(true);
                        user.getMovementData().setLastJumpPadSet(System.currentTimeMillis());
                    }
                }

                if (user.getMovementData().isJumpPad() && user.getMovementData().isOnGround() && user.getMovementData().getGroundTicks() > 15 && (System.currentTimeMillis() - user.getMovementData().getLastJumpPadSet()) > 1000L) {
                    user.getMovementData().setJumpPad(false);
                }
            }

            if (type.equalsIgnoreCase(Packet.Client.ENTITY_ACTION)) {
                user.getMovementData().setLastSprint(user.getMovementData().isSprinting());
                WrappedInEntityActionPacket wrappedInEntityActionPacket = new WrappedInEntityActionPacket(packet, user.getPlayer());
                if (wrappedInEntityActionPacket.getAction() == WrappedInEntityActionPacket.EnumPlayerAction.START_SPRINTING) {
                    user.getMovementData().setSprinting(true);
                } else if (wrappedInEntityActionPacket.getAction() == WrappedInEntityActionPacket.EnumPlayerAction.STOP_SPRINTING) {
                    user.getMovementData().setSprinting(false);
                }

                if (wrappedInEntityActionPacket.getAction()
                        == WrappedInEntityActionPacket.EnumPlayerAction.START_SNEAKING) {
                    user.getMovementData().setSneaking(true);
                } else if (wrappedInEntityActionPacket.getAction()
                        == WrappedInEntityActionPacket.EnumPlayerAction.STOP_SPRINTING) {
                    user.getMovementData().setSneaking(false);
                }
            }

            if (type.equalsIgnoreCase(Packet.Client.CLOSE_WINDOW)) {

                if (!this.expectingInventoryClose) {
                    this.expectingInventoryClose = true;

                    TinyProtocolHandler.sendPacket(user.getPlayer(), new WrappedOutTransaction(0, this.inventoryCloseTransction, false).getObject());
                    this.inventoryTransactions.add(this.inventoryCloseTransction);

                    if (this.inventoryCloseTransction < 999) {
                        this.inventoryCloseTransction = (short) MathUtil.getRandomInteger(1000, 9000);
                    }
                }
            }

            if (type.equalsIgnoreCase(Packet.Client.TRANSACTION)) {
                if (this.expectingInventoryClose) {

                    WrappedInTransactionPacket wrappedInTransactionPacket = new WrappedInTransactionPacket(packet, user.getPlayer());
                    short ID = wrappedInTransactionPacket.getAction();


                    if (this.inventoryTransactions.contains(ID)) {
                        this.expectingInventoryClose = false;

                        user.getMiscData().setInventoryOpen(false);

                        this.inventoryTransactions.remove(ID);
                    }
                }
            }

            if (type.equalsIgnoreCase(Packet.Client.POSITION)
                    || type.equalsIgnoreCase(Packet.Client.POSITION_LOOK)
                    || type.equalsIgnoreCase(Packet.Client.LOOK)
                    || type.equalsIgnoreCase(Packet.Client.FLYING)) {

                WrappedInFlyingPacket wrappedInFlyingPacket = new WrappedInFlyingPacket(packet, user.getPlayer());

                //Used for block lagback checking, dont use this for checks.
                if (wrappedInFlyingPacket.isPos()
                        && user.getMovementData().isOnGround()
                        && user.getMovementData().isLastOnGround()
                        && user.getMovementData().isClientGround()
                        && user.getMovementData().isLastClientGround()
                        && user.getMovementData().getTo().getY() % 0.015625 == 0.0
                        && user.getMovementData().getFrom().getY() % 0.015625 == 0.0) {
                    this.lastGroundY = wrappedInFlyingPacket.getY();
                }

                if (user.getCombatData().getRespawnTimer().hasNotPassed(5)) {
                    user.getMiscData().setInventoryOpen(false);
                    user.getPlayer().closeInventory();
                }


                user.getOldProcessors().updateOldPrediction();


                if (user.getPlayer().isDead()) {
                    user.getMiscData().setDead(true);
                    user.getMiscData().setLastDeadTick(user.getConnectedTick());
                } else {
                    user.getMiscData().setDead(false);
                }

                float pitchDelta = Math.abs(user.getMovementData().getTo().getPitch() - user.getMovementData().getFrom().getPitch());
                float yawDelta = Math.abs(user.getMovementData().getTo().getYaw() - user.getMovementData().getFrom().getYaw());

                user.getMovementData().setYawDelta(yawDelta);
                user.getMovementData().setYawDeltaClamped(MathUtil.yawTo180F(yawDelta));
                user.getMovementData().setPitchDelta(pitchDelta);

                if (user.getMovementData().isDidUnknownTeleport()) {

                    if (Math.abs(user.getConnectedTick() - user.getMovementData().getUnknownTeleportTick()) > 20) {

                        if ((user.getMovementData().getTo().getY() - user.getMovementData().getFrom().getY() >= 0.0)) {


                            if (user.getMovementData().getMovementSpeed() > 0.66 && (System.currentTimeMillis() - user.getCombatData().getLastEntityDamage()) > 1000L && (System.currentTimeMillis() - user.getCombatData().getLastBowDamage()) > 1000L) {
                                user.getMovementData().setDidUnknownTeleport(false);
                            }
                        }

                        if (Math.abs(user.getConnectedTick() - user.getMovementData().getUnknownTeleportTick()) > 5 && (user.getMovementData().isOnGround() || user.getMovementData().isClientGround())) {
                            user.getMovementData().setDidUnknownTeleport(false);
                        }
                    }
                }

                if ((System.currentTimeMillis() - user.getMovementData().getLastExplode()) > 1000L && user.getMovementData().isExplode() && user.getMovementData().isOnGround() && user.getMovementData().isLastOnGround()) {
                    user.getMovementData().setExplode(false);
                }

                if (user.isWaitingForMovementVerify()) {

                    if (user.movementVerifyBlocks > 5) {
                        user.movementVerifyBlocks = 0;
                        user.setWaitingForMovementVerify(false);
                    }

                    double x = Math.floor(user.getMovementData().getFrom().getX());
                    double z = Math.floor(user.getMovementData().getFrom().getZ());
                    if (Math.floor(user.getMovementData().getTo().getX()) != x || Math.floor(user.getMovementData().getTo().getZ()) != z) {
                        user.movementVerifyBlocks++;
                    }
                }


                if (user.getFlyingTick() < 20) {
                    user.setFlyingTick(user.getFlyingTick() + 1);
                } else if (user.getFlyingTick() >= 20) {
                    user.setFlyingTick(0);
                }

                user.getVelocityData().setLastVelocityTicks(user.getVelocityData().getVelocityTicks());

                user.setConnectedTick(user.getConnectedTick() + 1);

                user.getCombatData().setTransactionHits(user.getCombatData().getTransactionHits() + 1);

                user.getVelocityData().setVelocityTicks(user.getVelocityData().getVelocityTicks() + 1);
                user.getVelocityProcessor().setTicksSinceVelocity(user.getVelocityProcessor().getTicksSinceVelocity() + 1);

                if (user.getMovementData().isJumpPad()) {
                    if ((System.currentTimeMillis() - user.getMovementData().getLastJumpPadSet()) > 230L && user.getMovementData().isOnGround() && user.getMovementData().isLastOnGround()) {
                        user.getMovementData().setJumpPad(false);
                    }
                    user.getMovementData().setLastJunpPadUpdate(System.currentTimeMillis());
                }

                user.setSafe(TimeUtils.secondsFromLong(user.getTimestamp()) > 2L || user.isHasVerify());

                if (!user.isHasVerify()) user.setHasVerify(user.isSafe());


                if (user.isSafe()) {

                    if (!user.getMiscData().isAfkMovement() && ((System.currentTimeMillis() - user.getMovementData().getLastFullBlockMoved()) > 700L || (System.currentTimeMillis() - user.getCombatData().getLastEntityDamage()) < 1000L || (System.currentTimeMillis() - user.getCombatData().getLastBowDamage() < 1000L || (System.currentTimeMillis() - user.getCombatData().getLastRandomDamage()) < 1000L)) && (user.getMovementData().getTo().getY() - user.getMovementData().getFrom().getY()) < -0.55) {
                        user.getMiscData().setAfkMovement(true);
                    } else if (user.getMiscData().isAfkMovement()) {
                        if (user.getMovementData().getAfkMovementTotalBlocks() > 3) {
                            user.getMovementData().setAfkMovementTotalBlocks(0);
                            user.getMiscData().setAfkMovement(false);
                        }
                    }

                    user.getMovementData().setLastClientGround(user.getMovementData().isClientGround());
                    user.getMovementData().setClientGround(wrappedInFlyingPacket.isGround());


                    if (user.getMovementData().getFrom() != null) {
                        user.getMovementData().setFromFrom(user.getMovementData().getFrom().clone());
                    }
                    if (user.getMovementData().getTo() != null) {
                        user.getMovementData().setFrom(user.getMovementData().getTo().clone());
                    }

                    if (user.getMovementData().getToPacket() != null) {
                        user.getMovementData().setFromPacket(user.getMovementData().getTo().clone());
                    }


                    PlayerLocation lastLocation = user.getMovementData().getLocation();
                   // PlayerLocation lastLocation2 = user.getMovementData().getPreviousLocation();


                    if (user.isSafe() && user.getBoundingBox() != null) {

                        this.updateBlockCheck();
                    } else {
                        user.getMovementData().setOnGround(wrappedInFlyingPacket.isGround());
                    }

                    if (wrappedInFlyingPacket.isPos()) {

                        user.getMovementData().getTo().setX(wrappedInFlyingPacket.getX());
                        user.getMovementData().getTo().setY(wrappedInFlyingPacket.getY());
                        user.getMovementData().getTo().setZ(wrappedInFlyingPacket.getZ());
                        user.getMovementData().getTo().setClientGround(wrappedInFlyingPacket.isGround());
                        user.getMovementData().setLastPos(System.currentTimeMillis());

                        user.getMovementData().getToPacket().setX(user.getPlayer().getLocation().getX());
                        user.getMovementData().getToPacket().setY(user.getPlayer().getLocation().getY());
                        user.getMovementData().getToPacket().setZ(user.getPlayer().getLocation().getZ());
                        user.getMovementData().getToPacket().setClientGround(wrappedInFlyingPacket.isGround());

                        user.getMovementData().setLastOnGround(user.getMovementData().isOnGround());


                        user.previousPreviousLocations.add(user.getMovementData().getPreviousLocation());


                        CustomLocation customLocation = new CustomLocation(wrappedInFlyingPacket.getX(), wrappedInFlyingPacket.getY(), wrappedInFlyingPacket.getZ());

                        if (user.getMovementData().getLastGroundLocation() != null && user.getMovementData().isOnGround()) {

                            if (Math.abs((user.getMovementData().getTo().getY() - user.getMovementData().getFrom().getY())) > 0.0f && user.getMovementData().getTo().getY() < user.getMovementData().getFrom().getY()) {
                                double totalPrediction = MathUtil.round(user.getMovementData().getTo().getY(), 0) + user.getMovementData().getGroundYPredict();

                                if (totalPrediction < user.getMovementData().getLastFallJumpPrediction()) {
                                    user.getMovementData().setLastBlockFall(System.currentTimeMillis());
                                }

                                user.getMovementData().setLastFallJumpPrediction(totalPrediction);
                            }

                            if ((user.getMovementData().getTo().getY() - user.getMovementData().getFrom().getY()) > 0.4f && user.getMovementData().getTo().getY() > user.getMovementData().getFrom().getY()) {
                                double totalPrediction = MathUtil.round(user.getMovementData().getTo().getY(), 0) + user.getMovementData().getGroundYPredict();

                                if (totalPrediction > user.getMovementData().getLastGroundPrediction()) {
                                    user.getMovementData().setLastBlockJump(System.currentTimeMillis());
                                }

                                user.getMovementData().setLastGroundPrediction(totalPrediction);
                            }
                        }

                        if (user.getMovementData().isOnGround() && user.getMovementData().getTo() != null && user.getMovementData().getFrom() != null) {
                            if (user.getMovementData().isLastOnGround()) {
                                user.getMovementData().setGroundYPredict(user.getMovementData().getTo().getY());
                            }
                            user.getMovementData().setLastGroundLocation(customLocation);
                        }


                    }

                    user.getMovementData().setDeltaXZ((Math.hypot(user.getMovementData().getTo().getX() - user.getMovementData().getFrom().getX(), user.getMovementData().getTo().getZ() - user.getMovementData().getFrom().getZ())));

                    boolean badVector = Math.abs(user.getMovementData().getTo().toVector().length() - user.getMovementData().getFrom().toVector().length()) >= 1;

                    user.setBoundingBox(new BoundingBox((badVector ? user.getMovementData().getTo().toVector() : user.getMovementData().getFrom().toVector()), user.getMovementData().getTo().toVector()).grow(0.3f, 0, 0.3f).add(0, 0, 0, 0, 1.84f, 0));


                    if (wrappedInFlyingPacket.isLook()) {

                        user.getMovementData().getTo().setPitch(wrappedInFlyingPacket.getPitch());
                        user.getMovementData().getTo().setYaw(wrappedInFlyingPacket.getYaw());

                        user.getMovementData().getToPacket().setPitch(user.getMovementData().getLocation().getPitch());
                        user.getMovementData().getToPacket().setYaw(user.getMovementData().getLocation().getYaw());

                        user.getMovementData().setPreviousLocation(lastLocation);

                        if (user.getMovementData().getPreviousLocation() != null) {
                            user.getMovementData().setPreviousPreviousLocation(user.getMovementData().getPreviousLocation());
                        }


                        updateSensitityPrediction();
                    }

                    if (user.getMovementData().getTo() != null && user.getMovementData().getFrom() != null) {

                        if (Math.abs(user.getMovementData().getTo().getY() - user.getMovementData().getFrom().getY()) > 0.0f) {
                            user.getMovementData().setLastCheckBlockTick(user.getConnectedTick());
                        }

                        double x = Math.floor(user.getMovementData().getFrom().getX());
                        double z = Math.floor(user.getMovementData().getFrom().getZ());

                        if (Math.floor(user.getMovementData().getTo().getX()) != x || Math.floor(user.getMovementData().getTo().getZ()) != z) {

                            user.getMovementData().setLastCheckBlockTick(user.getConnectedTick());

                            if (user.totalBlocksCheck < 100) user.totalBlocksCheck++;

                            user.getMovementData().setLastFullBlockMoved(System.currentTimeMillis());

                            if (user.getCombatData().isRespawn() && (System.currentTimeMillis() - user.getCombatData().getLastRespawn()) > 1000L) {
                                user.getCombatData().setRespawn(false);
                            }

                            if (user.getMiscData().isAfkMovement()) {
                                user.getMovementData().setAfkMovementTotalBlocks(user.getMovementData().getAfkMovementTotalBlocks() + 1);
                            } else {
                                user.getMovementData().setAfkMovementTotalBlocks(0);
                            }
                        }
                    }

                    if (user.getMiscData().isSwitchedGamemodes() && user.getMovementData().isOnGround()) {
                        user.getMiscData().setSwitchedGamemodes(false);
                    }

                    if (user.getMovementData().getTo() != null && user.getMovementData().getFrom() != null) {
                        user.getMovementData().setBukkitTo(user.getMovementData().getTo().toLocation(user.getPlayer().getWorld()));
                        user.getMovementData().setBukkitFrom(user.getMovementData().getFrom().toLocation(user.getPlayer().getWorld()));

                        double x = Math.abs(Math.abs(user.getMovementData().getTo().getX()) - Math.abs(user.getMovementData().getFrom().getX()));
                        double z = Math.abs(Math.abs(user.getMovementData().getTo().getZ()) - Math.abs(user.getMovementData().getFrom().getZ()));
                        user.getMovementData().setMovementSpeed(Math.sqrt(x * x + z * z));
                    }
                }
            }
        }
    }

    private void updateBlockCheck() {

        //user.debug(""+user.getPlayer().getMaximumNoDamageTicks());

        BlockAssesement blockAssesement = new BlockAssesement(user.getBoundingBox(), user);

        user.getMovementData().setChunkLoaded(BlockUtil.isChunkLoaded(user.getMovementData().getTo().toLocation(user.getPlayer().getWorld())));

        boolean inCombat = user.getCombatData().wasAttacked(100);

        Material bukkitBlock = Objects.requireNonNull(BlockUtil.getBlock(user.getPlayer().getLocation())).getType();

        boolean climbable = (bukkitBlock == Material.LADDER || bukkitBlock == Material.VINE);

        float offset = (inCombat ? 0.9f
                : (climbable || user.getBlockData().iceTicks > 0
                || (user.getMovementData().getTo().getY() - user.getMovementData().getFrom().getY()) < -1.00f ? 0.8f : 0.3f));

        if (user.getMovementData().isChunkLoaded()) {

            Venom.getInstance().getBlockBoxManager()
                    .getBlockBox().getCollidingBoxes(user.getPlayer().getWorld(), user.getBoundingBox().grow(offset, 0.35f, offset), user)
                    .parallelStream().forEach(boundingBox -> {

                Block block = BlockUtil.getBlock(boundingBox.getMinimum().toLocation(user.getPlayer().getWorld()));

                if (block != null) {
                    blockAssesement.updateBlocks(new BlockEntry(block, boundingBox, inCombat), climbable);
                }
            });
        }

        user.getMovementData().setOnGround(blockAssesement.isOnGround());

        //    user.getMovementData().setTestGround(blockAssesement.isTestGround());

        user.getMovementData().setCollidedGround(blockAssesement.isCollidedGround());

        if (blockAssesement.isCollidedGround()) {
            user.getMovementData().setLastCollidedGround(System.currentTimeMillis());
        }

        user.getBlockData().isGroundWater = blockAssesement.isLiquidGround();
        user.update(blockAssesement);
    }


    private void updateSensitityPrediction() {

        if (user.getMovementData().getTo() != null && user.getMovementData().getFrom() != null) {

            //Credit: Dawson

            lastDeltaPitch = pitchDelta;
            lastDeltaYaw = yawDelta;
            yawDelta = Math.abs(user.getMovementData().getTo().getYaw() - user.getMovementData().getFrom().getYaw());
            pitchDelta = user.getMovementData().getTo().getPitch() - user.getMovementData().getFrom().getPitch();


            yawGCD = MathUtil.gcd((long) (yawDelta * offset), (long) (lastDeltaYaw * offset));
            pitchGCD = MathUtil.gcd((long) (Math.abs(pitchDelta) * offset), (long) (Math.abs(lastDeltaPitch) * offset));

            double yawGcd = yawGCD / offset;
            double pitchGcd = pitchGCD / offset;

            user.getMovementData().setMouseDeltaX((int)
                    (Math.abs((user.getMovementData().getTo().getYaw() - user.getMovementData().getFrom().getYaw())) / yawGcd));
            user.getMovementData().setMouseDeltaY((int)
                    (Math.abs((user.getMovementData().getTo().getPitch() - user.getMovementData().getFrom().getPitch())) / pitchGCD));

            if (yawGCD > 160000 && yawGCD < 10500000) yawGcdList.add(yawGcd);

            if (pitchGCD > 160000 && pitchGCD < 10500000) pitchGcdList.add(pitchGcd);

            if (yawGcdList.size() > 3 && pitchGcdList.size() > 3) {

                if (timer.hasPassed()) {
                    timer.reset();

                    lastSensX = sensitivityX;
                    lastSensY = sensitivityY;

                    yawMode = MathUtil.getMode(yawGcdList);
                    pitchMode = MathUtil.getMode(pitchGcdList);
                    sensXPercent = MathUtil.sensToPercent(sensitivityX = MathUtil.getSensitivityFromYawGCD(yawMode));
                    sensYPercent = MathUtil.sensToPercent(sensitivityY = MathUtil.getSensitivityFromPitchGCD(pitchMode));
                    user.getMiscData().setClientSensitivity(sensXPercent);
                    user.getMiscData().setClientSensitivity2(sensitivityY);
                    user.getMiscData().setHasSetClientSensitivity(true);
                }

                lastDeltaX = deltaX;
                lastDeltaY = deltaY;

                deltaX = MathUtil.getDeltaX(yawDelta, (float) yawMode);
                deltaY = MathUtil.getDeltaY(pitchDelta, (float) pitchMode);

                user.getOptifineProcessor().updateNew(user);
            }

        }
    }
}