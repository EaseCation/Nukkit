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
public class BlockSandstone extends BlockSolidMeta {
    public static final int NORMAL = 0;
    public static final int CHISELED = 1;
    public static final int CUT = 2;
    public static final int SMOOTH = 3;

    public static final int TYPE_MASK = 0b11;

    private static final String[] NAMES = new String[]{
            "Sandstone",
            "Chiseled Sandstone",
            "Cut Sandstone",
            "Smooth Sandstone",
    };

    public BlockSandstone() {
        this(0);
    }

    public BlockSandstone(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return SANDSTONE;
    }

    @Override
    public float getHardness() {
        if (V1_21_20.isAvailable() && getSandstoneType() == SMOOTH) {
            return 2;
        }
        return 0.8f;
    }

    @Override
    public float getResistance() {
        if (getSandstoneType() == SMOOTH) {
            return 30;
        }
        return 4;
    }

    @Override
    public String getName() {
        return NAMES[this.getSandstoneType()];
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
    public Item toItem(boolean addUserData) {
        return Item.get(this.getItemId(), this.getSandstoneType());
    }

    @Override
    public int getToolType() {
        return BlockToolType.PICKAXE;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.SAND_BLOCK_COLOR;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public float getFurnaceXpMultiplier() {
        if (getSandstoneType() != SMOOTH) {
            return 0;
        }
        return 0.1f;
    }

    public int getSandstoneType() {
        return getDamage() & TYPE_MASK;
    }
}
