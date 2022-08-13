package cn.nukkit.level.sound;

import cn.nukkit.block.Block;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.LevelSoundEventPacket;

/**
 * @author CreeperFace
 */
public class BlockPlaceSound extends LevelSoundEventSound {
    public BlockPlaceSound(Vector3 pos, int blockId, int meta) {
        super(pos, LevelSoundEventPacket.SOUND_PLACE, (blockId << Block.BLOCK_META_BITS) | meta, 1);
    }
}