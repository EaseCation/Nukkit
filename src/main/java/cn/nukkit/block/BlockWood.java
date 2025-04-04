package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.utils.BlockColor;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@Deprecated
public class BlockWood extends BlockSolid {
    public static final int[] LOGS = {
            OAK_LOG,
            SPRUCE_LOG,
            BIRCH_LOG,
            JUNGLE_LOG,
    };

    public static final int OAK = 0;
    public static final int SPRUCE = 1;
    public static final int BIRCH = 2;
    public static final int JUNGLE = 3;

    public static final int TYPE_MASK = 0b11;
    public static final int PILLAR_X_AXIS_BIT = 0b100;
    public static final int PILLAR_Z_AXIS_BIT = 0b1000;
    public static final int PILLAR_AXIS_MASK = PILLAR_X_AXIS_BIT | PILLAR_Z_AXIS_BIT;

    private static final String[] NAMES = new String[]{
            "Oak Log",
            "Spruce Log",
            "Birch Log",
            "Jungle Log",
    };

    private static final short[] FACES = new short[]{
            0,
            0,
            0b1000,
            0b1000,
            0b0100,
            0b0100
    };

    public BlockWood() {
        this(0);
    }

    public BlockWood(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return LOG;
    }

    @Override
    public float getHardness() {
        return 2;
    }

    @Override
    public float getResistance() {
        return 10;
    }

    @Override
    public String getName() {
        return NAMES[this.getLogType()];
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
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        this.setDamage(this.getLogType() | FACES[face.getIndex()]);
        this.getLevel().setBlock(block, this, true, true);

        return true;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(this.getItemId(), this.getLogType());
    }

    @Override
    public int getToolType() {
        return BlockToolType.AXE;
    }

    @Override
    public BlockColor getColor() {
        if ((getDamage() & PILLAR_AXIS_MASK) != 0) {
            switch (getLogType()) {
                default:
                case OAK:
                    return BlockColor.PODZOL_BLOCK_COLOR;
                case SPRUCE:
                    return BlockColor.BROWN_BLOCK_COLOR;
                case BIRCH:
                    return BlockColor.QUARTZ_BLOCK_COLOR;
                case JUNGLE:
                    return BlockColor.PODZOL_BLOCK_COLOR;
            }
        }
        switch (getLogType()) {
            default:
            case OAK:
                return BlockColor.WOOD_BLOCK_COLOR;
            case SPRUCE:
                return BlockColor.PODZOL_BLOCK_COLOR;
            case BIRCH:
                return BlockColor.SAND_BLOCK_COLOR;
            case JUNGLE:
                return BlockColor.DIRT_BLOCK_COLOR;
        }
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, float fx, float fy, float fz, Player player) {
        if (!item.isAxe()) {
            return false;
        }

        if (player != null) {
            player.swingArm();
            if (player.isSurvivalLike() && item.hurtAndBreak(1) < 0) {
                item.pop();
                player.level.addLevelSoundEvent(player, LevelSoundEventPacket.SOUND_BREAK);
            }
        }

        level.addLevelSoundEvent(blockCenter(), LevelSoundEventPacket.SOUND_ITEM_USE_ON, getFullId());

        level.setBlock(this, getStrippedBlock(), true);
        return true;
    }

    @Override
    public int getFuelTime() {
        return 300;
    }

    protected Block getStrippedBlock() {
        int meta = getDamage();

        switch (meta & PILLAR_AXIS_MASK) {
            case PILLAR_X_AXIS_BIT:
                meta = BlockLogStripped.PILLAR_AXIS_X;
                break;
            case PILLAR_Z_AXIS_BIT:
                meta = BlockLogStripped.PILLAR_AXIS_Z;
                break;
            case PILLAR_AXIS_MASK:
                return get(WOOD, getWoodMeta());
            default:
                meta = BlockLogStripped.PILLAR_AXIS_Y;
                break;
        }

        return get(getStrippedLogId(), meta);
    }

    protected int getStrippedLogId() {
        switch (getLogType()) {
            default:
            case OAK:
                return STRIPPED_OAK_LOG;
            case SPRUCE:
                return STRIPPED_SPRUCE_LOG;
            case BIRCH:
                return STRIPPED_BIRCH_LOG;
            case JUNGLE:
                return STRIPPED_JUNGLE_LOG;
        }
    }

    protected int getWoodMeta() {
        return getLogType();
    }

    public int getLogType() {
        return getDamage() & TYPE_MASK;
    }

    @Override
    public boolean isLog() {
        return true;
    }
}
