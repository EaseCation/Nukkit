package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.utils.BlockColor;

/**
 * Created on 2015/11/22 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockGrassPath extends BlockGrass {

    public BlockGrassPath() {
    }

    @Override
    public int getId() {
        return GRASS_PATH;
    }

    @Override
    public String getName() {
        return "Dirt Path";
    }

    @Override
    public int getToolType() {
        return BlockToolType.SHOVEL;
    }

    @Override
    public float getHardness() {
        return 0.65f;
    }

    @Override
    public float getResistance() {
        return 3.25f;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.DIRT_BLOCK_COLOR;
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }

    @Override
    public int onUpdate(int type) {
        return 0;
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, float fx, float fy, float fz, Player player) {
        if (item.isHoe()) {
            level.addLevelSoundEvent(blockCenter(), LevelSoundEventPacket.SOUND_ITEM_USE_ON, getFullId(FARMLAND));
            if (player != null) {
                player.swingArm();
                if (player.isSurvivalLike() && item.hurtAndBreak(1) < 0) {
                    item.pop();
                    player.level.addLevelSoundEvent(player, LevelSoundEventPacket.SOUND_BREAK);
                }
            }
            this.getLevel().setBlock(this, get(FARMLAND), true);
            return true;
        }

        return false;
    }

    @Override
    public double getMaxY() {
        return this.y + 1 - 1 / 16.0;
    }

    @Override
    public boolean isDirt() {
        return false;
    }
}
