package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

import static cn.nukkit.GameVersion.*;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BlockQuartz extends BlockSolid {

    public static final int NORMAL = 0;
    public static final int CHISELED = 1;
    public static final int PILLAR = 2;
    public static final int SMOOTH = 3;

    public static final int TYPE_MASK = 0b11;

    private static final String[] NAMES = new String[]{
            "Block of Quartz",
            "Chiseled Quartz Block",
            "Quartz Pillar",
            "Smooth Quartz Block",
    };

    private static final short[] FACES = new short[]{
            0,
            0,
            0b1000,
            0b1000,
            0b0100,
            0b0100
    };

    public BlockQuartz() {
        this(0);
    }

    public BlockQuartz(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return QUARTZ_BLOCK;
    }

    @Override
    public boolean isStackedByData() {
        return !V1_21_20.isAvailable();
    }

    @Override
    public float getHardness() {
        if (V1_21_20.isAvailable() && getQuartzType() == SMOOTH) {
            return 2;
        }
        return 0.8f;
    }

    @Override
    public float getResistance() {
        if (getQuartzType() == SMOOTH) {
            return 30;
        }
        return 4;
    }

    @Override
    public String getName() {
        return NAMES[this.getQuartzType()];
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        if (this.getDamage() != NORMAL) {
            this.setDamage(this.getQuartzType() | FACES[face.getIndex()]);
        }
        this.getLevel().setBlock(block, this, true, true);

        return true;
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
        return Item.get(getItemId(), this.getQuartzType());
    }

    @Override
    public int getToolType() {
        return BlockToolType.PICKAXE;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.QUARTZ_BLOCK_COLOR;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public float getFurnaceXpMultiplier() {
        if (getQuartzType() != SMOOTH) {
            return 0;
        }
        return 0.1f;
    }

    public int getQuartzType() {
        return getDamage() & TYPE_MASK;
    }
}
