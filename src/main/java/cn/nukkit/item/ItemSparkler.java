package cn.nukkit.item;

import cn.nukkit.utils.DyeColor;

public class ItemSparkler extends ItemChemicalTickable {
    public ItemSparkler() {
        this(0, 1);
    }

    public ItemSparkler(Integer meta) {
        this(meta, 1);
    }

    public ItemSparkler(Integer meta, int count) {
        super(SPARKLER, meta, count, DyeColor.getByDyeData(meta != null ? meta : 0).getName() + " Sparkler");
    }

    @Override
    public boolean canDualWield() {
        return true;
    }

    @Override
    protected int getTickRate() {
        return 5 * 20;
    }
}
