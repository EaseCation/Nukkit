package cn.nukkit.level.generator.object.tree;

import cn.nukkit.block.Block;

/**
 * @author FlamingKnight
 */
public class ObjectCrimsonTree extends ObjectNetherTree {

    @Override
    public int getTrunkBlock() {
        return Block.CRIMSON_STEM;
    }

    @Override
    public int getLeafBlock() {
        return Block.NETHER_WART_BLOCK;
    }
}