package dev.demon.venom.utils.box.simple.types;

import dev.demon.venom.api.tinyprotocol.api.ProtocolVersion;
import dev.demon.venom.utils.box.simple.CollisionBox;
import org.bukkit.block.Block;

public interface CollisionFactory {
    CollisionBox fetch(ProtocolVersion version, Block block);
}