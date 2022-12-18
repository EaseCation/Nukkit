package cn.nukkit.block;

import cn.nukkit.event.block.BlockFadeEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;

//和pm源码有点出入，这里参考了wiki

/**
 * Created on 2015/12/6 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockOreRedstoneGlowing extends BlockOreRedstone {

    public BlockOreRedstoneGlowing() {
    }

    @Override
    public String getName() {
        return "Glowing Redstone Ore";
    }

    @Override
    public int getId() {
        return LIT_REDSTONE_ORE;
    }

    @Override
    public int getLightLevel() {
        return 9;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(getItemId(getUnlitBlockId()));
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_SCHEDULED || type == Level.BLOCK_UPDATE_RANDOM) {
            BlockFadeEvent event = new BlockFadeEvent(this, get(getUnlitBlockId()));
            level.getServer().getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                level.setBlock(this, event.getNewState(), false, false);
            }

            return Level.BLOCK_UPDATE_WEAK;
        }

        return 0;
    }

    @Override
    public Item getSilkTouchResource() {
        return toItem(true);
    }

    @Override
    protected int getLitBlockId() {
        throw new UnsupportedOperationException();
    }

    protected int getUnlitBlockId() {
        return REDSTONE_ORE;
    }
}
