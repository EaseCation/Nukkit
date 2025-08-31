package cn.nukkit.block;

import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.item.Item;

public class BlockItemFrameGlow extends BlockItemFrame {
    public BlockItemFrameGlow() {
        this(0);
    }

    public BlockItemFrameGlow(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return BLOCK_GLOW_FRAME;
    }

    @Override
    public String getName() {
        return "Glow Item Frame";
    }

    @Override
    public int getBlockEntityType() {
        return BlockEntityType.GLOW_ITEM_FRAME;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(Item.GLOW_FRAME);
    }

    @Override
    protected String getBlockEntityId() {
        return BlockEntity.GLOW_ITEM_FRAME;
    }
}
