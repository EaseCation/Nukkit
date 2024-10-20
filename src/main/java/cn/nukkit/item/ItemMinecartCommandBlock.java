package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;

public class ItemMinecartCommandBlock extends Item {

    public ItemMinecartCommandBlock() {
        this(0, 1);
    }

    public ItemMinecartCommandBlock(Integer meta) {
        this(meta, 1);
    }

    public ItemMinecartCommandBlock(Integer meta, int count) {
        super(COMMAND_BLOCK_MINECART, meta, count, "Minecart with Command Block");
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Level level, Player player, Block block, Block target, BlockFace face, double fx, double fy, double fz) {
        //TODO
        return false;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
