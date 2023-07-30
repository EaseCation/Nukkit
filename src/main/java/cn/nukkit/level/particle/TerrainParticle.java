package cn.nukkit.level.particle;

import cn.nukkit.block.Block;
import cn.nukkit.math.Vector3;

/**
 * Created on 2015/11/21 by xtypr.
 * Package cn.nukkit.level.particle in project Nukkit .
 */
public class TerrainParticle extends GenericParticle {
    public TerrainParticle(Vector3 pos, Block block) {
        this(pos, block.getDamage(), block.getId());
    }

    public TerrainParticle(Vector3 pos, int blockId) {
        this(pos, blockId, 0);
    }

    public TerrainParticle(Vector3 pos, int blockId, int blockMeta) {
        super(pos, Particle.TERRAIN, (blockId << Block.BLOCK_META_BITS) | blockMeta);
    }
}
