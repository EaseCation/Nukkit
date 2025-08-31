package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.passive.EntitySniffer;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.network.protocol.LevelEventPacket;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.utils.BlockColor;

import java.util.concurrent.ThreadLocalRandom;

import static cn.nukkit.GameVersion.*;

public class BlockSnifferEgg extends BlockTransparent {
    public static final int NO_CRACKS = 0;
    public static final int CRACKED = 1;
    public static final int MAX_CRACKED = 2;

    public BlockSnifferEgg() {
        this(0);
    }

    public BlockSnifferEgg(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return SNIFFER_EGG;
    }

    @Override
    public String getName() {
        return "Sniffer Egg";
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
    public boolean isSolid() {
        return false;
    }

    @Override
    public double getMinX() {
        return this.x + 1 / 16.0;
    }

    @Override
    public double getMinZ() {
        return this.z + 2 / 16.0;
    }

    @Override
    public double getMaxX() {
        return this.x + 1 - 1 / 16.0;
    }

    @Override
    public double getMaxZ() {
        return this.z + 1 - 2 / 16.0;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.RED_BLOCK_COLOR;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        if (!super.place(item, block, target, face, fx, fy, fz, player)) {
            return false;
        }

        int hatchTime = ThreadLocalRandom.current().nextInt(7200, 8800);
        if (down().is(MOSS_BLOCK)) {
            level.addLevelEvent(this, LevelEventPacket.EVENT_PARTICLE_TURTLE_EGG);
            hatchTime /= 2;
        }
        level.scheduleRandomUpdate(this, hatchTime);
        return true;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            if (getDamage() < MAX_CRACKED) {
                setDamage(getDamage() + 1);
                level.setBlock(this, this, true);

                level.addLevelEvent(this, LevelEventPacket.EVENT_PARTICLE_TURTLE_EGG);
                level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_SNIFFER_EGG_CRACK);

                int hatchTime = ThreadLocalRandom.current().nextInt(7200, 8800);
                level.scheduleRandomUpdate(this, down().is(MOSS_BLOCK) ? hatchTime / 2 : hatchTime);
                return type;
            }

            level.setBlock(this, Blocks.air(), true);

            level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_SNIFFER_EGG_HATCHED);

            new EntitySniffer(getChunk(), Entity.getDefaultNBT(add(0.5, 0, 0.5))
                    .putBoolean("IsBaby", true)
                    .putFloat("Scale", 0.45f))
                    .spawnToAll();
            return Level.BLOCK_UPDATE_NORMAL;
        }
        return 0;
    }

    @Override
    public boolean canProvideSupport(BlockFace face, SupportType type) {
        return face.isVertical() && type == SupportType.CENTER;
    }
}
