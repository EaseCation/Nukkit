package cn.nukkit.block;

import cn.nukkit.event.redstone.RedstoneUpdateEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;

/**
 * @author Pub4Game
 */
public class BlockRedstoneLampLit extends BlockRedstoneLamp {

    public BlockRedstoneLampLit() {
    }

    @Override
    public String getName() {
        return "Lit Redstone Lamp";
    }

    @Override
    public int getId() {
        return LIT_REDSTONE_LAMP;
    }

    @Override
    public int getLightLevel() {
        return 15;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(REDSTONE_LAMP);
    }

    @Override
    public int onUpdate(int type) {
        if ((type == Level.BLOCK_UPDATE_NORMAL || type == Level.BLOCK_UPDATE_REDSTONE) && !this.level.isBlockPowered(this)) {
            if (!this.level.isRedstoneEnabled()) {
                return 0;
            }

            // Redstone event
            RedstoneUpdateEvent ev = new RedstoneUpdateEvent(this);
            getLevel().getServer().getPluginManager().callEvent(ev);
            if (ev.isCancelled()) {
                return 0;
            }
            this.level.scheduleUpdate(this, 4);
            return 1;
        }

        if (type == Level.BLOCK_UPDATE_SCHEDULED && !this.level.isBlockPowered(this)) {
            this.level.setBlock(this, Block.get(BlockID.REDSTONE_LAMP), true, false);
        }
        return 0;
    }
}
