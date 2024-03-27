package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.event.block.BlockFadeEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Dimension;
import cn.nukkit.level.Level;
import cn.nukkit.utils.BlockColor;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BlockIce extends BlockTransparent {

    public BlockIce() {
    }

    @Override
    public int getId() {
        return ICE;
    }

    @Override
    public String getName() {
        return "Ice";
    }

    @Override
    public float getResistance() {
        return 2.5f;
    }

    @Override
    public float getHardness() {
        return 0.5f;
    }

    @Override
    public float getFrictionFactor() {
        return 0.98f;
    }

    @Override
    public int getToolType() {
        return BlockToolType.PICKAXE;
    }

    @Override
    public boolean onBreak(Item item, Player player) {
        if (this.getLevel().getDimension() != Dimension.NETHER && (player == null || player.isSurvivalLike()) && !item.hasEnchantment(Enchantment.SILK_TOUCH)) {
            return this.getLevel().setBlock(this, Block.get(BlockID.WATER), true);
        } else {
            return super.onBreak(item, player);
        }
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_RANDOM) {
            if (level.getBlockLightAt((int) this.x, (int) this.y, (int) this.z) >= 12) {
                BlockFadeEvent event = new BlockFadeEvent(this, level.getDimension() == Dimension.NETHER ? get(AIR) : get(WATER));
                level.getServer().getPluginManager().callEvent(event);
                if (!event.isCancelled()) {
                    level.setBlock(this, event.getNewState(), true);
                }
                return Level.BLOCK_UPDATE_RANDOM;
            }
        }
        return 0;
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        return new Item[0];
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.ICE_BLOCK_COLOR;
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }
}
