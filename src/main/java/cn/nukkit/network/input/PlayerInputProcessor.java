package cn.nukkit.network.input;

import cn.nukkit.Player;
import cn.nukkit.network.protocol.DataPacket;

/** 主线程输入验证与最终移动结果；不依赖协议适配或反作弊实现。 */
public interface PlayerInputProcessor {
    /** 包事件已结束；取消包仍可验证客户端帧，但不得重新放行。 */
    InputValidationResult validateReceivedPacket(Player player, DataPacket packet, boolean eventCancelled);

    /** 只接收不可取消的服务端提交快照，预测完成不等于有效位置提交。 */
    void onMovementCommitted(Player player, MovementCommit commit);
}
