package dev.demon.venom.utils.box.simple.blocks;


import dev.demon.venom.api.tinyprotocol.api.ProtocolVersion;
import dev.demon.venom.utils.box.simple.CollisionBox;
import dev.demon.venom.utils.box.simple.types.CollisionFactory;
import dev.demon.venom.utils.box.simple.types.SimpleCollisionBox;
import org.bukkit.block.Block;

@SuppressWarnings("Duplicates")
public class DynamicRod implements CollisionFactory {

    public static final CollisionBox UD = new SimpleCollisionBox(0.4375,0, 0.4375, 0.5625, 1, 0.625);
    public static final CollisionBox EW = new SimpleCollisionBox(0,0.4375, 0.4375, 1, 0.5625, 0.625);
    public static final CollisionBox NS = new SimpleCollisionBox(0.4375, 0.4375, 0, 0.5625, 0.625, 1);

    @Override
    public CollisionBox fetch(ProtocolVersion version, Block b) {
        switch (b.getData()) {
            case 0:
            case 1:
            default:
                return UD.copy();
            case 2:
            case 3:
                return NS.copy();
            case 4:
            case 5:
                return EW.copy();
        }
    }

}
