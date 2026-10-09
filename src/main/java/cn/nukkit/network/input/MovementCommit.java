package cn.nukkit.network.input;

import cn.nukkit.Player;
import cn.nukkit.level.Level;

import java.util.UUID;

/** 最终服务端位置快照；回调后必须重新核对会话与位置代际。 */
public record MovementCommit(MovementCommitResult result, int serverTick, long movementEpoch,
                             UUID sessionId, Level level, double x, double y, double z,
                             double yaw, double pitch, boolean onGround) {
    public static MovementCommit capture(Player player, MovementCommitResult result, long expectedEpoch) {
        return new MovementCommit(result, player.getServer().getTick(), expectedEpoch,
                player.getSessionId(), player.getLevel(), player.x, player.y, player.z,
                player.yaw, player.pitch, player.isOnGround());
    }

    public boolean isAccepted() {
        return this.result == MovementCommitResult.APPLIED || this.result == MovementCommitResult.UNCHANGED;
    }

    public boolean isCurrent(Player player) {
        return !player.isClosed() && player.isOnline() && player.isAlive()
                && this.sessionId.equals(player.getSessionId()) && this.level == player.getLevel()
                && this.movementEpoch == player.getMovementEpoch()
                && this.x == player.x && this.y == player.y && this.z == player.z
                && this.yaw == player.yaw && this.pitch == player.pitch && this.onGround == player.isOnGround();
    }
}
