package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.utils.BlockColor;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BlockWall extends BlockTransparentMeta {

    public static final int NORMAL_COBBLESTONE_WALL = 0;
    public static final int MOSSY_COBBLESTONE_WALL = 1;
    public static final int GRANITE_WALL = 2;
    public static final int DIORITE_WALL = 3;
    public static final int ANDESITE_WALL = 4;
    public static final int SANDSTONE_WALL = 5;
    public static final int BRICK_WALL = 6;
    public static final int STONE_BRICK_WALL = 7;
    public static final int MOSSY_STONE_BRICK_WALL = 8;
    public static final int NETHER_BRICK_WALL = 9;
    public static final int END_BRICK_WALL = 10;
    public static final int PRISMARINE_WALL = 11;
    public static final int RED_SANDSTONE_WALL = 12;
    public static final int RED_NETHER_BRICK_WALL = 13;

    private static final String[] NAMES = new String[]{
            "Cobblestone Wall",
            "Mossy Cobblestone Wall",
            "Granite Wall",
            "Diorite Wall",
            "Andesite Wall",
            "Sandstone Wall",
            "Brick Wall",
            "Stone Brick Wall",
            "Mossy Stone Brick Wall",
            "Nether Brick Wall",
            "End Stone Brick Wall",
            "Prismarine Wall",
            "Red Sandstone Wall",
            "Red Nether Brick Wall",
            "Wall",
            "Wall",
    };

    //TODO: connections 1.16.0

    public BlockWall() {
        this(0);
    }

    public BlockWall(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return COBBLESTONE_WALL;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return 30;
    }

    @Override
    public String getName() {
        return NAMES[this.getDamage() & 0xf];
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        boolean north = this.canConnect(this.getSide(BlockFace.NORTH), BlockFace.SOUTH);
        boolean south = this.canConnect(this.getSide(BlockFace.SOUTH), BlockFace.NORTH);
        boolean west = this.canConnect(this.getSide(BlockFace.WEST), BlockFace.EAST);
        boolean east = this.canConnect(this.getSide(BlockFace.EAST), BlockFace.WEST);

        double n = north ? 0 : 0.25;
        double s = south ? 1 : 0.75;
        double w = west ? 0 : 0.25;
        double e = east ? 1 : 0.75;

        if (north && south && !west && !east) {
            w = 0.3125;
            e = 0.6875;
        } else if (!north && !south && west && east) {
            n = 0.3125;
            s = 0.6875;
        }

        return new SimpleAxisAlignedBB(
                this.x + w,
                this.y,
                this.z + n,
                this.x + e,
                this.y + 1.5,
                this.z + s
        );
    }

    public boolean canConnect(Block block, BlockFace face) {
        return block.isWall() || block.isFenceGate() || SupportType.hasFullSupport(block, face);
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public boolean canContainWater() {
        return true;
    }

    @Override
    public boolean canProvideSupport(BlockFace face, SupportType type) {
        return face.isVertical() && type == SupportType.CENTER;
    }

    @Override
    public boolean isWall() {
        return true;
    }

    @Override
    public BlockColor getColor() {
        switch (getDamage()) {
            default:
            case NORMAL_COBBLESTONE_WALL:
            case MOSSY_COBBLESTONE_WALL:
            case ANDESITE_WALL:
            case STONE_BRICK_WALL:
            case MOSSY_STONE_BRICK_WALL:
                return BlockColor.STONE_BLOCK_COLOR;
            case GRANITE_WALL:
                return BlockColor.DIRT_BLOCK_COLOR;
            case DIORITE_WALL:
                return BlockColor.QUARTZ_BLOCK_COLOR;
            case SANDSTONE_WALL:
            case END_BRICK_WALL:
                return BlockColor.SAND_BLOCK_COLOR;
            case BRICK_WALL:
                return BlockColor.RED_BLOCK_COLOR;
            case NETHER_BRICK_WALL:
            case RED_NETHER_BRICK_WALL:
                return BlockColor.NETHER_BLOCK_COLOR;
            case PRISMARINE_WALL:
                return BlockColor.CYAN_BLOCK_COLOR;
            case RED_SANDSTONE_WALL:
                return BlockColor.ORANGE_BLOCK_COLOR;
        }
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(getItemId(), getDamage() & 0xf);
    }
}
