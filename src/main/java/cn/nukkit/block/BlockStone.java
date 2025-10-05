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
public class BlockStone extends BlockSolid {
    public static final int TYPE_NORMAL = 0;
    public static final int TYPE_GRANITE = 1;
    public static final int TYPE_POLISHED_GRANITE = 2;
    public static final int TYPE_DIORITE = 3;
    public static final int TYPE_POLISHED_DIORITE = 4;
    public static final int TYPE_ANDESITE = 5;
    public static final int TYPE_POLISHED_ANDESITE = 6;

    private static final String[] NAMES = new String[]{
            "Stone",
            "Granite",
            "Polished Granite",
            "Diorite",
            "Polished Diorite",
            "Andesite",
            "Polished Andesite",
            "",
    };

    public BlockStone() {
        this(0);
    }

    public BlockStone(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return STONE;
    }

    @Override
    public boolean isStackedByData() {
        return !V1_20_50.isAvailable();
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
        return NAMES[this.getDamage() & 0x07];
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            return new Item[]{
                    getDamage() == TYPE_NORMAL ? Item.get(getItemId(COBBLESTONE)) : Item.get(getItemId(), getDamage())
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
    public boolean canSilkTouch() {
        return true;
    }

    @Override
    public BlockColor getColor() {
        switch (getDamage() & 0x7) {
            default:
            case TYPE_NORMAL:
            case TYPE_ANDESITE:
            case TYPE_POLISHED_ANDESITE:
                return BlockColor.STONE_BLOCK_COLOR;
            case TYPE_GRANITE:
            case TYPE_POLISHED_GRANITE:
                return BlockColor.DIRT_BLOCK_COLOR;
            case TYPE_DIORITE:
            case TYPE_POLISHED_DIORITE:
                return BlockColor.QUARTZ_BLOCK_COLOR;
        }
    }

    @Override
    public float getFurnaceXpMultiplier() {
        if (getDamage() != TYPE_NORMAL) {
            return 0;
        }
        return 0.1f;
    }
}
