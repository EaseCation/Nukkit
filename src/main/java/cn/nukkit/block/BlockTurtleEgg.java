package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Mth;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.utils.BlockColor;

import java.util.concurrent.ThreadLocalRandom;

import static cn.nukkit.GameVersion.*;

public class BlockTurtleEgg extends BlockTransparentMeta {

    public static final int EGG_COUNT_MASK = 0b11;
    public static final int EGG_COUNT_BITS = 2;
    public static final int CRACKED_STATE_MASK = 0b1100;

    public static final int NO_CRACKS = 0;
    public static final int CRACKED = 1;
    public static final int MAX_CRACKED = 2;

    public BlockTurtleEgg() {
        this(0);
    }

    public BlockTurtleEgg(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return TURTLE_EGG;
    }

    @Override
    public String getName() {
        return "Turtle Egg";
    }

    @Override
    public double getHardness() {
        if (V1_20_30.isAvailable()) {
            return 0.5;
        }
        return 0.4;
    }

    @Override
    public double getResistance() {
        return 2.5;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.SAND_BLOCK_COLOR;
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
    public double getMinX() {
        return this.y + 3 / 16.0;
    }

    @Override
    public double getMinZ() {
        return this.y + 3 / 16.0;
    }

    @Override
    public double getMaxX() {
        return this.y + 1 - 3 / 16.0;
    }

    @Override
    public double getMaxY() {
        return this.y + 1 - 9 / 16.0;
    }

    @Override
    public double getMaxZ() {
        return this.y + 1 - 3 / 16.0;
    }

    @Override
    public boolean canProvideSupport(BlockFace face, SupportType type) {
        return false;
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[0];
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(getItemId());
    }

    @Override
    public boolean onBreak(Item item) {
        level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_BLOCK_TURTLE_EGG_BREAK);

        int count = getEggCount() - 1;
        return level.setBlock(this, count == 0 ? get(AIR) : get(TURTLE_EGG, (count - 1) | (getDamage() & CRACKED_STATE_MASK)), true);
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (!SupportType.hasFullSupport(down(), BlockFace.UP)) {
            return false;
        }

        if (!super.place(item, block, target, face, fx, fy, fz, player)) {
            return false;
        }

        level.scheduleRandomUpdate(this, 1000);
        return true;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, Player player) {
        if (item.getId() == getItemId()) {
            int count = getEggCount();
            if (count == 4) {
                return true;
            }

            if (player != null && !player.isCreative()) {
                item.count--;
            }

            setEggCount(count + 1);
            level.setBlock(this, this, true);

            level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_PLACE, getFullId());
            return true;
        }
        return false;
    }

    @Override
    public int onUpdate(int type) {
        //TODO: hatch
        if (type == Level.BLOCK_UPDATE_RANDOM) {

        }

        if (type == Level.BLOCK_UPDATE_SCHEDULED) {

            level.scheduleRandomUpdate(this, ThreadLocalRandom.current().nextInt(750, 750 * 2));
            return type;
        }

        return 0;
    }

    @Override
    public boolean hasEntityCollision() {
        return true;
    }

    @Override
    public void onEntityCollide(Entity entity) {
        //TODO: trample
    }

    public int getEggCount() {
        return (getDamage() & EGG_COUNT_MASK) + 1;
    }

    public void setEggCount(int count) {
        setDamage((getDamage() & ~EGG_COUNT_MASK) | ((Mth.clamp(count, 1, 4) - 1) & EGG_COUNT_MASK));
    }

    public int getCrackedState() {
        return (getDamage() & CRACKED_STATE_MASK) >> EGG_COUNT_BITS;
    }

    public void setCrackedState(int state) {
        setDamage((getDamage() & ~CRACKED_STATE_MASK) | (state << EGG_COUNT_BITS));
    }
}
