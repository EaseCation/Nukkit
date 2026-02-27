package cn.nukkit.knockback;

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

    // === 地面状态 ===
    private float groundMultiplierH = 1.0f;
    private float groundMultiplierV = 1.0f;

    // === 高度差限制 ===
    private boolean limitYDifference = false;
    private float yDifferenceLimit = 1.2f;

    // === 弓箭/钓竿独立参数（-1 表示使用 baseH/baseV） ===
    private float bowBaseH = -1;
    private float bowBaseV = -1;
    private float rodBaseH = -1;
    private float rodBaseV = -1;

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
        copy.baseH = this.baseH;
        copy.baseV = this.baseV;
        copy.enchantBonusH = this.enchantBonusH;
        copy.enchantBonusV = this.enchantBonusV;
        copy.enchantLevel = this.enchantLevel;
        copy.friction = this.friction;
        copy.inheritHorizontal = this.inheritHorizontal;
        copy.inheritVertical = this.inheritVertical;
        copy.inheritRatioH = this.inheritRatioH;
        copy.inheritRatioV = this.inheritRatioV;
        copy.useRealVelocity = this.useRealVelocity;
        copy.verticalLimit = this.verticalLimit;
        copy.horizontalMin = this.horizontalMin;
        copy.horizontalMax = this.horizontalMax;
        copy.directionCorrection = this.directionCorrection;
        copy.sprintMultiplierH = this.sprintMultiplierH;
        copy.sprintMultiplierV = this.sprintMultiplierV;
        copy.stopSprinting = this.stopSprinting;
        copy.sprintSlowdownH = this.sprintSlowdownH;
        copy.groundMultiplierH = this.groundMultiplierH;
        copy.groundMultiplierV = this.groundMultiplierV;
        copy.limitYDifference = this.limitYDifference;
        copy.yDifferenceLimit = this.yDifferenceLimit;
        copy.bowBaseH = this.bowBaseH;
        copy.bowBaseV = this.bowBaseV;
        copy.rodBaseH = this.rodBaseH;
        copy.rodBaseV = this.rodBaseV;
        return copy;
    }

    // === 有效击退值 ===

    public float getEffectiveBaseH() {
        return baseH + enchantLevel * enchantBonusH;
    }

    public float getEffectiveBaseV() {
        return baseV + enchantLevel * enchantBonusV;
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
}
