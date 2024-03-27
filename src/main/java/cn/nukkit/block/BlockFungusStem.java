package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.network.protocol.LevelSoundEventPacket;

public abstract class BlockFungusStem extends BlockRotatedPillar {
    protected BlockFungusStem(int meta) {
        super(meta);
    }

    @Override
    public float getHardness() {
        return 2;
    }

    @Override
    public float getResistance() {
        return 10;
    }

    @Override
    public int getToolType() {
        return BlockToolType.AXE;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, Player player) {
        if (!item.isAxe()) {
            return false;
        }

        if (player != null && !player.isCreative()) {
            item.useOn(this);
        }

        level.addLevelSoundEvent(blockCenter(), LevelSoundEventPacket.SOUND_ITEM_USE_ON, getFullId());

        level.setBlock(this, getStrippedBlock(), true);
        return true;
    }

    protected abstract Block getStrippedBlock();
}
