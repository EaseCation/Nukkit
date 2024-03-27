package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.utils.BlockColor;

/**
 * @author Nukkit Project Team
 */
public class BlockPressurePlateWood extends BlockPressurePlateBase {

    public BlockPressurePlateWood(int meta) {
        super(meta);
        this.onPitch = 0.8f;
        this.offPitch = 0.7f;
    }

    public BlockPressurePlateWood() {
        this(0);
    }

    @Override
    public String getName() {
        return "Oak Pressure Plate";
    }

    @Override
    public int getId() {
        return WOODEN_PRESSURE_PLATE;
    }

    @Override
    public int getToolType() {
        return BlockToolType.AXE;
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
    public Item[] getDrops(Item item, Player player) {
        return new Item[]{
                toItem(true)
        };
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WOOD_BLOCK_COLOR;
    }

    @Override
    protected int computeRedstoneStrength() {
        AxisAlignedBB bb = getCollisionBoundingBox();

        for (Entity entity : this.level.getCollidingEntities(bb)) {
            if (entity.doesTriggerPressurePlate()) {
                return 15;
            }
        }

        return 0;
    }
}