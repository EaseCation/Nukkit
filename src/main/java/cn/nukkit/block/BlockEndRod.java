package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;

/**
 * http://minecraft.gamepedia.com/End_Rod
 *
 * @author PikyCZ
 */
public class BlockEndRod extends BlockTransparentMeta implements Faceable {

    private static final int[] FACES = {0, 1, 3, 2, 5, 4};

    public BlockEndRod() {
        this(0);
    }

    public BlockEndRod(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "End Rod";
    }

    @Override
    public int getId() {
        return END_ROD;
    }

    @Override
    public double getHardness() {
        return 0;
    }

    @Override
    public double getResistance() {
        return 0;
    }

    @Override
    public int getLightLevel() {
        return 14;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
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
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        this.setDamage(FACES[player != null ? face.getIndex() : 0]);
        this.getLevel().setBlock(block, this, true, true);

        return true;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(this.getItemId());
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(this.getDamage() & 0x07);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.AIR_BLOCK_COLOR;
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
    public boolean isSolid() {
        return false;
    }

    @Override
    public boolean canProvideSupport(BlockFace face, SupportType type) {
        return false;
    }
}
