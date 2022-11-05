package cn.nukkit.block;

import cn.nukkit.entity.Entity;

public class BlockCauldronLava extends BlockCauldron {

    public BlockCauldronLava() {
        this(0);
    }

    public BlockCauldronLava(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return LAVA_CAULDRON;
    }

    @Override
    public int getLightLevel() {
        return 15;
    }

    @Override
    public void onEntityCollide(Entity entity) {
        //TODO
    }
}
