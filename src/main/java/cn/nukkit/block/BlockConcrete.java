package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

/**
 * Created by CreeperFace on 2.6.2017.
 */
public class BlockConcrete extends BlockSolidMeta {

    public BlockConcrete() {
        this(0);
    }

    public BlockConcrete(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return CONCRETE;
    }

    @Override
    public float getResistance() {
        return 9;
    }

    @Override
    public float getHardness() {
        return 1.8f;
    }

    @Override
    public String getName() {
        return "Concrete";
    }

    @Override
    public int getToolType() {
        return BlockToolType.PICKAXE;
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        return item.getTier() >= ItemTool.TIER_WOODEN ? new Item[]{toItem(true)} : new Item[0];
    }

    @Override
    public BlockColor getColor() {
        return DyeColor.getByWoolData(getDamage()).getColor();
    }

    public DyeColor getDyeColor() {
        return DyeColor.getByWoolData(getDamage());
    }
}
