package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityFallingBlock;
import cn.nukkit.event.block.BlockFallEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockFace.Axis;
import cn.nukkit.math.BlockFace.Plane;
import cn.nukkit.utils.BlockColor;

public class BlockScaffolding extends BlockTransparentMeta { // extends BlockFallable (HeavyBlock) in vanilla

    public static final int STABILITY_MASK = 0b111;
    public static final int STABILITY_CHECK_BIT = 0b1000;

    public static final int UNSTABLE = 7;

    public BlockScaffolding() {
        this(0);
    }

    public BlockScaffolding(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return SCAFFOLDING;
    }

    @Override
    public String getName() {
        return "Scaffolding";
    }

    @Override
    public double getHardness() {
        return 0.6;
    }

    @Override
    public double getResistance() {
        return 4.5;
    }

    @Override
    public int getBurnChance() {
        return 60;
    }

    @Override
    public int getBurnAbility() {
        return 20;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.SAND_BLOCK_COLOR;
    }

    @Override
    public AxisAlignedBB getBoundingBox() {
        return null;
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        return null;
    }

    @Override
    protected AxisAlignedBB recalculateCollisionBoundingBox() {
        return this;
    }

    @Override
    public boolean canPassThrough() {
        return true;
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
    public boolean canProvideSupport(BlockFace face, SupportType type) {
        return face == BlockFace.UP;
    }

    @Override
    public boolean hasEntityCollision() {
        return true;
    }

    @Override
    public void onEntityCollide(Entity entity) {
        entity.resetFallDistance();
    }

    @Override
    public boolean canContainWater() {
        return true;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(getItemId());
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        int stability = UNSTABLE;
        Block below = down();
        if (below.getId() == SCAFFOLDING) {
            stability = below.getDamage() & STABILITY_MASK;
        } else if (SupportType.hasFullSupport(below, BlockFace.UP)) {
            stability = 0;
        } else {
            for (BlockFace side : Plane.HORIZONTAL) {
                Block sideBlock = getSide(side);
                if (sideBlock.getId() != SCAFFOLDING) {
                    continue;
                }

                int provide = (sideBlock.getDamage() & STABILITY_MASK) + 1;
                if (provide < stability) {
                    stability = provide;
                }
            }

            if (stability == UNSTABLE && face == BlockFace.UP) {
                return false;
            }
        }

        setDamage(stability);
        if (!super.place(item, block, target, face, fx, fy, fz, player)) {
            return false;
        }

        level.scheduleUpdate(this, 1);
        return true;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, Player player) {
        if (item.getId() == getItemId() && face.getAxis() == Axis.Y) {
            Block top = this;
            Block up;
            while ((up = top.up()).getId() == SCAFFOLDING) {
                top = up;
            }

            if (!up.canBeReplaced() || up.isLava() || top.getFloorY() >= level.getMaxHeight()) {
                return true;
            }

            level.setBlock(up, clone(), true);

            if (player != null && !player.isCreative()) {
                item.count--;
            }
            return true;
        }

        return false;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            int stability = calculateStability();
            if (stability != getStability()) {
                level.scheduleUpdate(this, 1);
                return type;
            }
            return 0;
        }

        if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            int stability = calculateStability();

            if (stability == UNSTABLE) {
                if (isStabilityCheck()) {
                    level.useBreakOn(this);
                    return type;
                }

                BlockFallEvent event = new BlockFallEvent(this);
                level.getServer().getPluginManager().callEvent(event);
                if (!event.isCancelled()) {
                    //TODO: sync
                    level.setBlock(this, get(AIR), true);

                    EntityFallingBlock fallingBlock = (EntityFallingBlock) Entity.createEntity("FallingSand", getChunk(), Entity.getDefaultNBT(add(0.5, 0, 0.5))
                            .putInt("TileID", getId())
                            .putByte("Data", 0));
                    if (fallingBlock != null) {
                        fallingBlock.sync = true;
                        fallingBlock.spawnToAll();
                    }

                    return type;
                }
            }

            if (stability != getStability() || !isStabilityCheck()) {
                setDamage(stability | STABILITY_CHECK_BIT);
                level.setBlock(this, this, true);
                return type;
            }
        }

        return 0;
    }

    @Override
    public boolean isHeavyBlock() {
        return true;
    }

    protected boolean canSlide() {
        return calculateStability() == UNSTABLE;
    }

    private int calculateStability() {
        Block below = down();
        if (below.getId() == SCAFFOLDING) {
            return below.getDamage() & STABILITY_MASK;
        } else if (SupportType.hasFullSupport(below, BlockFace.UP)) {
            return 0;
        }

        int stability = UNSTABLE;
        for (BlockFace face : Plane.HORIZONTAL) {
            Block block = getSide(face);
            if (block.getId() != SCAFFOLDING) {
                continue;
            }

            int provide = (block.getDamage() & STABILITY_MASK) + 1;
            if (provide < stability) {
                stability = provide;
            }
        }
        return stability;
    }

    public int getStability() {
        return (getDamage() & STABILITY_MASK);
    }

    public boolean isStabilityCheck() {
        return (getDamage() & STABILITY_CHECK_BIT) == STABILITY_CHECK_BIT;
    }
}
