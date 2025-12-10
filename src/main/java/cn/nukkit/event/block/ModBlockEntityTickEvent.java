package cn.nukkit.event.block;

import cn.nukkit.block.Block;
import cn.nukkit.blockentity.BlockEntityModBlock;
import cn.nukkit.event.HandlerList;

public class ModBlockEntityTickEvent extends BlockEvent {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final BlockEntityModBlock blockEntity;

    public ModBlockEntityTickEvent(BlockEntityModBlock blockEntity) {
        super(null);
        this.blockEntity = blockEntity;
    }

    public BlockEntityModBlock getBlockEntity() {
        return blockEntity;
    }

    @Override
    public Block getBlock() {
        return blockEntity.getBlock();
    }
}
