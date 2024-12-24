package cn.nukkit.block;

import cn.nukkit.item.Item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BlockUnknown extends Block {

    private final int id;

    public BlockUnknown(int id) {
        this(id, 0);
    }

    public BlockUnknown(int id, Integer meta) {
        super(meta);
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return "Missing";
    }

    @Override
    public Item toItem() {
        return Item.get(Item.AIR);
    }
}
