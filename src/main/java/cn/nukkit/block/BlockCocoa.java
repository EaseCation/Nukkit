package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.block.BlockGrowEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemDye;
import cn.nukkit.level.Level;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.utils.Faceable;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by CreeperFace on 27. 10. 2016.
 */
public class BlockCocoa extends BlockFlowable implements Faceable {

    protected static final AxisAlignedBB[] EAST = new SimpleAxisAlignedBB[]{new SimpleAxisAlignedBB(0.6875D, 0.4375D, 0.375D, 0.9375D, 0.75D, 0.625D), new SimpleAxisAlignedBB(0.5625D, 0.3125D, 0.3125D, 0.9375D, 0.75D, 0.6875D), new SimpleAxisAlignedBB(0.5625D, 0.3125D, 0.3125D, 0.9375D, 0.75D, 0.6875D)};
    protected static final AxisAlignedBB[] WEST = new SimpleAxisAlignedBB[]{new SimpleAxisAlignedBB(0.0625D, 0.4375D, 0.375D, 0.3125D, 0.75D, 0.625D), new SimpleAxisAlignedBB(0.0625D, 0.3125D, 0.3125D, 0.4375D, 0.75D, 0.6875D), new SimpleAxisAlignedBB(0.0625D, 0.3125D, 0.3125D, 0.4375D, 0.75D, 0.6875D)};
    protected static final AxisAlignedBB[] NORTH = new SimpleAxisAlignedBB[]{new SimpleAxisAlignedBB(0.375D, 0.4375D, 0.0625D, 0.625D, 0.75D, 0.3125D), new SimpleAxisAlignedBB(0.3125D, 0.3125D, 0.0625D, 0.6875D, 0.75D, 0.4375D), new SimpleAxisAlignedBB(0.3125D, 0.3125D, 0.0625D, 0.6875D, 0.75D, 0.4375D)};
    protected static final AxisAlignedBB[] SOUTH = new SimpleAxisAlignedBB[]{new SimpleAxisAlignedBB(0.375D, 0.4375D, 0.6875D, 0.625D, 0.75D, 0.9375D), new SimpleAxisAlignedBB(0.3125D, 0.3125D, 0.5625D, 0.6875D, 0.75D, 0.9375D), new SimpleAxisAlignedBB(0.3125D, 0.3125D, 0.5625D, 0.6875D, 0.75D, 0.9375D)};
    protected static final AxisAlignedBB[] ALL = new AxisAlignedBB[12];

    private static final int[] FACES = new int[]{
            0,
            0,
            0,
            2,
            3,
            1,
    };

    private static final int[] FACING = new int[]{
            3, 4, 2, 5,
    };

    public BlockCocoa() {
        this(0);
    }

    public BlockCocoa(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return COCOA;
    }

    @Override
    public String getName() {
        return "Cocoa";
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

    private AxisAlignedBB getRelativeBoundingBox() {
        int damage = this.getDamage();
        if (damage > 11) {
            this.setDamage(damage = 11);
        }
        AxisAlignedBB boundingBox = ALL[damage];
        if (boundingBox != null) return boundingBox;

        AxisAlignedBB[] bbs;

        switch (getDamage()) {
            case 1:
            case 5:
            case 9:
                bbs = EAST;
                break;
            case 2:
            case 6:
            case 10:
                bbs = SOUTH;
                break;
            case 3:
            case 7:
            case 11:
                bbs = WEST;
                break;
            default:
                bbs = NORTH;
                break;
        }

        return ALL[damage] = bbs[this.getDamage() >> 2];
    }

    @Override
    public boolean canPassThrough() {
        return false;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        if (face.isVertical()) {
            return false;
        }

        if (block.isLiquid() || !block.isAir() && block.canContainWater() && level.getExtraBlock(this).isWater()) {
            return false;
        }

        if (canBeSupportedBy(target)) {
            this.setDamage(FACES[face.getIndex()]);
            return level.setBlock(block, this, true);
        }
        return false;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            Block side = this.getSide(BlockFace.fromIndex(FACING[this.getDamage() & 0x3]));
            if (!canBeSupportedBy(side)) {
                this.getLevel().useBreakOn(this, true);
                return Level.BLOCK_UPDATE_NORMAL;
            }
        } else if (type == Level.BLOCK_UPDATE_RANDOM) {
            if (ThreadLocalRandom.current().nextInt(2) == 1) {
                if (this.getDamage() / 4 < 2) {
                    BlockCocoa block = (BlockCocoa) this.clone();
                    block.setDamage(block.getDamage() + 4);
                    BlockGrowEvent ev = new BlockGrowEvent(this, block);
                    Server.getInstance().getPluginManager().callEvent(ev);

                    if (!ev.isCancelled()) {
                        this.getLevel().setBlock(this, ev.getNewState(), true, true);
                    } else {
                        return Level.BLOCK_UPDATE_RANDOM;
                    }
                }
            } else {
                return Level.BLOCK_UPDATE_RANDOM;
            }
        }

        return 0;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, float fx, float fy, float fz, Player player) {
        if (item.isFertilizer()) {
            Block block = this.clone();
            if (this.getDamage() / 4 < 2) {
                block.setDamage(block.getDamage() + 4);
                BlockGrowEvent ev = new BlockGrowEvent(this, block);
                Server.getInstance().getPluginManager().callEvent(ev);

                if (ev.isCancelled()) {
                    return false;
                }
                this.getLevel().setBlock(this, ev.getNewState(), true, true);
                this.level.addParticle(new BoneMealParticle(this));

                if (player != null && (player.gamemode & 0x01) == 0) {
                    item.count--;
                }
            }

            return true;
        }

        return false;
    }

    @Override
    public boolean isFertilizable() {
        return true;
    }

    @Override
    public float getResistance() {
        return 15;
    }

    @Override
    public float getHardness() {
        return 0.2f;
    }

    @Override
    public int getToolType() {
        return BlockToolType.AXE | BlockToolType.SWORD;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(Item.DYE, ItemDye.COCOA_BEANS);
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        if (this.getDamage() >= 0x8) {
            return new Item[]{
                    Item.get(Item.DYE, ItemDye.COCOA_BEANS, 3)
            };
        } else {
            return new Item[]{
                    Item.get(Item.DYE, ItemDye.COCOA_BEANS, 1)
            };
        }
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(this.getDamage() & 0x07);
    }

    @Override
    public boolean isVegetation() {
        return true;
    }

    private boolean canBeSupportedBy(Block block) {
        int id = block.getId();
        if (id == JUNGLE_LOG) {
            return true;
        }
        if (id == WOOD) {
            return (block.getDamage() & BlockWoodBark.TYPE_MASK) == BlockWoodBark.JUNGLE;
        }
        return false;
    }
}
