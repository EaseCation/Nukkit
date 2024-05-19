package cn.nukkit.item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemNetherBrick extends Item {

    public ItemNetherBrick() {
        this(0, 1);
    }

    public ItemNetherBrick(Integer meta) {
        this(meta, 1);
    }

    public ItemNetherBrick(Integer meta, int count) {
        super(NETHERBRICK, meta, count, "Nether Brick");
    }

    @Override
    public float getFurnaceXpMultiplier() {
        return 0.1f;
    }
}
