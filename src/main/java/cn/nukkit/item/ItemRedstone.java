package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.item.armortrim.TrimMaterialNames;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemRedstone extends Item {

    public ItemRedstone() {
        this(0, 1);
    }

    public ItemRedstone(Integer meta) {
        this(meta, 1);
    }

    public ItemRedstone(Integer meta, int count) {
        super(REDSTONE, meta, count, "Redstone Dust");
        this.block = Block.get(BlockID.REDSTONE_WIRE);
    }

    @Override
    public float getFurnaceXpMultiplier() {
        return 0.3f;
    }

    @Override
    public String getTrimMaterialName() {
        return TrimMaterialNames.REDSTONE;
    }
}
