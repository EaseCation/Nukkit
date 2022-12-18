package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.network.protocol.LevelEventPacket;

public class BlockSlabCopperCutWaxed extends BlockSlabCopperCut {
    public BlockSlabCopperCutWaxed() {
        this(0);
    }

    public BlockSlabCopperCutWaxed(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return WAXED_CUT_COPPER_SLAB;
    }

    @Override
    public String getName() {
        return isTopSlot() ? "Upper Waxed Cut Copper Slab" : "Waxed Cut Copper Slab";
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, Player player) {
        if (item.isAxe()) {
            if (player != null && !player.isCreative()) {
                item.useOn(this);
            }

            level.addLevelEvent(this, LevelEventPacket.EVENT_PARTICLE_WAX_OFF);

            level.setBlock(this, get(getDewaxedBlockId(), getDamage()), true);
            return true;
        }

        return false;
    }

    @Override
    public int onUpdate(int type) {
        return 0;
    }

    @Override
    public boolean isWaxed() {
        return true;
    }

    @Override
    protected int getDoubleSlabBlockId() {
        return WAXED_DOUBLE_CUT_COPPER_SLAB;
    }

    @Override
    protected final int getWaxedBlockId() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected int getIncrementAgeBlockId() {
        return WAXED_EXPOSED_CUT_COPPER_SLAB;
    }

    protected int getDewaxedBlockId() {
        return CUT_COPPER_SLAB;
    }
}
