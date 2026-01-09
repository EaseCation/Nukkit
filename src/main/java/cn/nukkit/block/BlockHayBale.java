package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockFace.Axis;
import cn.nukkit.utils.BlockColor;

/**
 * Created on 2015/11/24 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockHayBale extends BlockSolid {
    public static final int PILLAR_AXIS_Y = 0b00;
    public static final int PILLAR_AXIS_X = 0b01;
    public static final int PILLAR_AXIS_Z = 0b10;
    public static final int PILLAR_AXIS_OFFSET = 2;
    public static final int PILLAR_AXIS_MASK = (PILLAR_AXIS_X | PILLAR_AXIS_Z) << PILLAR_AXIS_OFFSET;

    BlockHayBale() {

    }

    @Override
    public int getId() {
        return HAY_BLOCK;
    }

    @Override
    public String getName() {
        return "Hay Bale";
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
    public int getBurnChance() {
        return 60;
    }

    @Override
    public int getBurnAbility() {
        return 20;
    }

    @Override
    public int getToolType() {
        return BlockToolType.HOE;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        this.setDamage(face.getAxis().getIndex() << 2);
        this.getLevel().setBlock(block, this, true, true);

        return true;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.YELLOW_BLOCK_COLOR;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(this.getId(), 0);
    }

    @Override
    public int getCompostableChance() {
        return 85;
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
