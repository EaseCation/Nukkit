package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.BlockFace;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.potion.Potion;
import cn.nukkit.utils.BlockColor;

public class BlockDirtRooted extends BlockSolid {
    public BlockDirtRooted() {
    }

    @Override
    public int getId() {
        return DIRT_WITH_ROOTS;
    }

    @Override
    public String getName() {
        return "Rooted Dirt";
    }

    @Override
    public float getHardness() {
        return 0.5f;
    }

    @Override
    public float getResistance() {
        return 2.5f;
    }

    @Override
    public int getToolType() {
        return BlockToolType.SHOVEL;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, float fx, float fy, float fz, Player player) {
        if (item.isFertilizer()) {
            if (!down().isAir()) {
                return false;
            }

            if (player != null && !player.isCreative()) {
                item.pop();
            }

            level.addParticle(new BoneMealParticle(this));

            level.setBlock(downVec(), get(HANGING_ROOTS), true);
            return true;
        }

        if (item.isHoe()) {
            if (!up().isAir()) {
                return false;
            }

            level.addLevelSoundEvent(blockCenter(), LevelSoundEventPacket.SOUND_ITEM_USE_ON, getFullId(DIRT));
            if (player != null) {
                player.swingArm();
                if (player.isSurvivalLike() && item.hurtAndBreak(1) < 0) {
                    item.pop();
                    player.level.addLevelSoundEvent(player, LevelSoundEventPacket.SOUND_BREAK);
                }
            }

            level.setBlock(this, get(DIRT), true);

            level.dropItem(upVec(), Item.get(getItemId(HANGING_ROOTS)));
            return true;
        }

        if (item.isShovel()) {
            if (!up().isAir()) {
                return false;
            }

            level.addLevelSoundEvent(blockCenter(), LevelSoundEventPacket.SOUND_ITEM_USE_ON, getFullId(GRASS_PATH));
            if (player != null && player.isSurvivalLike() && item.hurtAndBreak(1) < 0) {
                item.pop();
                player.level.addLevelSoundEvent(player, LevelSoundEventPacket.SOUND_BREAK);
            }

            level.setBlock(this, get(GRASS_PATH), true);
            return true;
        }

        if (item.isPotion() && item.getDamage() == Potion.WATER) {
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
}
