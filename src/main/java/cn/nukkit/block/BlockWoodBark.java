package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.utils.BlockColor;

public class BlockWoodBark extends BlockSolidMeta {

    public static final int OAK = 0;
    public static final int SPRUCE = 1;
    public static final int BIRCH = 2;
    public static final int JUNGLE = 3;
    public static final int ACACIA = 4;
    public static final int DARK_OAK = 5;

    public static final int TYPE_MASK = 0b111;
    public static final int STRIPPED_BIT = 0b1000;

    private static final String[] NAMES = new String[]{
            "Oak Wood",
            "Spruce Wood",
            "Birch Wood",
            "Jungle Wood",
            "Acacia Wood",
            "Dark Oak Wood",
            "Wood",
            "Wood",
    };

    public BlockWoodBark() {
        this(0);
    }

    public BlockWoodBark(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return WOOD;
    }

    @Override
    public String getName() {
        return (isStripped() ? "Stripped " : "") + NAMES[getDamage() & TYPE_MASK];
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return 10;
    }

    @Override
    public int getBurnChance() {
        return 5;
    }

    @Override
    public int getBurnAbility() {
        return 5;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(getItemId(), getDamage() & 0xf);
    }

    @Override
    public BlockColor getColor() {
        switch (getDamage() & TYPE_MASK) {
            default:
            case OAK:
                return BlockColor.WOOD_BLOCK_COLOR;
            case SPRUCE:
                return BlockColor.PODZOL_BLOCK_COLOR;
            case BIRCH:
                return BlockColor.SAND_BLOCK_COLOR;
            case JUNGLE:
                return BlockColor.DIRT_BLOCK_COLOR;
            case ACACIA:
                return BlockColor.ORANGE_BLOCK_COLOR;
            case DARK_OAK:
                return BlockColor.BROWN_BLOCK_COLOR;
        }
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, Player player) {
        if (isStripped() || !item.isAxe()) {
            return false;
        }

        if (player != null && !player.isCreative()) {
            item.useOn(this);
        }

        level.addLevelSoundEvent(blockCenter(), LevelSoundEventPacket.SOUND_ITEM_USE_ON, getFullId());

        setDamage(getDamage() | STRIPPED_BIT);
        level.setBlock(this, this, true);
        return true;
    }

    public boolean isStripped() {
        return (getDamage() & STRIPPED_BIT) == STRIPPED_BIT;
    }

    @Override
    public boolean isLog() {
        return true;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        return level.setBlock(this, this, true);
    }
}