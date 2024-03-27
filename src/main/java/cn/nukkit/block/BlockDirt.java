package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemDye;
import cn.nukkit.item.ItemID;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

/**
 * author: MagicDroidX
 * AMAZING COARSE DIRT added by kvetinac97
 * Nukkit Project
 */
public class BlockDirt extends BlockSolidMeta {
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
    public boolean onActivate(Item item, BlockFace face, Player player) {
        if (item.isHoe()) {
            if (this.up().isAir()) {
                if (player != null && !player.isCreative()) {
                    item.useOn(this);
                }
                this.getLevel().setBlock(this, this.getDamage() == TYPE_NORMAL_DIRT ? get(FARMLAND) : get(DIRT), true);
                return true;
            }
        } else if (item.isShovel()) {
            if (this.up().isAir()) {
                if (player != null && !player.isCreative()) {
                    item.useOn(this);
                }
                this.getLevel().setBlock(this, get(GRASS_PATH), true);
                return true;
            }
        } else if (item.getId() == ItemID.DYE && item.getDamage() == ItemDye.BONE_MEAL) {
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
