package dev.demon.venom.utils.block;


import dev.demon.venom.Venom;
import dev.demon.venom.api.user.User;
import dev.demon.venom.utils.box.BoundingBox;
import dev.demon.venom.utils.box.ReflectionUtil;
import dev.demon.venom.utils.version.VersionUtil;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.material.*;


import java.util.List;
import java.util.Objects;

/**
 * Created on 21/10/2019 Package me.jumba.util.block
 */
@Getter
@Setter
public class BlockAssesement {

    private User user;
    private BoundingBox boundingBox;
    private boolean cactus, redstone, enchantmentTable, nearWallCombat, inBlock, testGround, ironBar, collidesHorizontallyStrick, presurePlate, enderFrame, bed, leaves, chest, trapDoor, hopper, lillyPad, anvil, collidedGround, door, halfGlass, liquidGround, onGround, collidesVertically, collidesHorizontally, soulSand, snow, onIce, onNearIce, blockAbove, stair, slab, pistion, climbale, groundSlime, web, chests, halfblock, liquid, wall, carpet, stairSlabs, slime, fence, rail;

    private int lastNoneNullblock = 0;

    public BlockAssesement(BoundingBox box, User user) {
        this.user = user;
        this.boundingBox = box;
    }


    public void update(BoundingBox bb, Block block, World world) {


        if (user.getMovementData().isChunkLoaded()) {

            if (block != null) {
                user.getMovementData().setLastBlockGroundTick(user.getConnectedTick());
            }

            if ((bb.collidesVertically(boundingBox.subtract(0, 0, 0, 0 , 0, 0)))) {
                testGround = true;
            }

            if ((bb.collidesVertically(boundingBox.subtract(0, 0.1f, 0, 0, 0, 0)) || bb.collidesVertically(boundingBox.subtract(0, 0.2f, 0, 0, 0, 0)) || bb.collidesVertically(boundingBox.subtract(0, 0.3f, 0, 0, 0, 0)) || bb.collidesVertically(boundingBox.subtract(0, 0.4f, 0, 0, 0, 0)) || bb.collidesVertically(boundingBox.subtract(0, 0.5f, 0, 0, 0, 0))) || (bb.collidesVertically(boundingBox.subtract(0, 0.12f, 0, 0, 1f, 0)))) {

                if (block != null && (block.getType() == Material.WATER || block.getType() == Material.LAVA || block.getType() == Material.STATIONARY_LAVA || block.getType() == Material.STATIONARY_WATER)) {
                    liquidGround = true;
                }

                if (((Math.abs(user.getMovementData().getTo().getY() - user.getMovementData().getFrom().getY()) > 0.0) && collidesHorizontally && collidesVertically)) {
                    onGround = false;
                    collidedGround = true;
                } else {
                    onGround = true;
                }
            }

            if (bb.collidesHorizontally(boundingBox)) {
                collidesHorizontally = true;

                if (block != null && (block.getType() == Material.LONG_GRASS || block.getType() == Material.DOUBLE_PLANT)) {
                    collidesHorizontallyStrick = true;
                }
            }

            if (bb.collidesVertically(boundingBox)) {
                collidesVertically = true;
            }

            if (block != null) {

                user.totalBlockUpdates++;

            }
        }
    }

    public void updateBlocks(BlockEntry blockEntry, boolean ladder) {

        //data.parallelStream().forEach(blockEntry -> {


        Block block = blockEntry.getBlock();

        BoundingBox bb = blockEntry.getBoundingBox();

        if (bb.intersectsWithBox(user.getBoundingBox())) {
            inBlock = true;
        }


        if (bb.collidesVertically(boundingBox)) {
            collidesVertically = true;
        }

        if (block.getType() != null && block.getType().isBlock()) {
            if (bb.collidesHorizontally(boundingBox)) {
                collidesHorizontally = true;
            }

            if (blockEntry.isInCombat() && bb.grow(1.9f, 0, 1.9f).collidesHorizontally(boundingBox)) {
                nearWallCombat = true;
            }
        }


        if ((bb.collidesVertically(boundingBox.subtract(0, 0.1f, 0, 0, 0, 0))
                || bb.collidesVertically(boundingBox.subtract(0, 0.2f, 0, 0, 0, 0)))) {

            onGround = true;
        }



        if (block.getType() == Material.REDSTONE_WIRE) {
            redstone = true;
        }

        if (block != null) {

            if (Venom.getInstance().getVersionUtil().getVersion() != VersionUtil.Version.V1_7 && block.getType() == Material.SLIME_BLOCK) {
                slime = true;
            }

            switch (block.getType()) {

                case TRIPWIRE:
           /*     case STRING: {
                    string = true;
                    break;
                }*/

                case CARPET: {
                    carpet = true;

                    if (user.getMovementData().isClientGround()) {
                        onGround = true;
                    }

                    break;
                }

                case ICE:
                case PACKED_ICE: {
                    onIce = onNearIce = true;
                    break;
                }

                case WEB: {
                    web = true;
                    break;
                }

                case ENDER_PORTAL_FRAME: {
                    enderFrame = true;
                    break;
                }

                case BED_BLOCK:
                case BED: {
                    bed = true;
                    halfblock = true;
                    break;
                }

                case LEAVES:
                case LEAVES_2: {
                    leaves = true;
                    break;
                }

                case CHEST:
                case TRAPPED_CHEST:
                case ENDER_CHEST: {
                    chest = true;
                    halfblock = true;
                    break;
                }

                case HOPPER: {
                    hopper = true;
                    break;
                }

                case WATER_LILY: {
                    lillyPad = true;
                }

                case WATER:
                case STATIONARY_WATER:
                case LAVA:
                case STATIONARY_LAVA: {
                    liquid = true;
                }

                case ANVIL: {
                    anvil = true;

                    //wtF?
                    if (!onGround && user.getMovementData().isClientGround()) {
                        onGround = true;
                    }

                    break;
                }

                case SNOW:
                case SNOW_BLOCK: {
                    snow = true;
                    break;
                }

                case LADDER:
                case VINE: {
                    if (block.getType() != Material.SNOW && block.getType() != Material.SNOW_BLOCK) {
                        climbale = true;
                    }
                    halfblock = true;

                    break;
                }

                case COBBLE_WALL: {
                    wall = true;
                    break;
                }

                case SOUL_SAND: {
                    soulSand = true;
                    break;
                }

                case IRON_BARDING: {
                    ironBar = true;
                    halfblock = true;

                    break;
                }

                case ENCHANTMENT_TABLE: {
                    enchantmentTable = true;
                    halfblock = true;
                    break;
                }

                case PISTON_STICKY_BASE:
                case PISTON_BASE:
                case PISTON_EXTENSION:
                case PISTON_MOVING_PIECE: {
                    pistion = true;
                    break;
                }

                case FLOWER_POT:
                case DAYLIGHT_DETECTOR:
                case DAYLIGHT_DETECTOR_INVERTED:
                case SKULL:
                case CAKE:
                case REDSTONE_COMPARATOR:
                case REDSTONE_COMPARATOR_OFF:
                case REDSTONE_COMPARATOR_ON:
                case DIODE_BLOCK_OFF:
                case DIODE_BLOCK_ON:
                case DIODE:
                case CAKE_BLOCK: {

                    switch (block.getType()) {
                        case REDSTONE_COMPARATOR:
                        case REDSTONE_COMPARATOR_OFF:
                        case DIODE_BLOCK_OFF:
                        case DIODE_BLOCK_ON:
                        case DIODE:
                  //      case REDSTONE_COMPARATOR_ON: {
                    //        redstoneRepeater = true;
                      //      break;
                        //}
                    }

                    halfblock = true;
                    break;
                }

                case FENCE:
                case FENCE_GATE:
                case ACACIA_FENCE:
                case BIRCH_FENCE:
                case ACACIA_FENCE_GATE:
                case DARK_OAK_FENCE:
                case IRON_FENCE:
                case JUNGLE_FENCE:
                case BIRCH_FENCE_GATE:
                case DARK_OAK_FENCE_GATE:
                case JUNGLE_FENCE_GATE:
                case NETHER_FENCE:
                case SPRUCE_FENCE:
                case SPRUCE_FENCE_GATE: {
                    fence = true;
                    halfblock = true;
                    break;
                }

                case GOLD_PLATE:
                case IRON_PLATE:
                case STONE_PLATE:
                case WOOD_PLATE: {
                    presurePlate = true;
                    break;
                }

                case TRAP_DOOR:
                case IRON_TRAPDOOR: {
                    trapDoor = true;
                    halfblock = true;
                    break;
                }

                case DARK_OAK_DOOR:
                case ACACIA_DOOR:
                case BIRCH_DOOR:
                case IRON_DOOR:
                case JUNGLE_DOOR:
                case SPRUCE_DOOR:
                case WOOD_DOOR:
                case WOODEN_DOOR:
                case DARK_OAK_DOOR_ITEM:
                case ACACIA_DOOR_ITEM:
                case BIRCH_DOOR_ITEM:
                case IRON_DOOR_BLOCK:
                case JUNGLE_DOOR_ITEM:
                case SPRUCE_DOOR_ITEM: {
                    door = true;
                    break;
                }

                case STAINED_GLASS_PANE: {
                    halfGlass = true;
                    halfblock = true;
                    break;
                }

                case SANDSTONE_STAIRS:
                case SMOOTH_STAIRS:
                case SPRUCE_WOOD_STAIRS:
                case ACACIA_STAIRS:
                case BIRCH_WOOD_STAIRS:
                case BRICK_STAIRS:
                case COBBLESTONE_STAIRS:
                case DARK_OAK_STAIRS:
                case JUNGLE_WOOD_STAIRS:
                case NETHER_BRICK_STAIRS:
                case QUARTZ_STAIRS:
                case RED_SANDSTONE_STAIRS:
                case WOOD_STAIRS: {
                    stair = true;
                    stairSlabs = true;
                    halfblock = true;
                    break;
                }

                case DETECTOR_RAIL:
                case POWERED_RAIL:
                case ACTIVATOR_RAIL:
                case RAILS: {
                    rail = true;
                    break;
                }


                case LONG_GRASS:
             /*    case DOUBLE_PLANT: {
                    plant = true;
                    break;
                }*/
            }

            switch (block.getType().getId()) {
                case 85:
                case 139:
                case 113:
                case 188:
                case 189:
                case 190:
                case 191:
                case 192: {
                    wall = true;
                }
            }


            if (slab || stair || fence || wall || ladder) {
                halfblock = true;
            }

            if (ladder) {
                climbale = true;
            }

            Class<? extends MaterialData> blockData = block.getType().getData();

            if ((bb.getMaximum().getY()) >= boundingBox.getMaximum().getY()
                    && bb.collidesVertically(boundingBox.add(0, 0, 0, 0, 0.35f, 0))
                    && block.getType() != Material.DOUBLE_PLANT) {
                blockAbove = true;
            }

            if (block.getType() == Material.STEP || blockData == Step.class || blockData == WoodenStep.class) {
               //Bukkit.broadcastMessage(""+(bb.getMaximum().getY() - bb.getMinimum().getY()));
                slab = true;
                stairSlabs = true;
            }
        }
        //});

    }
}