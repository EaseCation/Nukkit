package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BlockQuartz extends BlockSolidMeta {

    public static final int QUARTZ_NORMAL = 0;
    public static final int QUARTZ_CHISELED = 1;
    public static final int QUARTZ_PILLAR = 2;
    public static final int SMOOTH_QUARTZ = 3;

    public static final int TYPE_MASK = 0b11;

    private static final String[] NAMES = new String[]{
            "Block of Quartz",
            "Chiseled Quartz Block",
            "Pillar Quartz Block",
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
    public float getHardness() {
        return 0.8f;
    }

    @Override
    public float getResistance() {
        return 4;
    }

    @Override
    public String getName() {
        return NAMES[this.getDamage() & 0x03];
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (this.getDamage() != QUARTZ_NORMAL) {
            this.setDamage(((this.getDamage() & 0x03) | FACES[face.getIndex()]));
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
        return Item.get(getItemId(), this.getDamage() & 0x03);
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
        if (getDamage() != SMOOTH_QUARTZ) {
            return 0;
        }
        return 0.1f;
    }
}
