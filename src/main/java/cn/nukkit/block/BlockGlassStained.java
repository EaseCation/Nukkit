package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

import static cn.nukkit.GameVersion.*;

/**
 * Created by CreeperFace on 7.8.2017.
 */
public class BlockGlassStained extends BlockGlass {

    public BlockGlassStained() {
        this(0);
    }

    public BlockGlassStained(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return STAINED_GLASS;
    }

    @Override
    public boolean isStackedByData() {
        return !V1_20_30.isAvailable();
    }

    @Override
    public String getName() {
        return getDyeColor().getName() + " Stained Glass";
    }

    @Override
    public BlockColor getColor() {
        return DyeColor.getByWoolData(getDamage()).getColor();
    }

    @Override
    public float getFurnaceXpMultiplier() {
        return 0;
    }

    public DyeColor getDyeColor() {
        return DyeColor.getByWoolData(getDamage());
    }
}
