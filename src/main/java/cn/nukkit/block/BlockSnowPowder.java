package cn.nukkit.block;

import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBucket;
import cn.nukkit.utils.BlockColor;

public class BlockSnowPowder extends BlockFlowable {
    public BlockSnowPowder() {
        this(0);
    }

    public BlockSnowPowder(int meta) {
        super(0);
    }

    @Override
    public int getId() {
        return POWDER_SNOW;
    }

    @Override
    public String getName() {
        return "Powder Snow";
    }

    @Override
    public double getHardness() {
        return 0.25;
    }

    @Override
    public double getResistance() {
        return 1.25;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(Item.BUCKET, ItemBucket.POWDER_SNOW_BUCKET);
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[0];
    }

    @Override
    public boolean breaksWhenMoved() {
        return false;
    }

    @Override
    public boolean sticksToPiston() {
        return true;
    }

    @Override
    public boolean canBeClimbed() {
        return true;
    }

    @Override
    public boolean hasEntityCollision() {
        return true;
    }

    @Override
    public void onEntityCollide(Entity entity) {
        entity.resetFallDistance();

        //TODO
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.SNOW_BLOCK_COLOR;
    }

    @Override
    public int getFullId() {
        return getId() << BLOCK_META_BITS;
    }

    @Override
    public void setDamage(int meta) {
    }
}
