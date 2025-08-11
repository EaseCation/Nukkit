package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.utils.BlockColor;

/**
 * Created on 2015/12/2 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockCobweb extends BlockFlowable {
    public BlockCobweb() {
        this(0);
    }

    public BlockCobweb(int meta) {
        super(0);
    }

    @Override
    public String getName() {
        return "Cobweb";
    }

    @Override
    public int getId() {
        return WEB;
    }

    @Override
    public float getHardness() {
        return 4;
    }

    @Override
    public float getResistance() {
        return 20;
    }

    @Override
    public int getToolType() {
        return BlockToolType.SWORD | BlockToolType.SHEARS;
    }

    @Override
    protected AxisAlignedBB recalculateCollisionBoundingBox() {
        return this;
    }

    @Override
    protected AxisAlignedBB recalculateSelectionBoundingBox() {
        return this;
    }

    @Override
    public boolean hasEntityCollision() {
        return true;
    }

    @Override
    public void onEntityCollide(Entity entity) {
        entity.resetFallDistance();
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        if (item.isShears()) {
            return new Item[]{
                    this.toItem(true)
            };
        } else if (item.isSword()) {
            return new Item[]{
                    Item.get(Item.STRING)
            };
        } else {
            return new Item[0];
        }
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WOOL_BLOCK_COLOR;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public boolean canContainWater() {
        return true;
    }
}
