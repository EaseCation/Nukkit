package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;

import static cn.nukkit.GameVersion.*;

public class BlockLightningRod extends BlockTransparent implements CopperBehavior, Faceable {
    public static final int FACING_DIRECTION_MASK = 0b111;
    public static final int POWERED_BIT = 0b1000;

    BlockLightningRod() {

    }

    @Override
    public int getId() {
        return LIGHTNING_ROD;
    }

    @Override
    public String getName() {
        return "Lightning Rod";
    }

    @Override
    public float getHardness() {
        return 3;
    }

    @Override
    public float getResistance() {
        return 30;
    }

    @Override
    public int getToolType() {
        return BlockToolType.PICKAXE;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_STONE) {
            return new Item[]{
                    this.toItem(true),
            };
        }
        return new Item[0];
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(getItemId());
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        setDamage(face.getIndex());
        return super.place(item, block, target, face, fx, fy, fz, player);
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        switch (getBlockFace().getAxis()) {
            default:
            case Y:
                return new SimpleAxisAlignedBB(this.x + 6.0 / 16, this.y, this.z + 6.0 / 16, this.x + 1 - 6.0 / 16, this.y + 1, this.z + 1 - 6.0 / 16);
            case X:
                return new SimpleAxisAlignedBB(this.x, this.y + 6.0 / 16, this.z + 6.0 / 16, this.x + 1, this.y + 1 - 6.0 / 16, this.z + 1 - 6.0 / 16);
            case Z:
                return new SimpleAxisAlignedBB(this.x + 6.0 / 16, this.y + 6.0 / 16, this.z, this.x + 1 - 6.0 / 16, this.y + 1 - 6.0 / 16, this.z + 1);
        }
    }

    @Override
    public boolean canProvideSupport(BlockFace face, SupportType type) {
        return getBlockFace().getAxis() == face.getAxis() && type == SupportType.CENTER;
    }

    @Override
    public boolean canContainWater() {
        return true;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.ORANGE_BLOCK_COLOR;
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, float fx, float fy, float fz, Player player) {
        if (!hasCopperBehavior()) {
            return false;
        }
        return CopperBehavior.use(this, this, item, player);
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_RANDOM) {
            if (!hasCopperBehavior()) {
                return 0;
            }
            CopperBehavior.randomTick(this, this);
            return type;
        }
        return 0;
    }

    @Override
    public boolean hasCopperBehavior() {
        return V1_21_111.isAvailable();
    }

    @Override
    public int getCopperAge() {
        return 0;
    }

    @Override
    public int getWaxedBlockId() {
        return WAXED_LIGHTNING_ROD;
    }

    @Override
    public int getDewaxedBlockId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getIncrementAgeBlockId() {
        return EXPOSED_LIGHTNING_ROD;
    }

    @Override
    public int getDecrementAgeBlockId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromIndex(getDamage() & FACING_DIRECTION_MASK);
    }

    @Override
    public boolean isLightningRod() {
        return true;
    }
}
