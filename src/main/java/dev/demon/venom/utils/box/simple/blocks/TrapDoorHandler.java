package dev.demon.venom.utils.box.simple.blocks;


import dev.demon.venom.api.tinyprotocol.api.ProtocolVersion;
import dev.demon.venom.utils.box.simple.CollisionBox;
import dev.demon.venom.utils.box.simple.types.CollisionFactory;
import dev.demon.venom.utils.box.simple.types.SimpleCollisionBox;
import org.bukkit.block.Block;

public class TrapDoorHandler implements CollisionFactory {
    @Override
    public CollisionBox fetch(ProtocolVersion version, Block block) {
        byte data = block.getState().getData().getData();
        float var2 = 0.1875F;

        if ((data & 4) != 0) {
            if ((data & 3) == 0) {
                return new SimpleCollisionBox(0.0F, 0.0F, 1.0F - var2, 1.0F, 1.0F, 1.0F);
            }

            if ((data & 3) == 1) {
                return new SimpleCollisionBox(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, var2);
            }

            if ((data & 3) == 2) {
                return new SimpleCollisionBox(1.0F - var2, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
            }

            if ((data & 3) == 3) {
                return new SimpleCollisionBox(0.0F, 0.0F, 0.0F, var2, 1.0F, 1.0F);
            }
        } else {
            if ((data & 8) != 0) {
                return new SimpleCollisionBox(0.0F, 1.0F - var2, 0.0F, 1.0F, 1.0F, 1.0F);
            } else {
                return new SimpleCollisionBox(0.0F, 0.0F, 0.0F, 1.0F, var2, 1.0F);
            }
        }
        return null;
    }
}
