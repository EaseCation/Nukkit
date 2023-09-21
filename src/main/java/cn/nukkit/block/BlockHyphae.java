package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.network.protocol.LevelSoundEventPacket;

import static cn.nukkit.GameVersion.*;

public abstract class BlockHyphae extends BlockRotatedPillar {
    protected BlockHyphae(int meta) {
        super(meta);
    }

    @Override
    public double getHardness() {
        if (V1_20_30.isAvailable()) {
            return 2;
        }
        return 0.3;
    }

    @Override
    public double getResistance() {
        return 10;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
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
