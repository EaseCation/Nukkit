package cn.nukkit.event.block;

import cn.nukkit.block.Block;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

/**
 * 无玩家参与的 useBreakOn 调用在破坏方块前触发，包括失去支撑和程序触发的破坏。
 * 取消后不会破坏方块、关闭方块实体、产生破坏粒子或掉落物。
 */
public class BlockNaturalBreakEvent extends BlockEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    public BlockNaturalBreakEvent(Block block) {
        super(block);
    }
}
