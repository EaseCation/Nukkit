package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

public abstract class BlockCoralBlockDead extends BlockCoralBlock {
    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        return level.setBlock(this, this, true);
    }

    @Override
    public int onUpdate(int type) {
        return 0;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.GRAY_BLOCK_COLOR;
    }

    @Override
    protected int getDeadBlockId() {
        return getId();
    }

    @Override
    public boolean isDeadCoral() {
        return true;
    }
}
