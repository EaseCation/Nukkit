package cn.nukkit.entity.knockback;

/**
 * 击退配置 Profile，承载所有击退相关的参数。
 * <p>
 * 每个实体可绑定一个 Profile，决定该实体被攻击时的击退行为。
 * 事件系统中的 per-hit Profile 是实体 Profile 的副本，可在事件处理中进一步修改。
 */
public class KnockbackProfile {

    private final String name;

    // === 基础击退值 ===
    private float baseH = 0.29f;
    private float baseV = 0.29f;

    // === 附魔击退 ===
    private float enchantBonusH = 0.1f;
    private float enchantBonusV = 0.1f;
    private int enchantLevel = 0;

    // === 算法参数 ===
    private float friction = 0.5f;
    private boolean inheritHorizontal = true;
    private boolean inheritVertical = true;
    private float inheritRatioH = 1.0f;
    private float inheritRatioV = 1.0f;
    private boolean useRealVelocity = false;
    private float verticalLimit = -1;
    private float horizontalMin = 0.2f;
    private float horizontalMax = 0.58f;
    private boolean directionCorrection = true;

    // === 疾跑相关 ===
    private float sprintMultiplierH = 1.0f;
    private float sprintMultiplierV = 1.0f;
    private boolean stopSprinting = false;
    private float sprintSlowdownH = 1.0f;
    // 疾跑绝对值替换：攻击者疾跑时直接用此基础值替换 baseH/baseV（-1 表示不启用，沿用 sprintMultiplier 乘数逻辑）
    private float sprintBaseH = -1;
    private float sprintBaseV = -1;

    // === 地面状态 ===
    private float groundMultiplierH = 1.0f;
    private float groundMultiplierV = 1.0f;

    // === 空中状态（受害者不在地面时的乘数，默认 1.0 无效果） ===
    private float airMultiplierH = 1.0f;
    private float airMultiplierV = 1.0f;

    // === 高度差限制 ===
    private boolean limitYDifference = false;
    private float yDifferenceLimit = 1.2f;

    // === 来源分类基础值（-1 表示使用 baseH/baseV） ===
    private float bowBaseH = -1;
    private float bowBaseV = -1;
    private float projectileBaseH = -1;
    private float projectileBaseV = -1;
    private float tridentBaseH = -1;
    private float tridentBaseV = -1;
    private float rodBaseH = 0;
    private float rodBaseV = 0;

    // === 鱼竿 motion 参数 ===
    private boolean rodHitMotion = true;
    private float rodHitMotionDivisor = 15.0f;
    private float rodHitMotionVertical = 0.3f;
    private boolean rodReelPullBack = true;
    private boolean rodReelMotionEC = true;
    private float rodReelECMotionDivisor = 8.0f;
    private float rodReelECMotionVertical = 0.3f;
    private float rodReelVanillaMotionMultiplier = 0.1f;
    private float rodReelVanillaVerticalMultiplier = 0.08f;
    private float rodReelVanillaShooterYOffset = 1.0f;

    public KnockbackProfile(String name) {
        this.name = name;
    }

    /**
     * 深拷贝，用于创建 per-hit 副本
     */
    public KnockbackProfile copy() {
        return copy(name + "-copy");
    }

    /**
     * 深拷贝并指定新名称
     */
    public KnockbackProfile copy(String newName) {
        KnockbackProfile copy = new KnockbackProfile(newName);
        return copy.copyFrom(this);
    }

    /**
     * 从另一个 Profile 复制全部可变参数，保留当前 Profile 名称。
     */
    public KnockbackProfile copyFrom(KnockbackProfile source) {
        this.baseH = source.baseH;
        this.baseV = source.baseV;
        this.enchantBonusH = source.enchantBonusH;
        this.enchantBonusV = source.enchantBonusV;
        this.enchantLevel = source.enchantLevel;
        this.friction = source.friction;
        this.inheritHorizontal = source.inheritHorizontal;
        this.inheritVertical = source.inheritVertical;
        this.inheritRatioH = source.inheritRatioH;
        this.inheritRatioV = source.inheritRatioV;
        this.useRealVelocity = source.useRealVelocity;
        this.verticalLimit = source.verticalLimit;
        this.horizontalMin = source.horizontalMin;
        this.horizontalMax = source.horizontalMax;
        this.directionCorrection = source.directionCorrection;
        this.sprintMultiplierH = source.sprintMultiplierH;
        this.sprintMultiplierV = source.sprintMultiplierV;
        this.stopSprinting = source.stopSprinting;
        this.sprintSlowdownH = source.sprintSlowdownH;
        this.sprintBaseH = source.sprintBaseH;
        this.sprintBaseV = source.sprintBaseV;
        this.groundMultiplierH = source.groundMultiplierH;
        this.groundMultiplierV = source.groundMultiplierV;
        this.airMultiplierH = source.airMultiplierH;
        this.airMultiplierV = source.airMultiplierV;
        this.limitYDifference = source.limitYDifference;
        this.yDifferenceLimit = source.yDifferenceLimit;
        this.bowBaseH = source.bowBaseH;
        this.bowBaseV = source.bowBaseV;
        this.projectileBaseH = source.projectileBaseH;
        this.projectileBaseV = source.projectileBaseV;
        this.tridentBaseH = source.tridentBaseH;
        this.tridentBaseV = source.tridentBaseV;
        this.rodBaseH = source.rodBaseH;
        this.rodBaseV = source.rodBaseV;
        this.rodHitMotion = source.rodHitMotion;
        this.rodHitMotionDivisor = source.rodHitMotionDivisor;
        this.rodHitMotionVertical = source.rodHitMotionVertical;
        this.rodReelPullBack = source.rodReelPullBack;
        this.rodReelMotionEC = source.rodReelMotionEC;
        this.rodReelECMotionDivisor = source.rodReelECMotionDivisor;
        this.rodReelECMotionVertical = source.rodReelECMotionVertical;
        this.rodReelVanillaMotionMultiplier = source.rodReelVanillaMotionMultiplier;
        this.rodReelVanillaVerticalMultiplier = source.rodReelVanillaVerticalMultiplier;
        this.rodReelVanillaShooterYOffset = source.rodReelVanillaShooterYOffset;
        return this;
    }

    // === 有效击退值 ===

    public float getEffectiveBaseH() {
        return baseH + enchantLevel * enchantBonusH;
    }

    public float getEffectiveBaseV() {
        return baseV + enchantLevel * enchantBonusV;
    }

    public float getBaseH(KnockbackSourceType sourceType) {
        return resolveBase(getSourceBaseH(sourceType), baseH);
    }

    public float getBaseV(KnockbackSourceType sourceType) {
        return resolveBase(getSourceBaseV(sourceType), baseV);
    }

    private float getSourceBaseH(KnockbackSourceType sourceType) {
        if (sourceType == null) {
            return baseH;
        }
        return switch (sourceType) {
            case BOW -> bowBaseH;
            case PROJECTILE -> projectileBaseH;
            case TRIDENT -> tridentBaseH;
            case ROD -> rodBaseH;
            case GENERIC -> baseH;
        };
    }

    private float getSourceBaseV(KnockbackSourceType sourceType) {
        if (sourceType == null) {
            return baseV;
        }
        return switch (sourceType) {
            case BOW -> bowBaseV;
            case PROJECTILE -> projectileBaseV;
            case TRIDENT -> tridentBaseV;
            case ROD -> rodBaseV;
            case GENERIC -> baseV;
        };
    }

    private static float resolveBase(float sourceBase, float defaultBase) {
        return sourceBase >= 0 ? sourceBase : defaultBase;
    }

    // === getter + 链式 setter ===

    public String getName() {
        return name;
    }

    public float getBaseH() {
        return baseH;
    }

    public KnockbackProfile setBaseH(float baseH) {
        this.baseH = baseH;
        return this;
    }

    public float getBaseV() {
        return baseV;
    }

    public KnockbackProfile setBaseV(float baseV) {
        this.baseV = baseV;
        return this;
    }

    public float getEnchantBonusH() {
        return enchantBonusH;
    }

    public KnockbackProfile setEnchantBonusH(float enchantBonusH) {
        this.enchantBonusH = enchantBonusH;
        return this;
    }

    public float getEnchantBonusV() {
        return enchantBonusV;
    }

    public KnockbackProfile setEnchantBonusV(float enchantBonusV) {
        this.enchantBonusV = enchantBonusV;
        return this;
    }

    public int getEnchantLevel() {
        return enchantLevel;
    }

    public KnockbackProfile setEnchantLevel(int enchantLevel) {
        this.enchantLevel = enchantLevel;
        return this;
    }

    public float getFriction() {
        return friction;
    }

    public KnockbackProfile setFriction(float friction) {
        this.friction = friction;
        return this;
    }

    public boolean isInheritHorizontal() {
        return inheritHorizontal;
    }

    public KnockbackProfile setInheritHorizontal(boolean inheritHorizontal) {
        this.inheritHorizontal = inheritHorizontal;
        return this;
    }

    public boolean isInheritVertical() {
        return inheritVertical;
    }

    public KnockbackProfile setInheritVertical(boolean inheritVertical) {
        this.inheritVertical = inheritVertical;
        return this;
    }

    public float getInheritRatioH() {
        return inheritRatioH;
    }

    public KnockbackProfile setInheritRatioH(float inheritRatioH) {
        this.inheritRatioH = inheritRatioH;
        return this;
    }

    public float getInheritRatioV() {
        return inheritRatioV;
    }

    public KnockbackProfile setInheritRatioV(float inheritRatioV) {
        this.inheritRatioV = inheritRatioV;
        return this;
    }

    public boolean isUseRealVelocity() {
        return useRealVelocity;
    }

    public KnockbackProfile setUseRealVelocity(boolean useRealVelocity) {
        this.useRealVelocity = useRealVelocity;
        return this;
    }

    public float getVerticalLimit() {
        return verticalLimit;
    }

    public KnockbackProfile setVerticalLimit(float verticalLimit) {
        this.verticalLimit = verticalLimit;
        return this;
    }

    public float getHorizontalMin() {
        return horizontalMin;
    }

    public KnockbackProfile setHorizontalMin(float horizontalMin) {
        this.horizontalMin = horizontalMin;
        return this;
    }

    public float getHorizontalMax() {
        return horizontalMax;
    }

    public KnockbackProfile setHorizontalMax(float horizontalMax) {
        this.horizontalMax = horizontalMax;
        return this;
    }

    public boolean isDirectionCorrection() {
        return directionCorrection;
    }

    public KnockbackProfile setDirectionCorrection(boolean directionCorrection) {
        this.directionCorrection = directionCorrection;
        return this;
    }

    public float getSprintMultiplierH() {
        return sprintMultiplierH;
    }

    public KnockbackProfile setSprintMultiplierH(float sprintMultiplierH) {
        this.sprintMultiplierH = sprintMultiplierH;
        return this;
    }

    public float getSprintMultiplierV() {
        return sprintMultiplierV;
    }

    public KnockbackProfile setSprintMultiplierV(float sprintMultiplierV) {
        this.sprintMultiplierV = sprintMultiplierV;
        return this;
    }

    public boolean isStopSprinting() {
        return stopSprinting;
    }

    public KnockbackProfile setStopSprinting(boolean stopSprinting) {
        this.stopSprinting = stopSprinting;
        return this;
    }

    public float getSprintSlowdownH() {
        return sprintSlowdownH;
    }

    public KnockbackProfile setSprintSlowdownH(float sprintSlowdownH) {
        this.sprintSlowdownH = sprintSlowdownH;
        return this;
    }

    public float getSprintBaseH() {
        return sprintBaseH;
    }

    public KnockbackProfile setSprintBaseH(float sprintBaseH) {
        this.sprintBaseH = sprintBaseH;
        return this;
    }

    public float getSprintBaseV() {
        return sprintBaseV;
    }

    public KnockbackProfile setSprintBaseV(float sprintBaseV) {
        this.sprintBaseV = sprintBaseV;
        return this;
    }

    public float getGroundMultiplierH() {
        return groundMultiplierH;
    }

    public KnockbackProfile setGroundMultiplierH(float groundMultiplierH) {
        this.groundMultiplierH = groundMultiplierH;
        return this;
    }

    public float getGroundMultiplierV() {
        return groundMultiplierV;
    }

    public KnockbackProfile setGroundMultiplierV(float groundMultiplierV) {
        this.groundMultiplierV = groundMultiplierV;
        return this;
    }

    public float getAirMultiplierH() {
        return airMultiplierH;
    }

    public KnockbackProfile setAirMultiplierH(float airMultiplierH) {
        this.airMultiplierH = airMultiplierH;
        return this;
    }

    public float getAirMultiplierV() {
        return airMultiplierV;
    }

    public KnockbackProfile setAirMultiplierV(float airMultiplierV) {
        this.airMultiplierV = airMultiplierV;
        return this;
    }

    public boolean isLimitYDifference() {
        return limitYDifference;
    }

    public KnockbackProfile setLimitYDifference(boolean limitYDifference) {
        this.limitYDifference = limitYDifference;
        return this;
    }

    public float getYDifferenceLimit() {
        return yDifferenceLimit;
    }

    public KnockbackProfile setYDifferenceLimit(float yDifferenceLimit) {
        this.yDifferenceLimit = yDifferenceLimit;
        return this;
    }

    public float getBowBaseH() {
        return bowBaseH;
    }

    public KnockbackProfile setBowBaseH(float bowBaseH) {
        this.bowBaseH = bowBaseH;
        return this;
    }

    public float getBowBaseV() {
        return bowBaseV;
    }

    public KnockbackProfile setBowBaseV(float bowBaseV) {
        this.bowBaseV = bowBaseV;
        return this;
    }

    public float getProjectileBaseH() {
        return projectileBaseH;
    }

    public KnockbackProfile setProjectileBaseH(float projectileBaseH) {
        this.projectileBaseH = projectileBaseH;
        return this;
    }

    public float getProjectileBaseV() {
        return projectileBaseV;
    }

    public KnockbackProfile setProjectileBaseV(float projectileBaseV) {
        this.projectileBaseV = projectileBaseV;
        return this;
    }

    public float getTridentBaseH() {
        return tridentBaseH;
    }

    public KnockbackProfile setTridentBaseH(float tridentBaseH) {
        this.tridentBaseH = tridentBaseH;
        return this;
    }

    public float getTridentBaseV() {
        return tridentBaseV;
    }

    public KnockbackProfile setTridentBaseV(float tridentBaseV) {
        this.tridentBaseV = tridentBaseV;
        return this;
    }

    public float getRodBaseH() {
        return rodBaseH;
    }

    public KnockbackProfile setRodBaseH(float rodBaseH) {
        this.rodBaseH = rodBaseH;
        return this;
    }

    public float getRodBaseV() {
        return rodBaseV;
    }

    public KnockbackProfile setRodBaseV(float rodBaseV) {
        this.rodBaseV = rodBaseV;
        return this;
    }

    public boolean isRodHitMotion() {
        return rodHitMotion;
    }

    public KnockbackProfile setRodHitMotion(boolean rodHitMotion) {
        this.rodHitMotion = rodHitMotion;
        return this;
    }

    public float getRodHitMotionDivisor() {
        return rodHitMotionDivisor;
    }

    public KnockbackProfile setRodHitMotionDivisor(float rodHitMotionDivisor) {
        this.rodHitMotionDivisor = rodHitMotionDivisor;
        return this;
    }

    public float getRodHitMotionVertical() {
        return rodHitMotionVertical;
    }

    public KnockbackProfile setRodHitMotionVertical(float rodHitMotionVertical) {
        this.rodHitMotionVertical = rodHitMotionVertical;
        return this;
    }

    public boolean isRodReelPullBack() {
        return rodReelPullBack;
    }

    public KnockbackProfile setRodReelPullBack(boolean rodReelPullBack) {
        this.rodReelPullBack = rodReelPullBack;
        return this;
    }

    public boolean isRodReelMotionEC() {
        return rodReelMotionEC;
    }

    public KnockbackProfile setRodReelMotionEC(boolean rodReelMotionEC) {
        this.rodReelMotionEC = rodReelMotionEC;
        return this;
    }

    public float getRodReelECMotionDivisor() {
        return rodReelECMotionDivisor;
    }

    public KnockbackProfile setRodReelECMotionDivisor(float rodReelECMotionDivisor) {
        this.rodReelECMotionDivisor = rodReelECMotionDivisor;
        return this;
    }

    public float getRodReelECMotionVertical() {
        return rodReelECMotionVertical;
    }

    public KnockbackProfile setRodReelECMotionVertical(float rodReelECMotionVertical) {
        this.rodReelECMotionVertical = rodReelECMotionVertical;
        return this;
    }

    public float getRodReelVanillaMotionMultiplier() {
        return rodReelVanillaMotionMultiplier;
    }

    public KnockbackProfile setRodReelVanillaMotionMultiplier(float rodReelVanillaMotionMultiplier) {
        this.rodReelVanillaMotionMultiplier = rodReelVanillaMotionMultiplier;
        return this;
    }

    public float getRodReelVanillaVerticalMultiplier() {
        return rodReelVanillaVerticalMultiplier;
    }

    public KnockbackProfile setRodReelVanillaVerticalMultiplier(float rodReelVanillaVerticalMultiplier) {
        this.rodReelVanillaVerticalMultiplier = rodReelVanillaVerticalMultiplier;
        return this;
    }

    public float getRodReelVanillaShooterYOffset() {
        return rodReelVanillaShooterYOffset;
    }

    public KnockbackProfile setRodReelVanillaShooterYOffset(float rodReelVanillaShooterYOffset) {
        this.rodReelVanillaShooterYOffset = rodReelVanillaShooterYOffset;
        return this;
    }
}
