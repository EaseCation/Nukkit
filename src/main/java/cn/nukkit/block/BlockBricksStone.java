package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

import static cn.nukkit.GameVersion.*;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BlockBricksStone extends BlockSolid {
    public static final int NORMAL = 0;
    public static final int MOSSY = 1;
    public static final int CRACKED = 2;
    public static final int CHISELED = 3;
    public static final int SMOOTH = 4;

    private static final String[] NAMES = new String[]{
            "Stone Bricks",
            "Mossy Stone Bricks",
            "Cracked Stone Bricks",
            "Chiseled Stone Bricks",
            "Smooth Stone Bricks",
    };

    public BlockBricksStone() {
        this(0);
    }

    public BlockBricksStone(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return STONEBRICK;
    }

    @Override
    public boolean isStackedByData() {
        return !V1_21_20.isAvailable();
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
    public String getName() {
        return NAMES[this.getDamage() & 0x03];
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            return new Item[]{
                    Item.get(getItemId(), this.getDamage() & 0x03, 1)
            };
        } else {
            return new Item[0];
        }
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
    public BlockColor getColor() {
        return BlockColor.STONE_BLOCK_COLOR;
    }

    @Override
    public float getFurnaceXpMultiplier() {
        if (getDamage() != CRACKED) {
            return 0;
        }
        return 0.1f;
    }
}
