package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockFace.Plane;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.utils.BlockColor;

import java.util.concurrent.ThreadLocalRandom;

public class BlockChorusPlant extends BlockFlowable {

    public BlockChorusPlant() {
        this(0);
    }

    public BlockChorusPlant(int meta) {
        super(0);
    }

    @Override
    public int getId() {
        return CHORUS_PLANT;
    }

    @Override
    public String getName() {
        return "Chorus Plant";
    }

    @Override
    public float getHardness() {
        return 0.4f;
    }

    @Override
    public float getResistance() {
        return 2;
    }

    @Override
    public int getToolType() {
        return BlockToolType.AXE | BlockToolType.SWORD;
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        return ThreadLocalRandom.current().nextBoolean() ? new Item[]{Item.get(ItemID.CHORUS_FRUIT, 0, 1)} : new Item[0];
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.PURPLE_BLOCK_COLOR;
    }

    @Override
    public boolean isVegetation() {
        return true;
    }

    @Override
    public boolean canPassThrough() {
        return false;
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        return new SimpleAxisAlignedBB(
                connectsTo(getSide(BlockFace.WEST)) ? x : x + 2 / 16.0,
                connectsTo(getSide(BlockFace.DOWN)) ? y : y + 2 / 16.0,
                connectsTo(getSide(BlockFace.NORTH)) ? z : z + 2 / 16.0,
                connectsTo(getSide(BlockFace.EAST)) ? x + 1 : x + 1 - 2 / 16.0,
                connectsTo(getSide(BlockFace.UP)) ? y + 1 : y + 1 - 2 / 16.0,
                connectsTo(getSide(BlockFace.SOUTH)) ? z + 1 : z + 1 - 2 / 16.0
        );
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        if (block.isLiquid() || !block.isAir() && block.canContainWater() && level.getExtraBlock(this).isWater()) {
            return false;
        }

        if (!canSurvive()) {
            return false;
        }

        return super.place(item, block, target, face, fx, fy, fz, player);
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            level.scheduleUpdate(this, 1);
            return type;
        }

        if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            if (canSurvive()) {
                return 0;
            }

            level.useBreakOn(this, true);
            return Level.BLOCK_UPDATE_NORMAL;
        }

        return 0;
    }

    private boolean canSurvive() {
        Block below = down();
        boolean emptyVertical = below.isAir() || up().isAir();

        for (BlockFace face : Plane.HORIZONTAL) {
            Block neighbor = getSide(face);
            if (!canGrowChorus(neighbor)) {
                continue;
            }
            if (!emptyVertical) {
                return false;
            }
            if (canGrowChorus(neighbor.down())) {
                return true;
            }
        }

        return canGrowChorus(below);
    }

    private static boolean connectsTo(Block block) {
        return canGrowChorus(block) || block.getId() == CHORUS_FLOWER;
    }

    static boolean canGrowChorus(Block block) {
        int id = block.getId();
        return id == CHORUS_PLANT || id == END_STONE;
    }
}
