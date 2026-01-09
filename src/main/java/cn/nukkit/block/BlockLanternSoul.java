package cn.nukkit.block;

public class BlockLanternSoul extends BlockLantern {
    BlockLanternSoul() {

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
