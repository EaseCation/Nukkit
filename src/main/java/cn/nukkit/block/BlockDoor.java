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
import cn.nukkit.utils.Faceable;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class BlockDoor extends BlockTransparent implements Faceable {
    public static final int DIRECTION_MASK = 0b11;
    public static final int DOOR_OPEN_BIT = 0b100;
    public static final int DOOR_TOP_BIT = 0b1000;
    public static final int DOOR_HINGE_BIT = 0b10000;

    private static final int[] FACES = {1, 2, 3, 0};

    protected BlockDoor(int meta) {
        super(meta);
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        double f = 0.1875;

        AxisAlignedBB bb = new SimpleAxisAlignedBB(
                this.x,
                this.y,
                this.z,
                this.x + 1,
                this.y + 2,
                this.z + 1
        );

        int j = isTop() ? (this.down().getDamage() & DIRECTION_MASK) : getDamage() & DIRECTION_MASK;
        boolean isOpen = isOpen();
        boolean isRight = isRightHinged();

        if (j == 0) {
            if (isOpen) {
                if (!isRight) {
                    bb.setBounds(
                            this.x,
                            this.y,
                            this.z,
                            this.x + 1,
                            this.y + 1,
                            this.z + f
                    );
                } else {
                    bb.setBounds(
                            this.x,
                            this.y,
                            this.z + 1 - f,
                            this.x + 1,
                            this.y + 1,
                            this.z + 1
                    );
                }
            } else {
                bb.setBounds(
                        this.x,
                        this.y,
                        this.z,
                        this.x + f,
                        this.y + 1,
                        this.z + 1
                );
            }
        } else if (j == 1) {
            if (isOpen) {
                if (!isRight) {
                    bb.setBounds(
                            this.x + 1 - f,
                            this.y,
                            this.z,
                            this.x + 1,
                            this.y + 1,
                            this.z + 1
                    );
                } else {
                    bb.setBounds(
                            this.x,
                            this.y,
                            this.z,
                            this.x + f,
                            this.y + 1,
                            this.z + 1
                    );
                }
            } else {
                bb.setBounds(
                        this.x,
                        this.y,
                        this.z,
                        this.x + 1,
                        this.y + 1,
                        this.z + f
                );
            }
        } else if (j == 2) {
            if (isOpen) {
                if (!isRight) {
                    bb.setBounds(
                            this.x,
                            this.y,
                            this.z + 1 - f,
                            this.x + 1,
                            this.y + 1,
                            this.z + 1
                    );
                } else {
                    bb.setBounds(
                            this.x,
                            this.y,
                            this.z,
                            this.x + 1,
                            this.y + 1,
                            this.z + f
                    );
                }
            } else {
                bb.setBounds(
                        this.x + 1 - f,
                        this.y,
                        this.z,
                        this.x + 1,
                        this.y + 1,
                        this.z + 1
                );
            }
        } else if (j == 3) {
            if (isOpen) {
                if (!isRight) {
                    bb.setBounds(
                            this.x,
                            this.y,
                            this.z,
                            this.x + f,
                            this.y + 1,
                            this.z + 1
                    );
                } else {
                    bb.setBounds(
                            this.x + 1 - f,
                            this.y,
                            this.z,
                            this.x + 1,
                            this.y + 1,
                            this.z + 1
                    );
                }
            } else {
                bb.setBounds(
                        this.x,
                        this.y,
                        this.z + 1 - f,
                        this.x + 1,
                        this.y + 1,
                        this.z + 1
                );
            }
        }

        return bb;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (!isTop() && !SupportType.hasFullSupport(this.down(), BlockFace.UP)) {
                Block up = this.up();

                if (up instanceof BlockDoor) {
                    this.getLevel().setBlock(up, Block.get(BlockID.AIR), true, false);
                    this.getLevel().useBreakOn(this, Item.get(Item.WOODEN_PICKAXE), true);
                }

                return Level.BLOCK_UPDATE_NORMAL;
            }
        }

        if (type == Level.BLOCK_UPDATE_REDSTONE) {
            if (!this.level.isRedstoneEnabled()) {
                return 0;
            }

            if ((!isOpen() && this.level.isBlockPowered(this)) || (isOpen() && !this.level.isBlockPowered(this))) {
                this.level.getServer().getPluginManager().callEvent(new BlockRedstoneEvent(this, isOpen() ? 15 : 0, isOpen() ? 0 : 15));

                this.toggle(null);
            }
        }

        return 0;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        if (this.y >= level.getHeightRange().getMaxY() - 1) {
            return false;
        }
        if (face == BlockFace.UP) {
            Block blockUp = this.up();
            Block blockDown = this.down();
            if (!blockUp.canBeReplaced() || !SupportType.hasFullSupport(blockDown, BlockFace.UP)) {
                return false;
            }

            int metaUp = DOOR_TOP_BIT;
            if (player != null) {
                BlockFace facing = player.getDirection();
                int direction = FACES[facing.getHorizontalIndex()];

                Block left = this.getSide(facing.rotateYCCW());
                if (left.getId() == this.getId()) {
                    metaUp |= DOOR_HINGE_BIT;
                } else {
                    int leftHinge = 0;
                    if (!left.isTransparent()) {
                        leftHinge++;
                    }
                    if (!left.up().isTransparent()) {
                        leftHinge++;
                    }

                    if (leftHinge < 2) {
                        Block right = this.getSide(facing.rotateY());
                        if (!right.isTransparent() && leftHinge == 0 || !right.up().isTransparent()) {
                            metaUp |= DOOR_HINGE_BIT;
                        }
                    }
                }

                this.setDamage(direction);
            }

            this.getLevel().setBlock(block, this, true, false); //Bottom

            if (this.level.isRedstoneEnabled()) {
                if (!this.isOpen() && this.level.isBlockPowered(this)) {
                    this.toggle(null);
                }
            }
            this.getLevel().setBlock(blockUp, Block.get(this.getId(), metaUp), true, true); //Top

            return true;
        }

        return false;
    }

    @Override
    public boolean onBreak(Item item, Player player) {
        if (isTop(this.getDamage())) {
            Block down = this.down();
            if (down.getId() == this.getId()) {
                this.getLevel().setBlock(down, Block.get(BlockID.AIR), true);
            }
        } else {
            Block up = this.up();
            if (up.getId() == this.getId()) {
                this.getLevel().setBlock(up, Block.get(BlockID.AIR), true);
            }
        }
        this.getLevel().setBlock(this, Block.get(BlockID.AIR), true);

        return true;
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, float fx, float fy, float fz, Player player) {
        if (!this.toggle(player)) {
            return false;
        }

        return true;
    }

    public boolean toggle(Player player) {
        DoorToggleEvent event = new DoorToggleEvent(this, player);
        this.getLevel().getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return false;
        }

        Block down;
        if (isTop()) {
            down = this.down();
        } else {
            down = this;
        }
        if (down.up().getId() != down.getId()) {
            return false;
        }
        down.setDamage(down.getDamage() ^ DOOR_OPEN_BIT);
        getLevel().setBlock(down, down, true, true);

        level.addLevelSoundEvent(blockCenter(), isOpen() ? LevelSoundEventPacket.SOUND_DOOR_OPEN : LevelSoundEventPacket.SOUND_DOOR_CLOSE, getFullId());
        return true;
    }

    public boolean isOpen() {
        if (isTop(this.getDamage())) {
            return (this.down().getDamage() & DOOR_OPEN_BIT) > 0;
        } else {
            return (this.getDamage() & DOOR_OPEN_BIT) > 0;
        }
    }

    public boolean isTop() {
        return isTop(this.getDamage());
    }

    public boolean isTop(int meta) {
        return (meta & DOOR_TOP_BIT) != 0;
    }

    public boolean isRightHinged() {
        if (isTop()) {
            return (this.getDamage() & DOOR_HINGE_BIT) > 0;
        }
        return (this.up().getDamage() & DOOR_HINGE_BIT) > 0;
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(this.getDamage() & DIRECTION_MASK);
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
    public boolean canContainWater() {
        return true;
    }

    @Override
    public boolean canProvideSupport(BlockFace face, SupportType type) {
        return false;
    }

    @Override
    public boolean isDoor() {
        return true;
    }
}
