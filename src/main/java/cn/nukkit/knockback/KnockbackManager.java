package cn.nukkit.knockback;

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
