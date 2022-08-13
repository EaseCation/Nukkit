package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;

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
    public boolean onActivate(Item item, BlockFace face, Player player) {
        //TODO
        return true;
    }
}
