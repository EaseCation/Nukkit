package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.utils.BlockColor;

/**
 * author: MagicDroidX
 * AMAZING COARSE DIRT added by kvetinac97
 * Nukkit Project
 */
public class BlockDirt extends BlockSolid {
    public static final int TYPE_NORMAL_DIRT = 0;
    public static final int TYPE_COARSE_DIRT = 1;

    public BlockDirt() {
        this(0);
    }

    public BlockDirt(int meta){
        super(meta);
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
        return this.getDamage() == TYPE_NORMAL_DIRT ? "Dirt" : "Coarse Dirt";
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, float fx, float fy, float fz, Player player) {
        if (item.isHoe()) {
            if (this.up().isAir()) {
                int newId = getDamage() == TYPE_NORMAL_DIRT ? FARMLAND : DIRT;
                level.addLevelSoundEvent(blockCenter(), LevelSoundEventPacket.SOUND_ITEM_USE_ON, getFullId(newId));
                if (player != null) {
                    player.swingArm();
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
        }

        return false;
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        return new Item[]{
                Item.get(DIRT)
        };
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.DIRT_BLOCK_COLOR;
    }
}
