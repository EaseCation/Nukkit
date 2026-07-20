package cn.nukkit.entity;

import cn.nukkit.event.entity.EntityDamageEvent;

/**
 * 允许伤害来源在目标进入连续攻击冷却门控前补充事件属性。
 */
@FunctionalInterface
public interface EntityDamageEventPreprocessor {

    void prepareDamageEvent(EntityDamageEvent event);
}
