package cn.nukkit.entity.knockback;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 击退 Profile 管理器，维护全局注册的 Profile 实例。
 */
public class KnockbackManager {

    private static final KnockbackManager INSTANCE = new KnockbackManager();

    private final Map<String, KnockbackProfile> profiles = new HashMap<>();
    private KnockbackProfile defaultProfile;

    private KnockbackManager() {
        // 默认 Profile：保留当前行为（motionX/Y/Z 不随客户端移动更新的 bug 行为）
        defaultProfile = new KnockbackProfile("default");
        register(defaultProfile);

        // 修正版 Profile：使用 player.speed 获取真实速度
        register(new KnockbackProfile("corrected").setUseRealVelocity(true));

        // PC 房间 Profile：更高的基础击退值
        register(new KnockbackProfile("pc").setBaseH(0.37f).setBaseV(0.35f).setUseRealVelocity(false));

        // PC 修正版
        register(new KnockbackProfile("pc-corrected").setBaseH(0.37f).setBaseV(0.35f).setUseRealVelocity(true));

        // JE（Java Edition）房间 Profile：对齐 KnockbackMaster 插件 default 手感（类 1.8 PvP）
        // normal=0.4/0.36，sprinting 绝对值替换=0.8/0.42，air 空中乘数=0.6/0.8
        register(new KnockbackProfile("je")
                .setBaseH(0.4f).setBaseV(0.36f)
                .setSprintBaseH(0.8f).setSprintBaseV(0.42f)
                .setAirMultiplierH(0.6f).setAirMultiplierV(0.8f)
                .setEnchantBonusH(0.4f).setEnchantBonusV(0.35f) // 击退附魔加成（近似 KnockbackMaster 的乘数手感）
                .setHorizontalMax(1.3f) // 放宽水平上限，容纳地面疾跑(0.8)+击退附魔，避免方向修正截断
                .setUseRealVelocity(false));
    }

    public static KnockbackManager get() {
        return INSTANCE;
    }

    public void register(KnockbackProfile profile) {
        profiles.put(profile.getName(), profile);
    }

    public KnockbackProfile getProfile(String name) {
        return profiles.get(name);
    }

    public KnockbackProfile getDefaultProfile() {
        return defaultProfile;
    }

    public void setDefaultProfile(KnockbackProfile profile) {
        this.defaultProfile = profile;
    }

    public Collection<KnockbackProfile> getProfiles() {
        return Collections.unmodifiableCollection(profiles.values());
    }
}
