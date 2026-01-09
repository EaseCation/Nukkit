package cn.nukkit.level.generator.object.tree;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.math.RandomSource;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ObjectTallBirchTree extends ObjectBirchTree {
    public ObjectTallBirchTree() {
        this(0);
    }

    public ObjectTallBirchTree(float beehiveProbability) {
        super(beehiveProbability);
    }

    @Override
    public void placeObject(ChunkManager level, int x, int y, int z, RandomSource random) {
        this.treeHeight = random.nextBoundedInt(3) + 10;
        super.placeObject(level, x, y, z, random);
    }
}
