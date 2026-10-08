package cn.nukkit.network.input;

@FunctionalInterface
public interface ServerInputTask {
    void run(InboundContext context);

    /** 主线程消费前检查动作归属；失效任务按原预算丢弃，不阻挡后续连接清理。 */
    default boolean isValid() {
        return true;
    }

    /** 只在主线程判断当前是否可执行；不触发业务回调或修改玩家状态。 */
    default boolean canRunBetweenTicks() {
        return true;
    }
}
