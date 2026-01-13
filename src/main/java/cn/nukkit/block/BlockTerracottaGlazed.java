package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.DyeColor;
import cn.nukkit.utils.Faceable;

/**
 * Created by CreeperFace on 2.6.2017.
 */
public abstract class BlockTerracottaGlazed extends BlockSolid implements Faceable {
    public static final int[] GLAZED_TERRACOTTAS = {
            WHITE_GLAZED_TERRACOTTA,
            ORANGE_GLAZED_TERRACOTTA,
            MAGENTA_GLAZED_TERRACOTTA,
            LIGHT_BLUE_GLAZED_TERRACOTTA,
            YELLOW_GLAZED_TERRACOTTA,
            LIME_GLAZED_TERRACOTTA,
            PINK_GLAZED_TERRACOTTA,
            GRAY_GLAZED_TERRACOTTA,
            SILVER_GLAZED_TERRACOTTA,
            CYAN_GLAZED_TERRACOTTA,
            PURPLE_GLAZED_TERRACOTTA,
            BLUE_GLAZED_TERRACOTTA,
            BROWN_GLAZED_TERRACOTTA,
            GREEN_GLAZED_TERRACOTTA,
            RED_GLAZED_TERRACOTTA,
            BLACK_GLAZED_TERRACOTTA,
    };

    BlockTerracottaGlazed() {

    }

    @Override
    public int getBlockDefaultMeta() {
        return 3;
    }

    @Override
    public float getResistance() {
        return 7;
    }

    @Override
    public float getHardness() {
        return 1.4f;
    }

    @Override
    public int getToolType() {
        return BlockToolType.PICKAXE;
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        return item.getTier() >= ItemTool.TIER_WOODEN ? new Item[]{this.toItem(true)} : new Item[0];
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        this.setDamage(player != null ? player.getDirection().getOpposite().getIndex() : getBlockDefaultMeta());
        return this.getLevel().setBlock(block, this, true, true);
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromIndex(this.getDamage() & 0x07);
    }

    @Override
    public boolean canBePulled() {
        return false;
    }

    @Override
    public float getFurnaceXpMultiplier() {
        return 0.1f;
    }

    public abstract DyeColor getDyeColor();

    @Override
    public String getDescriptionId() {
        return "tile.glazedTerracotta" + getDyeColor().getDescriptionNamePascalCase() + ".name";
    }
}
