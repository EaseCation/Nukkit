package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.utils.BlockColor;

public class BlockChain extends BlockRotatedPillar {
    public BlockChain() {
        this(0);
    }

    public BlockChain(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Chain";
    }

    @Override
    public int getId() {
        return BLOCK_CHAIN;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public boolean isTransparent() {
        return true;
    }

    @Override
    public double getHardness() {
        return 5;
    }

    @Override
    public double getResistance() {
        return 15;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            return new Item[]{
                    toItem(true),
            };
        }
        return new Item[0];
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        switch (getDamage()) {
            default:
            case PILLAR_AXIS_Y:
                return new SimpleAxisAlignedBB(this.x + 6.5 / 16, this.y, this.z + 6.5 / 16, this.x + 1 - 6.5 / 16, this.y + 1, this.z + 1 - 6.5 / 16);
            case PILLAR_AXIS_X:
                return new SimpleAxisAlignedBB(this.x, this.y + 6.5 / 16, this.z + 6.5 / 16, this.x + 1, this.y + 1 - 6.5 / 16, this.z + 1 - 6.5 / 16);
            case PILLAR_AXIS_Z:
                return new SimpleAxisAlignedBB(this.x + 6.5 / 16, this.y + 6.5 / 16, this.z, this.x + 1 - 6.5 / 16, this.y + 1 - 6.5 / 16, this.z + 1);}
    }

    @Override
    public boolean canProvideSupport(BlockFace face, SupportType type) {
        return false;
    }

    @Override
    public boolean canContainWater() {
        return true;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.AIR_BLOCK_COLOR;
    }
}
