package cn.nukkit.block;

public class BlockLanternSoul extends BlockLantern {
    public BlockLanternSoul() {
        this(0);
    }

    public BlockLanternSoul(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return SOUL_LANTERN;
    }

    @Override
    public String getName() {
        return "Soul Lantern";
    }

    @Override
    public int getLightLevel() {
        return 10;
    }
}
