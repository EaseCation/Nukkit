package cn.nukkit.event.block;

import cn.nukkit.block.Block;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.event.HandlerList;

public class ModBlockEntityTickEvent extends BlockEvent {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final BlockEntity blockEntity;

    public ModBlockEntityTickEvent(BlockEntity blockEntity) {
        super(null);
        this.blockEntity = blockEntity;
    }

    @Override
    public Block getBlock() {
        return blockEntity.getBlock();
    }
}
