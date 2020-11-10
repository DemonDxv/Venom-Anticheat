package dev.demon.venom.api.user.sub;

import dev.demon.venom.api.user.User;
import dev.demon.venom.api.user.User;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;


import java.util.concurrent.ThreadLocalRandom;

@Getter
@Setter
public class MiscData {

    private int lastDeadTick, blockingSpeedTicks, lastBlockPlaceTick, speedPotionTicks, mountedTicks, boatTicks, jumpPotionTicks;
    private boolean debugMode, hasLagged, usingChunkBuster, dead, afkMovement, hasSetClientSensitivity, inventoryOpen, hasJumpPotion, hasSpeedPotion, switchedGamemodes, isNearBoat;
    private float speedPotionEffectLevel, jumpPotionMultiplyer;
    private long lastEjectVechielEject, lastBlockBreakCancel, lastBlockCancel, lastNearBoat, lastMount, lastGamemodeSwitch, lastMoutUpdate, lastBlockPlace;
    private double clientSensitivity, clientSensitivity2;
    private short transactionID2 = randomTransactionID(), transactionID = randomTransactionID(), transactionIDVelocity = randomTransactionIDVelocity(), transactionFastID = randomTransactionID();

    private User user;

    public MiscData(User user) {
        this.user = user;
    }

    public short randomTransactionID() {
        return (short) ThreadLocalRandom.current().nextInt(999999999);
    }
    public short randomTransactionIDVelocity() {
        return (short) ThreadLocalRandom.current().nextInt(999999999);
    }

    public boolean isPickaxe(ItemStack itemStack) {
        if (itemStack.getType() == Material.DIAMOND_PICKAXE
                || itemStack.getType() == Material.IRON_PICKAXE
                || itemStack.getType() == Material.GOLD_PICKAXE
                || itemStack.getType() == Material.STONE_PICKAXE
                || itemStack.getType() == Material.WOOD_PICKAXE) {
            return true;
        }
        return false;
    }

    public boolean isSword(ItemStack itemStack) {
        if (itemStack.getType() == Material.DIAMOND_SWORD
                || itemStack.getType() == Material.IRON_SWORD
                || itemStack.getType() == Material.GOLD_SWORD
                || itemStack.getType() == Material.STONE_SWORD
                || itemStack.getType() == Material.WOOD_SWORD) return true;
        return false;
    }

    public boolean isInteractableItem(ItemStack itemStack) {

        return isSword(itemStack)
                || itemStack.getType().isEdible()
                || itemStack.getType() == Material.POTION
                || itemStack.getType() == Material.BOW;
    }
}
