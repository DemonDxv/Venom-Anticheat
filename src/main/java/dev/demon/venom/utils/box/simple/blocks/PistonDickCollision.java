package dev.demon.venom.utils.box.simple.blocks;


import dev.demon.venom.api.tinyprotocol.api.ProtocolVersion;
import dev.demon.venom.utils.box.simple.CollisionBox;
import dev.demon.venom.utils.box.simple.types.CollisionFactory;
import dev.demon.venom.utils.box.simple.types.ComplexCollisionBox;
import dev.demon.venom.utils.box.simple.types.SimpleCollisionBox;
import org.bukkit.block.Block;

public class PistonDickCollision implements CollisionFactory {
    public static final int[] offsetsXForSide = new int[]{0, 0, 0, 0, -1, 1};

    @Override
    public CollisionBox fetch(ProtocolVersion version, Block block) {
        byte data = block.getState().getData().getData();

        switch (clamp_int(data & 7, 0, offsetsXForSide.length - 1)) {
            case 0:
                return new ComplexCollisionBox(new SimpleCollisionBox(0.0F, 0.0F, 0.0F, 1.0F, 0.25F, 1.0F),
                        new SimpleCollisionBox(0.375F, 0.25F, 0.375F, 0.625F, 1.0F, 0.625F));
            case 1:
                return new ComplexCollisionBox(new SimpleCollisionBox(0.0F, 0.75F, 0.0F, 1.0F, 1.0F, 1.0F),
                        new SimpleCollisionBox(0.375F, 0.0F, 0.375F, 0.625F, 0.75F, 0.625F));
            case 2:
                return new ComplexCollisionBox(new SimpleCollisionBox(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.25F),
                        new SimpleCollisionBox(0.25F, 0.375F, 0.25F, 0.75F, 0.625F, 1.0F));
            case 3:
                return new ComplexCollisionBox(new SimpleCollisionBox(0.0F, 0.0F, 0.75F, 1.0F, 1.0F, 1.0F),
                        new SimpleCollisionBox(0.25F, 0.375F, 0.0F, 0.75F, 0.625F, 0.75F));
            case 4:
                return new ComplexCollisionBox(new SimpleCollisionBox(0.0F, 0.0F, 0.0F, 0.25F, 1.0F, 1.0F),
                        new SimpleCollisionBox(0.375F, 0.25F, 0.25F, 0.625F, 0.75F, 1.0F));
            case 5:
                return new ComplexCollisionBox(new SimpleCollisionBox(0.75F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F),
                        new SimpleCollisionBox(0.0F, 0.375F, 0.25F, 0.75F, 0.625F, 0.75F));
        }
        return null;
    }

    public static int clamp_int(int p_76125_0_, int p_76125_1_, int p_76125_2_) {
        return p_76125_0_ < p_76125_1_ ? p_76125_1_ : (p_76125_0_ > p_76125_2_ ? p_76125_2_ : p_76125_0_);
    }
}
