package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockFace.Axis;
import cn.nukkit.utils.BlockColor;

/**
 * @author CreeperFace
 */
public class BlockBone extends BlockSolid {
    public static final int PILLAR_AXIS_Y = 0b00;
    public static final int PILLAR_AXIS_X = 0b01;
    public static final int PILLAR_AXIS_Z = 0b10;
    public static final int PILLAR_AXIS_OFFSET = 2;
    public static final int PILLAR_AXIS_MASK = (PILLAR_AXIS_X | PILLAR_AXIS_Z) << PILLAR_AXIS_OFFSET;

    BlockBone() {

    }

    @Override
    public int getId() {
        return BONE_BLOCK;
    }

    @Override
    public String getName() {
        return "Bone Block";
    }

    @Override
    public float getHardness() {
        return 2;
    }

    @Override
    public float getResistance() {
        return 10;
    }

    @Override
    public int getToolType() {
        return BlockToolType.PICKAXE;
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            return new Item[]{toItem(true)};
        }

        return new Item[0];
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(this.getItemId());
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        this.setDamage(face.getAxis().getIndex() << 2);
        this.getLevel().setBlock(block, this, true);
        return true;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.SAND_BLOCK_COLOR;
    }

    public int getPillarAxis() {
        return (getDamage() & PILLAR_AXIS_MASK) >> PILLAR_AXIS_OFFSET;
    }

    public void setPillarAxis(int axis) {
        setDamage(axis & PILLAR_AXIS_MASK >> PILLAR_AXIS_OFFSET);
    }

    public Axis getAxis() {
        return Axis.fromIndex(getPillarAxis());
    }
}
