package cn.nukkit.network.input;

import cn.nukkit.math.Mth;

/** 只累计最终提交的服务端位移；不作为物理预测或客户端运动权威来源。 */
public final class MovementTickAccumulator {
    private double deltaX;
    private double deltaY;
    private double deltaZ;
    private double horizontalDistance;
    private double foodDistance;
    private double distanceExhaustion;
    private float jumpExhaustion;

    public void record(double dx, double dy, double dz, boolean foodEligible,
                       boolean sprinting, boolean inWater, boolean groundDeparture) {
        this.deltaX += dx;
        this.deltaY += dy;
        this.deltaZ += dz;
        double distance = Mth.length(dx, dz);
        this.horizontalDistance += distance;
        if (!foodEligible) {
            return;
        }
        this.foodDistance += distance;
        this.distanceExhaustion += inWater ? 0.015 * distance : sprinting ? 0.1 * distance : 0;
        // 离地由最终服务端碰撞/位移提交确认，不依赖网络帧恰好落在某个空中 tick。
        if (!inWater && groundDeparture) {
            this.jumpExhaustion += sprinting ? 0.2f : 0.05f;
        }
    }

    public double deltaX() {
        return this.deltaX;
    }

    public double deltaY() {
        return this.deltaY;
    }

    public double deltaZ() {
        return this.deltaZ;
    }

    public boolean movedHorizontally() {
        return this.horizontalDistance * this.horizontalDistance > Mth.EPSILON;
    }

    public float exhaustion() {
        return ((float) this.foodDistance >= 0.05f ? (float) this.distanceExhaustion : 0) + this.jumpExhaustion;
    }

    /** 传送不计作移动路程，也不能把旧位置段带入下一次速度或附魔统计。 */
    public void resetDisplacement() {
        this.deltaX = 0;
        this.deltaY = 0;
        this.deltaZ = 0;
        this.horizontalDistance = 0;
    }

    public void clearTick() {
        this.resetDisplacement();
        this.foodDistance = 0;
        this.distanceExhaustion = 0;
        this.jumpExhaustion = 0;
    }
}
