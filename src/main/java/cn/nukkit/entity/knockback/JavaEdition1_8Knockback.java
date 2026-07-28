package cn.nukkit.entity.knockback;

import cn.nukkit.math.Mth;
import cn.nukkit.math.Vector3;

/**
 * Java Edition 1.8 击退公式的纯数学实现。
 * <p>
 * 所有方法均返回新向量，不会修改传入的运动或位置向量。
 */
public final class JavaEdition1_8Knockback {

    private JavaEdition1_8Knockback() {
    }

    /**
     * 应用普通受击击退。方向参数表示受害者远离伤害来源的水平差值。
     */
    public static Vector3 applyRegular(Vector3 currentMotion, double awayX, double awayZ,
                                       KnockbackProfile profile) {
        double motionX = profile.isInheritHorizontal()
                ? currentMotion.x * profile.getFriction() * profile.getInheritRatioH() : 0.0D;
        double motionY = profile.isInheritVertical()
                ? currentMotion.y * profile.getFriction() * profile.getInheritRatioV() : 0.0D;
        double motionZ = profile.isInheritHorizontal()
                ? currentMotion.z * profile.getFriction() * profile.getInheritRatioH() : 0.0D;
        motionY += profile.getBaseV();
        float horizontalLength = (float) Mth.length(awayX, awayZ);

        if (horizontalLength > 0.0D) {
            motionX += awayX / horizontalLength * profile.getBaseH();
            motionZ += awayZ / horizontalLength * profile.getBaseH();
        }

        float verticalLimit = profile.getVerticalLimit() >= 0.0f
                ? profile.getVerticalLimit() : profile.getBaseV();
        return new Vector3(motionX, Math.min(motionY, verticalLimit), motionZ);
    }

    /**
     * 按攻击者朝向应用疾跑与击退附魔共用的近战追加击退。
     */
    public static Vector3 applyMeleeExtra(Vector3 currentMotion, float attackerYaw, int knockbackLevel,
                                          KnockbackProfile profile) {
        if (knockbackLevel <= 0) {
            return new Vector3(currentMotion);
        }

        float yawRadians = attackerYaw * Mth.DEG_TO_RAD;
        float horizontal = knockbackLevel * profile.getEnchantBonusH();
        return currentMotion.add(
                -Mth.sin(yawRadians) * horizontal,
                profile.getEnchantBonusV(),
                Mth.cos(yawRadians) * horizontal
        );
    }

    /**
     * 按箭矢的水平运动方向应用 Punch 附魔追加击退。
     */
    public static Vector3 applyArrowExtra(Vector3 currentMotion, double arrowMotionX, double arrowMotionZ,
                                          int knockbackLevel, KnockbackProfile profile) {
        if (knockbackLevel <= 0) {
            return new Vector3(currentMotion);
        }

        float horizontalLength = (float) Mth.length(arrowMotionX, arrowMotionZ);
        if (horizontalLength <= 0.0D) {
            return new Vector3(currentMotion);
        }

        float arrowHorizontal = profile.getBowBaseH() >= 0.0f
                ? profile.getBowBaseH() : profile.getBaseH();
        float arrowVertical = profile.getBowBaseV() >= 0.0f
                ? profile.getBowBaseV() : profile.getBaseV();
        double horizontal = knockbackLevel * arrowHorizontal;
        return currentMotion.add(
                arrowMotionX / horizontalLength * horizontal,
                arrowVertical,
                arrowMotionZ / horizontalLength * horizontal
        );
    }

    /**
     * 应用鱼钩收竿时朝向持有者的追加运动。
     */
    public static Vector3 applyFishingReel(Vector3 currentMotion, Vector3 ownerPosition, Vector3 hookPosition,
                                           KnockbackProfile profile) {
        double offsetX = ownerPosition.x - hookPosition.x;
        double offsetY = ownerPosition.y - hookPosition.y;
        double offsetZ = ownerPosition.z - hookPosition.z;
        float distance = (float) Mth.length(offsetX, offsetY, offsetZ);
        float reelHorizontal = profile.getRodBaseH() >= 0.0f
                ? profile.getRodBaseH() : profile.getBaseH();
        float reelVertical = profile.getRodBaseV() >= 0.0f
                ? profile.getRodBaseV() : profile.getBaseV();

        return currentMotion.add(
                offsetX * reelHorizontal,
                offsetY * reelVertical + (float) Math.sqrt(distance) * profile.getRodDistanceLift(),
                offsetZ * reelHorizontal
        );
    }

    public static Vector3 applyTargetStateMultiplier(Vector3 motion, boolean onGround,
                                                      KnockbackProfile profile) {
        float horizontal = onGround ? profile.getGroundMultiplierH() : profile.getAirMultiplierH();
        float vertical = onGround ? profile.getGroundMultiplierV() : profile.getAirMultiplierV();
        return new Vector3(motion.x * horizontal, motion.y * vertical, motion.z * horizontal);
    }

    /**
     * 判断本次随机值是否通过 Java 1.8 的整次抗击退判定。
     */
    public static boolean shouldApplyKnockback(double resistance, double randomValue) {
        return randomValue >= resistance;
    }

}
