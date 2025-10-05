package cn.nukkit.item;

import cn.nukkit.entity.Entity;
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
    public int getDefaultMeta() {
        return 4;
    }

    @Override
    public boolean isValidMeta(int meta) {
        meta &= 0xf;
        return meta == DyeColor.RED.getDyeData()
                || meta == DyeColor.GREEN.getDyeData()
                || meta == DyeColor.BLUE.getDyeData()
                || meta == DyeColor.PURPLE.getDyeData()
                || meta == DyeColor.ORANGE.getDyeData();
    }

    @Override
    public boolean canDualWield() {
        return true;
    }

    @Override
    protected int getTickRate() {
        return 5 * 20;
    }

    @Override
    public boolean tick(Entity entity) {
        if (isActivated() && entity.isInsideOfWater()) {
            pop();
            return true;
        }
        return super.tick(entity);
    }
}
