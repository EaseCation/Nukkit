package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Dimension;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

import java.util.concurrent.ThreadLocalRandom;

public class BlockIceFrosted extends BlockTransparentMeta {

    public BlockIceFrosted() {
        this(0);
    }

    public BlockIceFrosted(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return FROSTED_ICE;
    }

    @Override
    public String getName() {
        return "Frosted Ice";
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public double getHardness() {
        return 0.5;
    }

    @Override
    public double getResistance() {
        return 2.5;
    }

    @Override
    public double getFrictionFactor() {
        return 0.98;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.ICE_BLOCK_COLOR;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(AIR);
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[0];
    }

    @Override
    public boolean onBreak(Item item) {
        if (level.getDimension() == Dimension.NETHER) {
            return super.onBreak(item);
        }
        return level.setBlock(this, Block.get(BlockID.WATER), true);
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (!super.place(item, block, target, face, fx, fy, fz, player)) {
            return false;
        }

        level.scheduleRandomUpdate(this, ThreadLocalRandom.current().nextInt(20, 20 * 2));
        return true;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {

//            level.scheduleRandomUpdate(this, ThreadLocalRandom.current().nextInt(20, 20 * 2));
        }

        if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            //TODO: FrostedIceBlock::_slightlyMelt

            level.scheduleRandomUpdate(this, ThreadLocalRandom.current().nextInt(20, 20 * 2));
        }

        return 0;
    }
}
