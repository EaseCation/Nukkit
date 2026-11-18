package cn.nukkit.molang.runtime;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.BooleanSupplier;

/**
 * 将实体私有变量与公开发布值分开。宿主在明确的帧边界调用 publish()。
 */
public final class MolangActorContext {
    private final MolangRuntime local;
    private final List<String> publicVariables;
    private final BooleanSupplier alive;
    private final MolangEntityReference reference;
    private volatile MolangRuntime published;
    private volatile boolean valid = true;

    public MolangActorContext(MolangRuntime local, Collection<String> publicVariables) {
        this(local, publicVariables, () -> true);
    }

    public MolangActorContext(MolangRuntime local, Collection<String> publicVariables, BooleanSupplier alive) {
        this.local = Objects.requireNonNull(local, "local");
        this.publicVariables = publicVariables.stream().map(MolangActorContext::variableName).distinct().toList();
        this.alive = Objects.requireNonNull(alive, "alive");
        published = new PublishedRuntime(new MolangStruct());
        WeakReference<MolangActorContext> owner = new WeakReference<>(this);
        reference = () -> {
            MolangActorContext context = owner.get();
            return context != null && context.valid && context.alive.getAsBoolean() ? context.published : null;
        };
    }

    public MolangRuntime local() {
        return local;
    }

    public MolangEntityReference publicReference() {
        return reference;
    }

    public void publish() {
        if (!valid || !alive.getAsBoolean() || publicVariables.isEmpty()) {
            return;
        }
        MolangStruct snapshot = new MolangStruct();
        for (String name : publicVariables) {
            Object value = local.getVariableValue(name);
            if (value != null) {
                snapshot.set(name, value);
            }
        }
        published = new PublishedRuntime(snapshot);
    }

    public void invalidate() {
        valid = false;
    }

    private static String variableName(String source) {
        String name = Objects.requireNonNull(source, "public variable").toLowerCase(Locale.ROOT);
        if (name.startsWith("variable.")) {
            name = name.substring("variable.".length());
        } else if (name.startsWith("v.")) {
            name = name.substring("v.".length());
        }
        return new MolangPath(name).name();
    }

    private final class PublishedRuntime implements MolangRuntime {
        private final MolangStruct snapshot;

        private PublishedRuntime(MolangStruct snapshot) {
            this.snapshot = snapshot;
        }

        @Override
        @Nullable
        public Object getHost() {
            return local.getHost();
        }

        @Override
        public MolangQuerySchema querySchema() {
            return local.querySchema();
        }

        @Override
        public void beginEvaluation() {
            local.beginEvaluation();
        }

        @Override
        public void endEvaluation() {
            local.endEvaluation();
        }

        @Override
        public void beginFrame(long frameId) {
            local.beginFrame(frameId);
        }

        @Override
        public void endFrame() {
            local.endFrame();
        }

        @Override
        public void invalidateQueries() {
            local.invalidateQueries();
        }

        @Override
        public float getVariable(String name) {
            return snapshot.getFloat(name);
        }

        @Override
        @Nullable
        public Object getVariableValue(String name) {
            // 公开复杂值不能经宿主 query 的可变参数别名反向修改发布快照。
            return MolangValues.copy(snapshot.get(name));
        }

        @Override
        public float getVariableNumber(String name) {
            return snapshot.getFloat(name);
        }

        @Override
        public float getVariableNumber(MolangPath path) {
            return snapshot.getFloat(path);
        }

        @Override
        public long getVariableOperand(MolangPath path) {
            return snapshot.getOperand(path);
        }

        @Override
        @Nullable
        public Object getVariableValue(MolangPath path) {
            return MolangValues.copy(snapshot.get(path));
        }

        @Override
        public void setVariable(String name, float value) {
            throw readOnly();
        }

        @Override
        public void setVariableValue(String name, @Nullable Object value) {
            throw readOnly();
        }

        @Override
        public float getTemp(String name) {
            return local.getTemp(name);
        }

        @Override
        public void setTemp(String name, float value) {
            local.setTemp(name, value);
        }

        @Override
        @Nullable
        public Object getTempValue(String name) {
            return local.getTempValue(name);
        }

        @Override
        public void setTempValue(String name, @Nullable Object value) {
            local.setTempValue(name, value);
        }

        @Override
        public float getContext(String name) {
            return local.getContext(name);
        }

        @Override
        @Nullable
        public Object getContextValue(String name) {
            return local.getContextValue(name);
        }

        @Override
        public QueryHandler resolveQuery(String name) {
            return local.resolveQuery(name);
        }

        @Override
        @Nullable
        public Object queryValue(String name, Object... args) {
            return local.queryValue(name, args);
        }

        @Override
        public float getThis() {
            return local.getThis();
        }

        @Override
        public float getVariableElement(String name, int index) {
            return MolangValues.toFloat(getVariableElementValue(name, index));
        }

        @Override
        @Nullable
        public Object getVariableElementValue(String name, int index) {
            return MolangValues.copy(MolangValues.getElement(snapshot.get(name), index));
        }

        @Override
        public void setVariableElement(String name, int index, float value) {
            throw readOnly();
        }

        @Override
        public void setVariableElementValue(String name, int index, @Nullable Object value) {
            throw readOnly();
        }

        @Override
        public float log(float value) {
            return local.log(value);
        }

        @Override
        public float debugOutput(float value) {
            return local.debugOutput(value);
        }

        @Override
        public boolean supportsStableVariableReads() {
            return true;
        }

        private UnsupportedOperationException readOnly() {
            return new UnsupportedOperationException("Published Molang variables are read-only");
        }
    }
}
