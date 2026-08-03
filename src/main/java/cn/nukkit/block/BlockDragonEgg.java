package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.event.block.BlockFromToEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.HeightRange;
import cn.nukkit.math.BlockFace;
import cn.nukkit.network.protocol.LevelEventPacket;
import cn.nukkit.utils.BlockColor;

import java.util.concurrent.ThreadLocalRandom;

import static cn.nukkit.GameVersion.*;
import static cn.nukkit.SharedConstants.*;

public class BlockDragonEgg extends BlockFallable {

    BlockDragonEgg() {

    }

    @Override
    public String getName() {
        return "Dragon Egg";
    }

    @Override
    public int getId() {
        return DRAGON_EGG;
    }

    @Override
    public float getHardness() {
        return 3;
    }

    @Override
    public float getResistance() {
        return 45;
    }

    @Override
    public int getLightLevel() {
        return 1;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.BLACK_BLOCK_COLOR;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public boolean isTransparent() {
        return true;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, float fx, float fy, float fz, Player player) {
        if (player == null) {
            return false;
        }
        this.teleport();
        return true;
    }

    public void teleport() {
        int minYDelta = -16;
        int maxYDelta = 16;
        int y = getFloorY();
        HeightRange heightRange = level.getHeightRange();
        if (y + minYDelta < heightRange.getMinY()) {
            minYDelta = heightRange.getMinY() - y;
        }
        if (y + maxYDelta > heightRange.getMaxY()) {
            maxYDelta = heightRange.getMaxY() - y;
        }
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < 1000; ++i) {
            Block to = this.getLevel().getBlock(this.add(random.nextInt(-16, 16), random.nextInt(minYDelta, maxYDelta), random.nextInt(-16, 16)));
            if (to.getId() == AIR) {
                BlockFromToEvent event = new BlockFromToEvent(this, 0, to);
                this.level.getServer().getPluginManager().callEvent(event);
                if (event.isCancelled()) return;
                to = event.getTo();

                int diffX = this.getFloorX() - to.getFloorX();
                int diffY = this.getFloorY() - to.getFloorY();
                int diffZ = this.getFloorZ() - to.getFloorZ();
                this.getLevel().addLevelEvent(floor(), LevelEventPacket.EVENT_PARTICLE_DRAGON_EGG_TELEPORT,
                        (((((Math.abs(diffX) << 16)
                                | (Math.abs(diffY) << 8))
                                | Math.abs(diffZ))
                                | ((diffX < 0 ? 1 : 0) << 24))
                                | ((diffY < 0 ? 1 : 0) << 25))
                                | ((diffZ < 0 ? 1 : 0) << 26));
                this.getLevel().setBlock(this, get(AIR), true);
                this.getLevel().setBlock(to, this, true);
                return;
            }
        }
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
    public int getToolType() {
        return V1_21_50.isAvailable() ? super.getToolType() : BlockToolType.PICKAXE;
    }

    @Override
    public boolean canHarvestWithHand() {
        return ENABLE_BLOCK_DESTROY_SPEED_COMPATIBILITY || V1_21_50.isAvailable();
    }

    @Override
    public Instrument getInstrument() {
        return Instrument.BASS_DRUM;
    }
}
