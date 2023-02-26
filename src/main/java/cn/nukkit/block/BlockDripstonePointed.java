package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityID;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.entity.item.EntityFallingBlock;
import cn.nukkit.event.block.BlockFallEvent;
import cn.nukkit.event.entity.EntityDamageByBlockEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Mth;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.BlockColor;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.List;

public class BlockDripstonePointed extends BlockTransparentMeta {
    public static final int THICKNESS_TIP = 0;
    public static final int THICKNESS_FRUSTUM = 1;
    public static final int THICKNESS_MIDDLE = 2;
    public static final int THICKNESS_BASE = 3;
    public static final int THICKNESS_MERGE = 4;

    public static final int THICKNESS_MASK = 0b111;
    public static final int HANGING_BIT = 0b1000;

    public BlockDripstonePointed() {
        this(0);
    }

    public BlockDripstonePointed(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return POINTED_DRIPSTONE;
    }

    @Override
    public String getName() {
        return "Dripstone Pointed";
    }

    @Override
    public double getHardness() {
        return 1.5;
    }

    @Override
    public double getResistance() {
        return 9;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(getItemId(), HANGING_BIT);
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        BlockFace side = face.getOpposite();
        if (!face.isVertical()) {
            side = BlockFace.DOWN;
        }
        Block attachedBlock = getSide(side);
        setDamage(side == BlockFace.UP ? HANGING_BIT : 0);
        if (!canBeSupportedBy(attachedBlock, side.getOpposite())) {
            side = side.getOpposite();
            attachedBlock = getSide(side);
            setDamage(side == BlockFace.UP ? HANGING_BIT : 0);
            if (!canBeSupportedBy(attachedBlock, side.getOpposite())) {
                return false;
            }
        }

        return super.place(item, block, target, face, fx, fy, fz, player);
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            level.scheduleUpdate(this, 1);
            return Level.BLOCK_UPDATE_NORMAL;
        }

        if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            int hangingBit = getDamage() & HANGING_BIT;

            Block above = up();
            Block below = down();
            boolean sameAbove = above.getId() == POINTED_DRIPSTONE && (above.getDamage() & HANGING_BIT) == hangingBit;
            boolean sameBelow = below.getId() == POINTED_DRIPSTONE && (below.getDamage() & HANGING_BIT) == hangingBit;
            if (sameAbove && sameBelow) {
                return Level.BLOCK_UPDATE_SCHEDULED;
            }

            if (canSurvive()) {
                if (hangingBit == 0) {
                    if (sameAbove) {
                        return 0;
                    }
                } else if (sameBelow) {
                    return 0;
                }

                if (updateColumnThickness()) {
                    return Level.BLOCK_UPDATE_NORMAL;
                }

                return 0;
            }

            if (!isHanging()) {
                level.useBreakOn(this);
                return Level.BLOCK_UPDATE_NORMAL;
            }

            List<Block> blocks = new ObjectArrayList<>();
            blocks.add(this);

            Block current = this;
            Block next;
            while ((next = current.down()).getId() == POINTED_DRIPSTONE && (next.getDamage() & HANGING_BIT) == hangingBit) {
                blocks.add(next);
                current = next;
            }

            for (int i = blocks.size() - 1; i >= 0; i--) {
                Block block = blocks.get(i);

                BlockFallEvent event = new BlockFallEvent(block);
                event.call();
                if (event.isCancelled()) {
                    level.useBreakOn(block);
                    return Level.BLOCK_UPDATE_NORMAL;
                }

                //TODO: sync
                level.setBlock(block, Blocks.air(), true);

                EntityFallingBlock fallingBlock = new EntityFallingBlock(block.getChunk(),
                        Entity.getDefaultNBT(block.add(0.5, 0, 0.5))
                                .putInt("TileID", POINTED_DRIPSTONE)
                                .putByte("Data", block.getDamage()));
                fallingBlock.sync = true;
                fallingBlock.spawnToAll();
            }

            return Level.BLOCK_UPDATE_NORMAL;
        }

        if (type == Level.BLOCK_UPDATE_RANDOM) {
            //TODO: grow & fillCauldron
            return Level.BLOCK_UPDATE_RANDOM;
        }

        return 0;
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        AxisAlignedBB aabb;
        switch (getThickness()) {
            default:
            case THICKNESS_TIP:
                if (isHanging()) {
                    aabb = new SimpleAxisAlignedBB(x + 5.0 / 16, y + 5.0 / 16, z + 5.0 / 16, x + 11.0 / 16, y + 1, z + 11.0 / 16);
                } else {
                    aabb = new SimpleAxisAlignedBB(x + 5.0 / 16, y, z + 5.0 / 16, x + 11.0 / 16, y + 11.0 / 16, z + 11.0 / 16);
                }
                break;
            case THICKNESS_FRUSTUM:
                aabb = new SimpleAxisAlignedBB(x + 4.0 / 16, y, z + 4.0 / 16, x + 12.0 / 16, y + 1, z + 12.0 / 16);
                break;
            case THICKNESS_MIDDLE:
                aabb = new SimpleAxisAlignedBB(x + 3.0 / 16, y, z + 3.0 / 16, x + 13.0 / 16, y + 1, z + 13.0 / 16);
                break;
            case THICKNESS_BASE:
                aabb = new SimpleAxisAlignedBB(x + 2.0 / 16, y, z + 2.0 / 16, x + 14.0 / 16, y + 1, z + 14.0 / 16);
                break;
            case THICKNESS_MERGE:
                aabb = new SimpleAxisAlignedBB(x + 5.0 / 16, y, z + 5.0 / 16, x + 11.0 / 16, y + 1, z + 11.0 / 16);
                break;
        }

        Vector3 offset = getRandomBlockPositionOffset();
        return aabb.offset(offset.getX(), offset.getY(), offset.getZ());
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    /*@Override
    public boolean hasEntityCollision() {
        return true;
    }

    @Override
    public void onEntityCollide(Entity entity) {
        if (entity.getNetworkId() == EntityID.THROWN_TRIDENT) {
            level.useBreakOn(this);
            return;
        }

        if (!isHanging() && getThickness() == THICKNESS_TIP) {
            return;
        }

        if (!(entity instanceof EntityLiving) || entity.fallDistance <= 1) {
            return;
        }

        float damage = Mth.ceil(entity.fallDistance * 2 - 2);
        if (damage < 0) {
            return;
        }

        entity.attack(new EntityDamageByBlockEvent(this, entity, DamageCause.FALL, damage));
        //TODO: move to Entity::fall
    }*/

    @Override
    public boolean canProvideSupport(BlockFace face, SupportType type) {
        return false;
    }

    @Override
    public boolean canContainWater() {
        return true;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.BROWN_TERRACOTA_BLOCK_COLOR;
    }

    @Override
    public boolean isHeavyBlock() {
        return true;
    }

    private boolean canSurvive() {
        BlockFace face = isHanging() ? BlockFace.UP : BlockFace.DOWN;
        Block block = getSide(face);
        return canBeSupportedBy(block, face.getOpposite());
    }

    private boolean canBeSupportedBy(Block block, BlockFace face) {
        return block.getId() == POINTED_DRIPSTONE && (block.getDamage() & HANGING_BIT) == (getDamage() & HANGING_BIT)
                || SupportType.hasFullSupport(block, face);
    }

    private Vector3 getRandomBlockPositionOffset() {
        int seed = Mth.getSeed(getFloorX(), 0, getFloorZ());
        return new Vector3(
                Mth.clamp((seed & 0xf) * (1.0 / 30) - 4.0 / 16, -2.0 / 16, 2.0 / 16),
                0,
                Mth.clamp(((seed >> 8) & 0xf) * (1.0 / 30) - 4.0 / 16, -2.0 / 16, 2.0 / 16));
    }

    private boolean updateColumnThickness() {
        int thickness = getThickness();
        int hangingBit = getDamage() & HANGING_BIT;
        BlockFace face = hangingBit != 0 ? BlockFace.UP : BlockFace.DOWN;

        Block block = getSide(face.getOpposite());
        if (block.getId() == POINTED_DRIPSTONE) {
            if (thickness != THICKNESS_MERGE) {
                setThickness(THICKNESS_MERGE);
                level.setBlock(this, this, true, false);
            }
        } else {
            if (thickness != THICKNESS_TIP) {
                setThickness(THICKNESS_TIP);
                level.setBlock(this, this, true, false);
            }
        }

        List<Block> blocks = new ObjectArrayList<>();
        Block current = this;
        Block next;
        while ((next = current.getSide(face)).getId() == POINTED_DRIPSTONE && (next.getDamage() & HANGING_BIT) == hangingBit) {
            blocks.add(next);
            current = next;
        }

        int height = blocks.size();
        switch (height) {
            case 0:
                return true;
            default:
                if (height > 2) {
                    for (int i = 1; i < height - 1; i++) {
                        Block middle = blocks.get(i);
                        if ((middle.getDamage() & THICKNESS_MASK) == THICKNESS_MIDDLE) {
                            continue;
                        }

                        middle.setDamage(THICKNESS_MIDDLE | hangingBit);
                        level.setBlock(middle, middle, true, false);
                    }
                }

                Block base = blocks.get(height - 1);
                if ((base.getDamage() & THICKNESS_MASK) != THICKNESS_BASE) {
                    base.setDamage(THICKNESS_BASE | hangingBit);
                    level.setBlock(base, base, true, false);
                }
            case 1:
                Block frustum = blocks.get(0);
                if ((frustum.getDamage() & THICKNESS_MASK) != THICKNESS_FRUSTUM) {
                    frustum.setDamage(THICKNESS_FRUSTUM | hangingBit);
                    level.setBlock(frustum, frustum, true, false);
                }
        }
        return true;
    }

    public int getThickness() {
        return getDamage() & THICKNESS_MASK;
    }

    public void setThickness(int thickness) {
        setDamage((getDamage() & ~THICKNESS_MASK) | (thickness & THICKNESS_MASK));
    }

    public boolean isHanging() {
        return (getDamage() & HANGING_BIT) == HANGING_BIT;
    }

    public void setHanging(boolean hanging) {
        setDamage(hanging ? getDamage() | HANGING_BIT : getDamage() & ~HANGING_BIT);
    }
}
