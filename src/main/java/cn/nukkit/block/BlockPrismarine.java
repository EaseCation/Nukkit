package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;


public class BlockPrismarine extends BlockSolidMeta {

    public static final int NORMAL = 0;
    public static final int DARK = 1;
    public static final int BRICKS = 2;

    private static final String[] NAMES = new String[]{
            "Prismarine",
            "Dark prismarine",
            "Prismarine bricks"
    };

    public BlockPrismarine() {
        this(0);
    }

    public BlockPrismarine(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return PRISMARINE;
    }

    @Override
    public float getHardness() {
        return 1.5f;
    }

    @Override
    public float getResistance() {
        return 30;
    }

    @Override
    public int getToolType() {
        return BlockToolType.PICKAXE;
    }

    @Override
    public String getName() {
        return NAMES[this.getDamage() > 2 ? 0 : this.getDamage()];
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            return new Item[]{
                    toItem(true)
            };
        } else {
            return new Item[0];
        }
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public BlockColor getColor() {
        switch (getDamage() & 0x03) {
            default:
            case NORMAL:
                return BlockColor.CYAN_BLOCK_COLOR;
            case DARK:
            case BRICKS:
                return BlockColor.DIAMOND_BLOCK_COLOR;
        }
    }
}
