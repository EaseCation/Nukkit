package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

public class BlockLight extends BlockTransparentMeta {

    public BlockLight() {
        this(0);
    }

    public BlockLight(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return LIGHT_BLOCK;
    }

    @Override
    public String getName() {
        return "Light Block";
    }

    @Override
    public double getHardness() {
        return -1;
    }

    @Override
    public double getResistance() {
        return 18000004;
    }

    @Override
    public boolean canBeReplaced() {
        return true;
    }

    @Override
    public boolean isBreakable(Item item) {
        return false;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.AIR_BLOCK_COLOR;
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        return null;
    }

    @Override
    public boolean canPassThrough() {
        return true;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public boolean canContainWater() {
        return true;
    }

    @Override
    public boolean canContainFlowingWater() {
        return true;
    }

    @Override
    public int getLightLevel() {
        return getDamage();
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, Player player) {
        if (player != null && !player.isCreative() || item.getId() != getItemId()) {
            return false;
        }

        setDamage((this.getDamage() + 1) & 0xf);
        level.setBlock(this, this, true);
        return true;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(ItemID.AIR);
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public boolean canProvideSupport(BlockFace face, SupportType type) {
        return false;
    }
}
