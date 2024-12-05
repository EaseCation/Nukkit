package cn.nukkit.block;

import cn.nukkit.math.Mth;
import cn.nukkit.utils.BlockColor;

import static cn.nukkit.GameVersion.*;
import static cn.nukkit.SharedConstants.*;

/**
 * @author CreeperFace
 */
public class BlockWeightedPressurePlateLight extends BlockPressurePlateBase {

    public BlockWeightedPressurePlateLight() {
        this(0);
    }

    public BlockWeightedPressurePlateLight(int meta) {
        super(meta);
        this.onPitch = 0.90000004f;
        this.offPitch = 0.75f;
    }

    @Override
    public int getId() {
        return LIGHT_WEIGHTED_PRESSURE_PLATE;
    }

    @Override
    public String getName() {
        return "Light Weighted Pressure Plate";
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
    public int getToolType() {
        return BlockToolType.PICKAXE;
    }

    @Override
    public boolean canHarvestWithHand() {
        return ENABLE_BLOCK_DESTROY_SPEED_COMPATIBILITY || V1_21_50.isAvailable();
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.GOLD_BLOCK_COLOR;
    }

    @Override
    protected int computeRedstoneStrength() {
        int count = Math.min(this.level.getCollidingEntities(getCollisionBoundingBox()).length, this.getMaxWeight());

        if (count > 0) {
            float f = (float) Math.min(this.getMaxWeight(), count) / (float) this.getMaxWeight();
            return Mth.ceil(f * 15.0F);
        } else {
            return 0;
        }
    }

    public int getMaxWeight() {
        return 15;
    }
}
