package cn.nukkit.network.input;

@FunctionalInterface
public interface ServerInputTask {
    void run(InboundContext context);

    /**
     * 仅返回服务端定义的任务类型，不得包含玩家数据或包载荷；诊断不执行业务逻辑。
     */
    default String diagnosticType() {
        return "ControlOrUnclassified";
    }

    /** 主线程消费前检查动作归属；失效任务按原预算丢弃，不阻挡后续连接清理。 */
    default boolean isValid() {
        return true;
    }

    /** 只在主线程判断当前是否可执行；不触发业务回调或修改玩家状态。 */
    default boolean canRunBetweenTicks() {
        return true;
    }
}
