package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.event.block.BlockRedstoneEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;

/**
 * @author Nukkit Project Team
 */
public class BlockLever extends BlockTransparent implements Faceable {

    public static final int LEVER_DIRECTION_MASK = 0b111;
    public static final int OPEN_BIT = 0b1000;

    public BlockLever() {
        this(0);
    }

    public BlockLever(int meta) {
        super(meta);
    }

    @Override
    public boolean canPassThrough() {
        return true;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public boolean breaksWhenMoved() {
        return true;
    }

    @Override
    public boolean sticksToPiston() {
        return false;
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        return null;
    }

    @Override
    public String getName() {
        return "Lever";
    }

    @Override
    public int getId() {
        return LEVER;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public float getHardness() {
        return 0.5f;
    }

    @Override
    public float getResistance() {
        return 2.5f;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(this.getItemId());
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        return new Item[]{toItem(true)};
    }

    public boolean isPowerOn() {
        return (this.getDamage() & OPEN_BIT) == OPEN_BIT;
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, float fx, float fy, float fz, Player player) {
        this.level.getServer().getPluginManager().callEvent(new BlockRedstoneEvent(this, isPowerOn() ? 15 : 0, isPowerOn() ? 0 : 15));
        this.setDamage(this.getDamage() ^ OPEN_BIT);

        boolean redstone = this.level.isRedstoneEnabled();

        this.getLevel().setBlock(this, this, true, true);
        this.getLevel().addLevelSoundEvent(this.blockCenter(), isPowerOn() ? LevelSoundEventPacket.SOUND_BUTTON_CLICK_ON : LevelSoundEventPacket.SOUND_BUTTON_CLICK_OFF, getFullId());

        if (redstone) {
            BlockFace facing = getBlockFace();
            Block target = this.getSide(facing.getOpposite());
            target.onUpdate(Level.BLOCK_UPDATE_REDSTONE);

            this.level.updateAroundRedstone(this, isPowerOn() ? facing.getOpposite() : null);
            this.level.updateAroundRedstone(target, isPowerOn() ? facing : null);
        }
        return true;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            BlockFace face = getBlockFace();
            if (!SupportType.hasFullSupport(getSide(face.getOpposite()), face)) {
                this.level.useBreakOn(this, true);
                return Level.BLOCK_UPDATE_NORMAL;
            }
        }
        return 0;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        if (!SupportType.hasFullSupport(target, face)) {
            return false;
        }

        this.setDamage(LeverOrientation.forFacings(face, player.getHorizontalFacing()).getMetadata());
        this.getLevel().setBlock(block, this, true, true);
        return true;
    }

    @Override
    public boolean onBreak(Item item, Player player) {
        this.getLevel().setBlock(this, Block.get(BlockID.AIR), true, true);

        if (isPowerOn()) {
            this.level.updateAround(this.getSideVec(getBlockFace().getOpposite()));
        }
        return true;
    }

    @Override
    public int getWeakPower(BlockFace side) {
        return isPowerOn() ? 15 : 0;
    }

    public int getStrongPower(BlockFace side) {
        return !isPowerOn() ? 0 : getBlockFace() == side ? 15 : 0;
    }

    @Override
    public boolean isPowerSource() {
        return true;
    }

    public enum LeverOrientation {
        DOWN_X(0, "down_east_west", BlockFace.DOWN),
        EAST(1, "east", BlockFace.EAST),
        WEST(2, "west", BlockFace.WEST),
        SOUTH(3, "south", BlockFace.SOUTH),
        NORTH(4, "north", BlockFace.NORTH),
        UP_Z(5, "up_north_south", BlockFace.UP),
        UP_X(6, "up_east_west", BlockFace.UP),
        DOWN_Z(7, "down_north_south", BlockFace.DOWN);

        private static final LeverOrientation[] META_LOOKUP = new LeverOrientation[values().length];
        private final int meta;
        private final String name;
        private final BlockFace facing;

        LeverOrientation(int meta, String name, BlockFace face) {
            this.meta = meta;
            this.name = name;
            this.facing = face;
        }

        public int getMetadata() {
            return this.meta;
        }

        public BlockFace getFacing() {
            return this.facing;
        }

        public String toString() {
            return this.name;
        }

        public static LeverOrientation byMetadata(int meta) {
            if (meta < 0 || meta >= META_LOOKUP.length) {
                meta = 0;
            }

            return META_LOOKUP[meta];
        }

        public static LeverOrientation forFacings(BlockFace clickedSide, BlockFace playerDirection) {
            switch (clickedSide) {
                case DOWN:
                    switch (playerDirection.getAxis()) {
                        case X:
                            return DOWN_X;

                        case Z:
                            return DOWN_Z;

                        default:
                            throw new IllegalArgumentException("Invalid entityFacing " + playerDirection + " for facing " + clickedSide);
                    }

                case UP:
                    switch (playerDirection.getAxis()) {
                        case X:
                            return UP_X;

                        case Z:
                            return UP_Z;

                        default:
                            throw new IllegalArgumentException("Invalid entityFacing " + playerDirection + " for facing " + clickedSide);
                    }

                case NORTH:
                    return NORTH;

                case SOUTH:
                    return SOUTH;

                case WEST:
                    return WEST;

                case EAST:
                    return EAST;

                default:
                    throw new IllegalArgumentException("Invalid facing: " + clickedSide);
            }
        }

        public String getName() {
            return this.name;
        }

        static {
            for (LeverOrientation face : values()) {
                META_LOOKUP[face.getMetadata()] = face;
            }
        }
    }

    @Override
    public BlockFace getBlockFace() {
        return LeverOrientation.byMetadata(this.getDamage() & LEVER_DIRECTION_MASK).getFacing();
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.AIR_BLOCK_COLOR;
    }

    @Override
    public boolean canContainWater() {
        return true;
    }

    @Override
    public boolean canContainFlowingWater() {
        return true;
    }

    @Override
    public boolean canProvideSupport(BlockFace face, SupportType type) {
        return false;
    }
}
