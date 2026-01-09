package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.Items;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BlockUnknown extends Block {

    private final int id;

    public BlockUnknown(int id) {
        this(id, 0);
    }

    public BlockUnknown(int id, int meta) {
        this.id = id;
        init(meta);
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
    public Item toItem(boolean addUserData) {
        return Items.air();
    }
}
