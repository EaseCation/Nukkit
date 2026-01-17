package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.Items;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.utils.Faceable;

import static cn.nukkit.GameVersion.*;

/**
 * @author CreeperFace
 */
public class BlockPistonHead extends BlockTransparent implements Faceable {

    BlockPistonHead() {

    }

    @Override
    public int getId() {
        return PISTON_ARM_COLLISION;
    }

    @Override
    public String getName() {
        return "Piston Head";
    }

    @Override
    public float getResistance() {
        return 7.5f;
    }

    @Override
    public float getHardness() {
        if (V1_20_30.isAvailable()) {
            return 1.5f;
        }
        return 0.5f;
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        return new Item[0];
    }

    @Override
    public boolean onBreak(Item item, Player player) {
        this.level.setBlock(this, Block.get(BlockID.AIR), true, true);
        Block piston = getSide(getBlockFace().getOpposite());

        if (piston instanceof BlockPistonBase && ((BlockPistonBase) piston).getBlockFace() == this.getBlockFace()) {
            piston.onBreak(item, player);
        }
        return true;
    }

    @Override
    public BlockFace getBlockFace() {
        BlockFace face = BlockFace.fromIndex(this.getDamage());

        return face.getHorizontalIndex() >= 0 ? face.getOpposite() : face;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public boolean canBePulled() {
        return false;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public AxisAlignedBB[] getCollisionShape(int flags) {
        //TODO: progress translated
        return switch (getBlockFace()) {
            case DOWN -> new AxisAlignedBB[]{
                    new SimpleAxisAlignedBB(x, y, z, x + 1, y + 4 / 16f, z + 1), // front
                    new SimpleAxisAlignedBB(x + 6 / 16f, y + 4 / 16f, z + 6 / 16f, x + 1 - 6 / 16f, y + 1 - 4 / 16f, z + 1 - 6 / 16f), // arm
                    new SimpleAxisAlignedBB(x + 5 / 16f, y + 1 - 4 / 16f, z + 5 / 16f, x + 1 - 5 / 16f, y + 1 /*+ 4 / 16f*/, z + 1 - 5 / 16f), // base arm
            };
            case UP -> new AxisAlignedBB[]{
                    new SimpleAxisAlignedBB(x, y + 1 - 4 / 16f, z, x + 1, y + 1, z + 1),
                    new SimpleAxisAlignedBB(x + 6 / 16f, y + 4 / 16f, z + 6 / 16f, x + 1 - 6 / 16f, y + 1 - 4 / 16f, z + 1 - 6 / 16f),
                    new SimpleAxisAlignedBB(x + 5 / 16f, y /*- 4 / 16f*/, z + 5 / 16f, x + 1 - 5 / 16f, y + 4 / 16f, z + 1 - 5 / 16f),
            };
            case SOUTH -> new AxisAlignedBB[]{
                    new SimpleAxisAlignedBB(x, y, z + 1 - 4 / 16f, x + 1, y + 1, z + 1),
                    new SimpleAxisAlignedBB(x + 6 / 16f, y + 6 / 16f, z + 4 / 16f, x + 1 - 6 / 16f, y + 1 - 6 / 16f, z + 1 - 4 / 16f),
                    new SimpleAxisAlignedBB(x + 5 / 16f, y + 5 / 16f, z /*- 4 / 16f*/, x + 1 - 5 / 16f, y + 1 - 5 / 16f, z + 4 / 16f),
            };
            case NORTH -> new AxisAlignedBB[]{
                    new SimpleAxisAlignedBB(x, y, z, x + 1, y + 1, z + 4 / 16f),
                    new SimpleAxisAlignedBB(x + 6 / 16f, y + 6 / 16f, z + 4 / 16f, x + 1 - 6 / 16f, y + 1 - 6 / 16f, z + 1 - 4 / 16f),
                    new SimpleAxisAlignedBB(x + 5 / 16f, y + 5 / 16f, z + 1 - 4 / 16f, x + 1 - 5 / 16f, y + 1 - 5 / 16f, z + 1 /*+ 4 / 16f*/),
            };
            case EAST -> new AxisAlignedBB[]{
                    new SimpleAxisAlignedBB(x + 1 - 4 / 16f, y, z, x + 1, y + 1, z + 1),
                    new SimpleAxisAlignedBB(x + 4 / 16f, y + 6 / 16f, z + 6 / 16f, x + 1 - 4 / 16f, y + 1 - 6 / 16f, z + 1 - 6 / 16f),
                    new SimpleAxisAlignedBB(x /*- 4 / 16f*/, y + 5 / 16f, z + 5 / 16f, x + 4 / 16f, y + 1 - 5 / 16f, z + 1 - 5 / 16f),
            };
            case WEST -> new AxisAlignedBB[]{
                    new SimpleAxisAlignedBB(x, y, z, x + 4 / 16f, y + 1, z + 1),
                    new SimpleAxisAlignedBB(x + 4 / 16f, y + 6 / 16f, z + 6 / 16f, x + 1 - 4 / 16f, y + 1 - 6 / 16f, z + 1 - 6 / 16f),
                    new SimpleAxisAlignedBB(x + 1 - 4 / 16f, y + 5 / 16f, z + 5 / 16f, x + 1 /*+ 4 / 16f*/, y + 1 - 5 / 16f, z + 1 - 5 / 16f),
            };
        };
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Items.air();
    }

    @Override
    public boolean canContainWater() {
        return true;
    }

    @Override
    public boolean canProvideSupport(BlockFace face, SupportType type) {
        return face == getBlockFace();
    }

    @Override
    public int getToolType() {
        return BlockToolType.PICKAXE;
    }
}
