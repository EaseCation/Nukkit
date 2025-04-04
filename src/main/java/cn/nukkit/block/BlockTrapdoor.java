package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.event.block.BlockRedstoneEvent;
import cn.nukkit.event.block.DoorToggleEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;

/**
 * Created by Pub4Game on 26.12.2015.
 */
public class BlockTrapdoor extends BlockTransparent implements Faceable {

    public static final int DIRECTION_MASK = 0b11;
    public static final int TRAPDOOR_TOP_BIT = 0x04;
    public static final int TRAPDOOR_OPEN_BIT = 0x08;

    public BlockTrapdoor() {
        this(0);
    }

    public BlockTrapdoor(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return TRAPDOOR;
    }

    @Override
    public String getName() {
        return "Oak Trapdoor";
    }

    @Override
    public float getHardness() {
        return 3;
    }

    @Override
    public float getResistance() {
        return 15;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public int getToolType() {
        return BlockToolType.AXE;
    }

    private static final AxisAlignedBB[] boundingBoxDamage = new AxisAlignedBB[16];

    static {
        for (int damage = 0; damage < 16; damage++) {
            AxisAlignedBB bb;
            double f = 0.1875;
            if ((damage & TRAPDOOR_TOP_BIT) > 0) {
                bb = new SimpleAxisAlignedBB(
                        0,
                        1 - f,
                        0,
                        1,
                        1,
                        1
                );
            } else {
                bb = new SimpleAxisAlignedBB(
                        0,
                        0,
                        0,
                        1,
                        0 + f,
                        1
                );
            }
            if ((damage & TRAPDOOR_OPEN_BIT) > 0) {
                if ((damage & DIRECTION_MASK) == 0) {
                    bb.setBounds(
                            0,
                            0,
                            1 - f,
                            1,
                            1,
                            1
                    );
                } else if ((damage & DIRECTION_MASK) == 1) {
                    bb.setBounds(
                            0,
                            0,
                            0,
                            1,
                            1,
                            0 + f
                    );
                }
                if ((damage & DIRECTION_MASK) == 2) {
                    bb.setBounds(
                            1 - f,
                            0,
                            0,
                            1,
                            1,
                            1
                    );
                }
                if ((damage & DIRECTION_MASK) == 3) {
                    bb.setBounds(
                            0,
                            0,
                            0,
                            0 + f,
                            1,
                            1
                    );
                }
            }
            boundingBoxDamage[damage] = bb;
        }
    }

    private AxisAlignedBB getRelativeBoundingBox() {
        return boundingBoxDamage[this.getDamage()];
    }

    @Override
    public double getMinX() {
        return this.x + getRelativeBoundingBox().getMinX();
    }

    @Override
    public double getMaxX() {
        return this.x + getRelativeBoundingBox().getMaxX();
    }

    @Override
    public double getMinY() {
        return this.y + getRelativeBoundingBox().getMinY();
    }

    @Override
    public double getMaxY() {
        return this.y + getRelativeBoundingBox().getMaxY();
    }

    @Override
    public double getMinZ() {
        return this.z + getRelativeBoundingBox().getMinZ();
    }

    @Override
    public double getMaxZ() {
        return this.z + getRelativeBoundingBox().getMaxZ();
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_REDSTONE && this.level.isRedstoneEnabled()) {
            if ((!isOpen() && this.level.isBlockPowered(this)) || (isOpen() && !this.level.isBlockPowered(this))) {
                this.level.getServer().getPluginManager().callEvent(new BlockRedstoneEvent(this, isOpen() ? 15 : 0, isOpen() ? 0 : 15));
                this.setDamage(this.getDamage() ^ TRAPDOOR_OPEN_BIT);
                this.level.setBlock(this, this, true);
                this.level.addLevelSoundEvent(this.blockCenter(), isOpen() ? LevelSoundEventPacket.SOUND_TRAPDOOR_OPEN : LevelSoundEventPacket.SOUND_TRAPDOOR_CLOSE, getFullId());
                return type;
            }
        }

        return 0;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        BlockFace facing;
        boolean top;
        int meta = 0;

        if (face.getAxis().isHorizontal() || player == null) {
            facing = face;
            top = fy > 0.5;
        } else {
            facing = player.getDirection().getOpposite();
            top = face != BlockFace.UP;
        }

        int faceBit = facing.getReversedHorizontalIndex();
        meta |= faceBit;

        if (top) {
            meta |= TRAPDOOR_TOP_BIT;
        }
        this.setDamage(meta);
        this.getLevel().setBlock(block, this, true, true);
        return true;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(this.getItemId());
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, float fx, float fy, float fz, Player player) {
        if (toggle(player)) {
            return true;
        }
        return false;
    }

    public boolean toggle(Player player) {
        DoorToggleEvent ev = new DoorToggleEvent(this, player);
        getLevel().getServer().getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            return false;
        }
        this.setDamage(this.getDamage() ^ TRAPDOOR_OPEN_BIT);
        level.addLevelSoundEvent(blockCenter(), isOpen() ? LevelSoundEventPacket.SOUND_TRAPDOOR_OPEN : LevelSoundEventPacket.SOUND_TRAPDOOR_CLOSE, getFullId());
        getLevel().setBlock(this, this, true);
        return true;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WOOD_BLOCK_COLOR;
    }

    public boolean isOpen() {
        return (this.getDamage() & TRAPDOOR_OPEN_BIT) == TRAPDOOR_OPEN_BIT;
    }

    public boolean isTop() {
        return (this.getDamage() & TRAPDOOR_TOP_BIT) == TRAPDOOR_TOP_BIT;
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromReversedHorizontalIndex(this.getDamage() & DIRECTION_MASK);
    }

    @Override
    public boolean canContainWater() {
        return true;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public boolean canProvideSupport(BlockFace face, SupportType type) {
        return false;
    }

    @Override
    public int getFuelTime() {
        return 300;
    }

    @Override
    public boolean isTrapdoor() {
        return true;
    }
}
