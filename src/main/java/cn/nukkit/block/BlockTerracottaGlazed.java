package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.Faceable;

/**
 * Created by CreeperFace on 2.6.2017.
 */
public abstract class BlockTerracottaGlazed extends BlockSolid implements Faceable {

    private static final int[] FACES = {2, 5, 3, 4};

    public BlockTerracottaGlazed() {
        this(0);
    }

    public BlockTerracottaGlazed(int meta) {
        super(meta);
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
        this.setDamage(FACES[player != null ? player.getDirection().getHorizontalIndex() : 0]);
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
}
