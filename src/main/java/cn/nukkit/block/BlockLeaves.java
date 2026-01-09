package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.api.API;
import cn.nukkit.api.API.Definition;
import cn.nukkit.event.block.LeavesDecayEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Hash;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * author: Angelic47
 * Nukkit Project
 */
public abstract class BlockLeaves extends BlockTransparent {
    public static final int[] LEAVES_1 = {
            OAK_LEAVES,
            SPRUCE_LEAVES,
            BIRCH_LEAVES,
            JUNGLE_LEAVES,
    };
    public static final int[] LEAVES_2 = {
            ACACIA_LEAVES,
            DARK_OAK_LEAVES,
    };

    public static final int OAK = 0;
    public static final int SPRUCE = 1;
    public static final int BIRCH = 2;
    public static final int JUNGLE = 3;

    public static final int UPDATE_BIT = 0b1;
    public static final int PERSISTENT_BIT = 0b10;

    @API(definition = Definition.INTERNAL)
    public static final int[] LEAVES1_NUKKIT_LEGACY_META_TO_NUKKIT_RUNTIME_META = { // different from vanilla...
            0b0000, 0b0001, 0b0010, 0b0011,
            0b1000, 0b1001, 0b1010, 0b1011,
            0b0100, 0b0101, 0b0110, 0b0111,
            0b1100, 0b1101, 0b1110, 0b1111
    };

    public static final int ACACIA = 0;
    public static final int DARK_OAK = 1;

    @API(definition = Definition.INTERNAL)
    public static final int[] LEAVES2_NUKKIT_LEGACY_META_TO_NUKKIT_RUNTIME_META = {
            0b000, 0b001,  0b000, 0b001,
            0b100, 0b101,  0b100, 0b101,
            0b010, 0b011,  0b010, 0b011,
            0b110, 0b111,  0b110, 0b111
    };

    @Override
    public float getHardness() {
        return 0.2f;
    }

    @Override
    public float getResistance() {
        return 1;
    }

    @Override
    public int getToolType() {
        return BlockToolType.HOE | BlockToolType.SHEARS | BlockToolType.SWORD;
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
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        this.setPersistent(true);
        this.getLevel().setBlock(this, this, true);
        return true;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(this.getItemId());
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        if (item.isShears()) {
            return new Item[]{
                    toItem(true)
            };
        }

        ThreadLocalRandom random = ThreadLocalRandom.current();
        List<Item> drops = new ArrayList<>(3);
        int chance = getSaplingDropChance();
        if (chance > 0 && random.nextInt(chance) == 0) {
            drops.add(getSapling());
        }
        if (canDropApple() && random.nextInt(200) == 0) {
            drops.add(Item.get(Item.APPLE));
        }
        if (random.nextInt(50) == 0) {
            drops.add(Item.get(Item.STICK, 0, random.nextInt(1, 3)));
        }
        return drops.toArray(new Item[0]);
    }

    @Override
    public int onUpdate(int type) {
        if (type != Level.BLOCK_UPDATE_RANDOM) {
            return 0;
        }

        if (isPersistent()) {
            return 0;
        }

        if (!isCheckDecay()) {
            setCheckDecay(true);
            getLevel().setBlock(this, this, true, false);
        } else {
            int check = 0;
            LeavesDecayEvent ev = new LeavesDecayEvent(this);
            Server.getInstance().getPluginManager().callEvent(ev);
            if (ev.isCancelled() || findLog(this, new LongOpenHashSet(), 0, check)) {
                getLevel().setBlock(this, this, true, false);
            } else {
                getLevel().useBreakOn(this);
                return Level.BLOCK_UPDATE_NORMAL;
            }
        }
        return type;
    }

    private Boolean findLog(Block pos, LongSet visited, int distance, int check) {
        return findLog(pos, visited, distance, check, null);
    }

    private Boolean findLog(Block pos, LongSet visited, int distance, int check, BlockFace fromSide) {
        ++check;
        long index = Hash.hashBlockPos((int) pos.x, (int) pos.y, (int) pos.z);
        if (visited.contains(index)) {
            return false;
        }
        if (pos.isLog()) {
            return true;
        }
        if (pos.isLeaves() && distance <= 6) {
            visited.add(index);
            Block down = pos.down();
            if (down.isLog()) {
                return true;
            }
            if (fromSide == null) {
                //North, East, South, West
                for (int side = 2; side <= 5; ++side) {
                    if (this.findLog(pos.getSide(BlockFace.fromIndex(side)), visited, distance + 1, check, BlockFace.fromIndex(side))) {
                        return true;
                    }
                }
            } else { //No more loops
                switch (fromSide) {
                    case NORTH:
                        if (this.findLog(pos.getSide(BlockFace.NORTH), visited, distance + 1, check, fromSide)) {
                            return true;
                        }
                        if (this.findLog(pos.getSide(BlockFace.WEST), visited, distance + 1, check, fromSide)) {
                            return true;
                        }
                        if (this.findLog(pos.getSide(BlockFace.EAST), visited, distance + 1, check, fromSide)) {
                            return true;
                        }
                        break;
                    case SOUTH:
                        if (this.findLog(pos.getSide(BlockFace.SOUTH), visited, distance + 1, check, fromSide)) {
                            return true;
                        }
                        if (this.findLog(pos.getSide(BlockFace.WEST), visited, distance + 1, check, fromSide)) {
                            return true;
                        }
                        if (this.findLog(pos.getSide(BlockFace.EAST), visited, distance + 1, check, fromSide)) {
                            return true;
                        }
                        break;
                    case WEST:
                        if (this.findLog(pos.getSide(BlockFace.NORTH), visited, distance + 1, check, fromSide)) {
                            return true;
                        }
                        if (this.findLog(pos.getSide(BlockFace.SOUTH), visited, distance + 1, check, fromSide)) {
                            return true;
                        }
                        if (this.findLog(pos.getSide(BlockFace.WEST), visited, distance + 1, check, fromSide)) {
                            return true;
                        }
                        break;
                    case EAST:
                        if (this.findLog(pos.getSide(BlockFace.NORTH), visited, distance + 1, check, fromSide)) {
                            return true;
                        }
                        if (this.findLog(pos.getSide(BlockFace.SOUTH), visited, distance + 1, check, fromSide)) {
                            return true;
                        }
                        if (this.findLog(pos.getSide(BlockFace.EAST), visited, distance + 1, check, fromSide)) {
                            return true;
                        }
                        break;
                }
            }
        }
        return false;
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
    public boolean isSolid() {
        return false;
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }

    protected boolean canDropApple() {
        return false;
    }

    protected int getSaplingDropChance() {
        return 20;
    }

    protected abstract Item getSapling();

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
    public int getCompostableChance() {
        return 30;
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
