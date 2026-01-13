package cn.nukkit.item;

import cn.nukkit.utils.DyeColor;

public class ItemGlowStick extends ItemChemicalTickable {
    public ItemGlowStick() {
        this(0, 1);
    }

    public ItemGlowStick(Integer meta) {
        this(meta, 1);
    }

    public ItemGlowStick(Integer meta, int count) {
        super(GLOW_STICK, meta, count, DyeColor.getByDyeData(meta != null ? meta : 0).getName() + " Glow Stick");
    }

    @Override
    public String getDescriptionId() {
        return "item.glow_stick." + getColor().getDescriptionName() + ".name";
    }

    @Override
    public int getDefaultMeta() {
        return 1;
    }

    @Override
    public boolean isValidMeta(int meta) {
        meta &= 0xf;
        return meta != DyeColor.BLACK.getDyeData() && meta != DyeColor.LIGHT_GRAY.getDyeData();
    }

    @Override
    protected int getTickRate() {
        return 30 * 20;
    }
}
