package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.passive.EntityTurtle;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.particle.DestroyBlockParticle;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Mth;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.utils.BlockColor;

import java.util.concurrent.ThreadLocalRandom;

import static cn.nukkit.GameVersion.*;

public class BlockTurtleEgg extends BlockTransparent {

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
    public float getHardness() {
        if (V1_20_30.isAvailable()) {
            return 0.5f;
        }
        return 0.4f;
    }

    @Override
    public float getResistance() {
        return 2.5f;
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
        return face == BlockFace.DOWN && type == SupportType.CENTER;
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        return new Item[0];
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(getItemId());
    }

    @Override
    public boolean onBreak(Item item, Player player) {
        level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_BLOCK_TURTLE_EGG_BREAK);

        int count = getEggCount() - 1;
        return level.setBlock(this, count == 0 ? get(AIR) : get(TURTLE_EGG, (count - 1) | (getDamage() & CRACKED_STATE_MASK)), true);
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
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
    public boolean onActivate(Item item, BlockFace face, float fx, float fy, float fz, Player player) {
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
        if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            ThreadLocalRandom random = ThreadLocalRandom.current();
            int time = level.getTime();
            if ((time >= 21600 && time < 22550 || random.nextInt(500) == 0) && down().isSand()) {
                int hatchLevel = getCrackedState();
                if (hatchLevel >= MAX_CRACKED) {
                    level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_BLOCK_TURTLE_EGG_HATCH);

                    level.setBlock(this, get(AIR), true);

                    FullChunk chunk = getChunk();
                    for (int i = 0; i < getEggCount(); i++) {
                        level.addParticle(new DestroyBlockParticle(this, this));

                        new EntityTurtle(chunk, Entity.getDefaultNBT(add(0.5, 0, 0.5), null, random.nextInt(360), 0)
                                .putBoolean("IsBaby", true)
                                .putFloat("Scale", 0.16f))
                                .spawnToAll();
                    }
                    return Level.BLOCK_UPDATE_NORMAL;
                }

                level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_BLOCK_TURTLE_EGG_CRACK);

                setCrackedState(hatchLevel + 1);
                level.setBlock(this, this, true);
            }
            level.scheduleRandomUpdate(this, random.nextInt(750, 750 * 2));
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
