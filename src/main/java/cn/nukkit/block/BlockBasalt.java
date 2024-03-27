package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

public class BlockBasalt extends BlockRotatedPillar {
    public BlockBasalt() {
        this(0);
    }

    public BlockBasalt(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return BASALT;
    }

    @Override
    public String getName() {
        return "Basalt";
    }

    @Override
    public float getHardness() {
        return 1.25f;
    }

    @Override
    public float getResistance() {
        return 21;
    }

    @Override
    public int getToolType() {
        return BlockToolType.PICKAXE;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            return new Item[]{
                    toItem(true),
            };
        }
        return new Item[0];
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.BLACK_BLOCK_COLOR;
    }
}
