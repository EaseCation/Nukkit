package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.network.protocol.AnimatePacket.SwingSource;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.potion.Potion;
import cn.nukkit.utils.BlockColor;

/**
 * author: MagicDroidX
 * AMAZING COARSE DIRT added by kvetinac97
 * Nukkit Project
 */
public class BlockDirt extends BlockSolid {
    public static final int[] DIRTS = {
            DIRT,
            COARSE_DIRT,
    };

    public static final int TYPE_NORMAL_DIRT = 0;
    public static final int TYPE_COARSE_DIRT = 1;

    BlockDirt() {

    }

    @Override
    public int getId() {
        return DIRT;
    }

    @Override
    public boolean canBeActivated() {
        return true;
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
    public int getToolType() {
        return BlockToolType.SHOVEL;
    }

    @Override
    public String getName() {
        return "Dirt";
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, float fx, float fy, float fz, Player player) {
        if (item.isHoe()) {
            if (this.up().isAir()) {
                int newId = getHoedBlockId();
                level.addLevelSoundEvent(blockCenter(), LevelSoundEventPacket.SOUND_ITEM_USE_ON, getFullId(newId));
                if (player != null) {
                    player.swingArm(SwingSource.USE_ITEM);
                    if (player.isSurvivalLike() && item.hurtAndBreak(1) < 0) {
                        item.pop();
                        player.level.addLevelSoundEvent(player, LevelSoundEventPacket.SOUND_BREAK);
                    }
                }
                this.getLevel().setBlock(this, get(newId), true);
                return true;
            }
        } else if (item.isShovel()) {
            if (this.up().isAir()) {
                level.addLevelSoundEvent(blockCenter(), LevelSoundEventPacket.SOUND_ITEM_USE_ON, getFullId(GRASS_PATH));
                if (player != null && player.isSurvivalLike() && item.hurtAndBreak(1) < 0) {
                    item.pop();
                    player.level.addLevelSoundEvent(player, LevelSoundEventPacket.SOUND_BREAK);
                }
                this.getLevel().setBlock(this, get(GRASS_PATH), true);
                return true;
            }
        } else if (item.isFertilizer()) {
            return BlockSeagrass.trySpawnSeaGrass(this, item, player);
        } else if (item.isPotion() && item.getDamage() == Potion.WATER) {
            level.setBlock(this, get(MUD), true);

            level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_BOTTLE_EMPTY);

            if (player != null) {
                if (item.getCount() == 1 && player.isSurvivalLike()) {
                    player.getInventory().setItemInHand(Item.get(Item.GLASS_BOTTLE));
                } else {
                    if (player.isSurvivalLike()) {
                        item.pop();
                    }
                    player.getInventory().addItemOrDrop(Item.get(Item.GLASS_BOTTLE));
                }
            }
            return true;
        }

        return false;
    }

    @Override
    public boolean isFertilizable() {
        return true;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.DIRT_BLOCK_COLOR;
    }

    @Override
    public boolean isDirt() {
        return true;
    }

    protected int getHoedBlockId() {
        return FARMLAND;
    }
}
