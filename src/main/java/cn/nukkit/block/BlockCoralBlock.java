package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;

import java.util.concurrent.ThreadLocalRandom;

public abstract class BlockCoralBlock extends BlockSolid {
    public static final int[] CORAL_BLOCKS = {
            TUBE_CORAL_BLOCK,
            BRAIN_CORAL_BLOCK,
            BUBBLE_CORAL_BLOCK,
            FIRE_CORAL_BLOCK,
            HORN_CORAL_BLOCK,
            TUBE_CORAL_BLOCK,
            TUBE_CORAL_BLOCK,
            TUBE_CORAL_BLOCK,
            DEAD_TUBE_CORAL_BLOCK,
            DEAD_BRAIN_CORAL_BLOCK,
            DEAD_BUBBLE_CORAL_BLOCK,
            DEAD_FIRE_CORAL_BLOCK,
            DEAD_HORN_CORAL_BLOCK,
    };
    public static final int[] DEAD_CORAL_BLOCKS = {
            DEAD_TUBE_CORAL_BLOCK,
            DEAD_BRAIN_CORAL_BLOCK,
            DEAD_BUBBLE_CORAL_BLOCK,
            DEAD_FIRE_CORAL_BLOCK,
            DEAD_HORN_CORAL_BLOCK,
    };

    @Deprecated
    public static final int COLOR_MASK = 0b111;
    @Deprecated
    public static final int DEAD_BIT = 0b1000;

    public static final int BLUE = 0;
    public static final int PINK = 1;
    public static final int PURPLE = 2;
    public static final int RED = 3;
    public static final int YELLOW = 4;

    private static final String[] NAMES = new String[]{
            "Tube Coral Block",
            "Brain Coral Block",
            "Bubble Coral Block",
            "Fire Coral Block",
            "Horn Coral Block",
            "Coral Block",
            "Coral Block",
            "Coral Block",
    };

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
    public Item toItem(boolean addUserData) {
        return Item.get(getItemId());
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        if (!super.place(item, block, target, face, fx, fy, fz, player)) {
            return false;
        }

        level.scheduleUpdate(this, ThreadLocalRandom.current().nextInt(40, 50));
        return true;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            level.scheduleUpdate(this, ThreadLocalRandom.current().nextInt(40, 50));
            return type;
        }

        if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            for (BlockFace face : BlockFace.getValues()) {
                Block block = getSide(face);
                if (block.isWater() || level.getExtraBlock(block).isWater()) {
                    return 0;
                }
            }

            level.setBlock(this, get(getDeadBlockId()), true);
            return type;
        }

        return 0;
    }

    @Override
    public boolean isCoralBlock() {
        return true;
    }

    protected abstract int getDeadBlockId();
}
