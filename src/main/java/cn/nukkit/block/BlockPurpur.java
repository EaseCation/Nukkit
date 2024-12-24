package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

public class BlockPurpur extends BlockSolid {

    public static final int NORMAL = 0;
    public static final int CHISELED = 1;
    public static final int PILLAR = 2;
    public static final int SMOOTH = 3;

    public static final int TYPE_MASK = 0b11;

    private static final String[] NAMES = new String[]{
            "Purpur Block",
            "Purpur Block",
            "Purpur Pillar",
            "Purpur Block"
    };

    private static final short[] FACES = new short[]{
            0,
            0,
            0b1000,
            0b1000,
            0b0100,
            0b0100
    };

    public BlockPurpur() {
        this(0);
    }

    public BlockPurpur(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return NAMES[this.getDamage() & TYPE_MASK];
    }

    @Override
    public int getId() {
        return PURPUR_BLOCK;
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
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        if (this.getDamage() != NORMAL) {
            this.setDamage(((this.getDamage() & TYPE_MASK) | FACES[face.getIndex()]));
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
        return Item.get(getItemId(), this.getDamage() & TYPE_MASK);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.MAGENTA_BLOCK_COLOR;
    }
}
