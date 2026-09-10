package cn.nukkit.event.block;

import cn.nukkit.block.Block;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

/**
 * 海绵开始吸水前触发。取消后整次吸水停止，主层水、含水副层和海绵状态均不改变。
 */
public class SpongeAbsorbEvent extends BlockEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    public SpongeAbsorbEvent(Block block) {
        super(block);
    }
}
