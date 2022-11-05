package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.block.LeavesDecayEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Hash;
import it.unimi.dsi.fastutil.longs.LongArraySet;
import it.unimi.dsi.fastutil.longs.LongSet;

import java.util.concurrent.ThreadLocalRandom;

/**
 * author: Angelic47
 * Nukkit Project
 */
public class BlockLeaves extends BlockTransparentMeta {
    public static final int OAK = 0;
    public static final int SPRUCE = 1;
    public static final int BIRCH = 2;
    public static final int JUNGLE = 3;

    public static final int TYPE_MASK = 0b11;
    public static final int UPDATE_BIT = 0b100;
    public static final int PERSISTENT_BIT = 0b1000;

    private static final String[] NAMES = new String[]{
            "Oak Leaves",
            "Spruce Leaves",
            "Birch Leaves",
            "Jungle Leaves",
    };

    public BlockLeaves() {
        this(0);
    }

    public BlockLeaves(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return LEAVES;
    }

    @Override
    public double getHardness() {
        return 0.2;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_SHEARS;
    }

    @Override
    public String getName() {
        return NAMES[this.getLeafType()];
    }

    @Override
    public int getBurnChance() {
        return 30;
    }

    @Override
    public int getBurnAbility() {
        return 60;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        this.setPersistent(true);
        this.getLevel().setBlock(this, this, true);
        return true;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return new ItemBlock(this, this.getLeafType(), 1);
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isShears()) {
            return new Item[]{
                    toItem(true)
            };
        } else {
            ThreadLocalRandom random = ThreadLocalRandom.current();
            if (this.canDropApple() && random.nextInt(200) == 0) {
                return new Item[]{
                        Item.get(Item.APPLE)
                };
            }
            if (random.nextInt(20) == 0) {
                if (random.nextBoolean()) {
                    return new Item[]{
                            Item.get(Item.STICK, 0, random.nextInt(1, 2))
                    };
                } else if (getId() != LEAVES || this.getLeafType() != JUNGLE || random.nextInt(20) == 0) {
                    return new Item[]{
                            this.getSapling()
                    };
                }
            }
        }
        return new Item[0];
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_RANDOM && !isPersistent() && !isCheckDecay()) {
            setCheckDecay(true);
            getLevel().setBlock(this, this, false, false);
        } else if (type == Level.BLOCK_UPDATE_RANDOM && isCheckDecay() && !isPersistent()) {
            setDamage(getLeafType());
            int check = 0;

            LeavesDecayEvent ev = new LeavesDecayEvent(this);

            Server.getInstance().getPluginManager().callEvent(ev);
            if (ev.isCancelled() || findLog(this, new LongArraySet(), 0, check)) {
                getLevel().setBlock(this, this, false, false);
            } else {
                getLevel().useBreakOn(this);
                return Level.BLOCK_UPDATE_NORMAL;
            }
        }
        return 0;
    }

    private Boolean findLog(Block pos, LongSet visited, Integer distance, Integer check) {
        return findLog(pos, visited, distance, check, null);
    }

    private Boolean findLog(Block pos, LongSet visited, Integer distance, Integer check, BlockFace fromSide) {
        ++check;
        long index = Hash.hashBlock((int) pos.x, (int) pos.y, (int) pos.z);
        if (visited.contains(index)) return false;
        if (pos.isLog()) return true;
        if (pos.isLeaves() && distance <= 6) {
            visited.add(index);
            Block down = pos.down();
            if (down.isLog()) {
                return true;
            }
            if (fromSide == null) {
                //North, East, South, West
                for (int side = 2; side <= 5; ++side) {
                    if (this.findLog(pos.getSide(BlockFace.fromIndex(side)), visited, distance + 1, check, BlockFace.fromIndex(side)))
                        return true;
                }
            } else { //No more loops
                switch (fromSide) {
                    case NORTH:
                        if (this.findLog(pos.getSide(BlockFace.NORTH), visited, distance + 1, check, fromSide))
                            return true;
                        if (this.findLog(pos.getSide(BlockFace.WEST), visited, distance + 1, check, fromSide))
                            return true;
                        if (this.findLog(pos.getSide(BlockFace.EAST), visited, distance + 1, check, fromSide))
                            return true;
                        break;
                    case SOUTH:
                        if (this.findLog(pos.getSide(BlockFace.SOUTH), visited, distance + 1, check, fromSide))
                            return true;
                        if (this.findLog(pos.getSide(BlockFace.WEST), visited, distance + 1, check, fromSide))
                            return true;
                        if (this.findLog(pos.getSide(BlockFace.EAST), visited, distance + 1, check, fromSide))
                            return true;
                        break;
                    case WEST:
                        if (this.findLog(pos.getSide(BlockFace.NORTH), visited, distance + 1, check, fromSide))
                            return true;
                        if (this.findLog(pos.getSide(BlockFace.SOUTH), visited, distance + 1, check, fromSide))
                            return true;
                        if (this.findLog(pos.getSide(BlockFace.WEST), visited, distance + 1, check, fromSide))
                            return true;
                    case EAST:
                        if (this.findLog(pos.getSide(BlockFace.NORTH), visited, distance + 1, check, fromSide))
                            return true;
                        if (this.findLog(pos.getSide(BlockFace.SOUTH), visited, distance + 1, check, fromSide))
                            return true;
                        if (this.findLog(pos.getSide(BlockFace.EAST), visited, distance + 1, check, fromSide))
                            return true;
                        break;
                }
            }
        }
        return false;
    }

    public int getLeafType() {
        return getDamage() & TYPE_MASK;
    }

    public boolean isCheckDecay() {
        return (getDamage() & UPDATE_BIT) != 0;
    }

    public void setCheckDecay(boolean checkDecay) {
        setDamage(checkDecay ? getDamage() | UPDATE_BIT : getDamage() & ~UPDATE_BIT);
    }

    public boolean isPersistent() {
        return (getDamage() & PERSISTENT_BIT) != 0;
    }

    public void setPersistent(boolean persistent) {
        setDamage(persistent ? getDamage() | PERSISTENT_BIT : getDamage() & ~PERSISTENT_BIT);
    }

    @Override
    public BlockColor getColor() {
        //TODO: biome blend
        return BlockColor.PLANT_BLOCK_COLOR;
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }

    protected boolean canDropApple() {
        return this.getLeafType() == OAK;
    }

    protected Item getSapling() {
        return Item.get(BlockID.SAPLING, this.getLeafType());
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
    public boolean isLeaves() {
        return true;
    }

    @Override
    public boolean isVegetation() {
        return true;
    }
}
