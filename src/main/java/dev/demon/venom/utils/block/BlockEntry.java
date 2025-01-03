package dev.demon.venom.utils.block;

import dev.demon.venom.utils.box.BoundingBox;

import lombok.Getter;
import org.bukkit.block.Block;


/**
 * Created on 06/06/2020 Package me.jumba.sparky.util.block
 */
@Getter
public class BlockEntry {

    private Block block;
    private BoundingBox boundingBox;
    private boolean inCombat;

    public BlockEntry(Block block, BoundingBox boundingBox, boolean inCombat) {
        this.block = block;
        this.boundingBox = boundingBox;
        this.inCombat = inCombat;
    }
}