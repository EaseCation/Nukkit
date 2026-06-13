package cn.nukkit.level;

/**
 * Level 级优化配置，默认关闭所有可能影响行为的优化。
 */
public class LevelOptimizationSettings {

    private final LiquidFlowSettings liquidFlow = new LiquidFlowSettings();

    public LiquidFlowSettings getLiquidFlow() {
        return liquidFlow;
    }

    public static LevelOptimizationSettings defaults() {
        return new LevelOptimizationSettings();
    }

    public LevelOptimizationSettings copy() {
        LevelOptimizationSettings settings = new LevelOptimizationSettings();
        settings.getLiquidFlow().copyFrom(this.liquidFlow);
        return settings;
    }

    public static class LiquidFlowSettings {

        private boolean equivalentOptimizationEnabled;
        private boolean visibilitySchedulingEnabled;
        private boolean normalUpdateDeduplicationEnabled;
        private boolean liquidUpdateAroundSuppressionEnabled;
        private int maxLiquidNormalUpdatesPerTick = -1;
        private int maxLiquidScheduledUpdatesPerTick = -1;
        private int visibilityChunkRadius = 1;
        private int visibilitySubChunkRadius = -1;

        public boolean isEquivalentOptimizationEnabled() {
            return equivalentOptimizationEnabled;
        }

        public LiquidFlowSettings setEquivalentOptimizationEnabled(boolean equivalentOptimizationEnabled) {
            this.equivalentOptimizationEnabled = equivalentOptimizationEnabled;
            return this;
        }

        public boolean isVisibilitySchedulingEnabled() {
            return visibilitySchedulingEnabled;
        }

        public LiquidFlowSettings setVisibilitySchedulingEnabled(boolean visibilitySchedulingEnabled) {
            this.visibilitySchedulingEnabled = visibilitySchedulingEnabled;
            return this;
        }

        public boolean isNormalUpdateDeduplicationEnabled() {
            return normalUpdateDeduplicationEnabled;
        }

        public LiquidFlowSettings setNormalUpdateDeduplicationEnabled(boolean normalUpdateDeduplicationEnabled) {
            this.normalUpdateDeduplicationEnabled = normalUpdateDeduplicationEnabled;
            return this;
        }

        public boolean isLiquidUpdateAroundSuppressionEnabled() {
            return liquidUpdateAroundSuppressionEnabled;
        }

        public LiquidFlowSettings setLiquidUpdateAroundSuppressionEnabled(boolean liquidUpdateAroundSuppressionEnabled) {
            this.liquidUpdateAroundSuppressionEnabled = liquidUpdateAroundSuppressionEnabled;
            return this;
        }

        public int getMaxLiquidNormalUpdatesPerTick() {
            return maxLiquidNormalUpdatesPerTick;
        }

        public LiquidFlowSettings setMaxLiquidNormalUpdatesPerTick(int maxLiquidNormalUpdatesPerTick) {
            this.maxLiquidNormalUpdatesPerTick = maxLiquidNormalUpdatesPerTick;
            return this;
        }

        public int getMaxLiquidScheduledUpdatesPerTick() {
            return maxLiquidScheduledUpdatesPerTick;
        }

        public LiquidFlowSettings setMaxLiquidScheduledUpdatesPerTick(int maxLiquidScheduledUpdatesPerTick) {
            this.maxLiquidScheduledUpdatesPerTick = maxLiquidScheduledUpdatesPerTick;
            return this;
        }

        public int getVisibilityChunkRadius() {
            return visibilityChunkRadius;
        }

        public LiquidFlowSettings setVisibilityChunkRadius(int visibilityChunkRadius) {
            this.visibilityChunkRadius = Math.max(0, visibilityChunkRadius);
            return this;
        }

        public int getVisibilitySubChunkRadius() {
            return visibilitySubChunkRadius;
        }

        public LiquidFlowSettings setVisibilitySubChunkRadius(int visibilitySubChunkRadius) {
            this.visibilitySubChunkRadius = visibilitySubChunkRadius;
            return this;
        }

        public LiquidFlowSettings copyFrom(LiquidFlowSettings settings) {
            this.equivalentOptimizationEnabled = settings.equivalentOptimizationEnabled;
            this.visibilitySchedulingEnabled = settings.visibilitySchedulingEnabled;
            this.normalUpdateDeduplicationEnabled = settings.normalUpdateDeduplicationEnabled;
            this.liquidUpdateAroundSuppressionEnabled = settings.liquidUpdateAroundSuppressionEnabled;
            this.maxLiquidNormalUpdatesPerTick = settings.maxLiquidNormalUpdatesPerTick;
            this.maxLiquidScheduledUpdatesPerTick = settings.maxLiquidScheduledUpdatesPerTick;
            this.visibilityChunkRadius = settings.visibilityChunkRadius;
            this.visibilitySubChunkRadius = settings.visibilitySubChunkRadius;
            return this;
        }
    }
}
