package cn.nukkit.molang;

import cn.nukkit.molang.compiler.MolangCompiler;
import cn.nukkit.molang.compiler.MolangCompileException;
import cn.nukkit.molang.lexer.Lexer;
import cn.nukkit.molang.parser.Parser;
import cn.nukkit.molang.parser.ast.Stmt.Program;
import cn.nukkit.molang.runtime.MolangRuntime;
import cn.nukkit.molang.runtime.MolangBindingPlan;
import cn.nukkit.molang.runtime.MolangQueryDefinition;
import cn.nukkit.molang.runtime.MolangQuerySchema;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class MolangEngine {
    private final int cacheCapacity;
    private final Map<CacheKey, CompiledExpression> cache = new LinkedHashMap<>(16, 0.75f, true);
    private long cacheHits;
    private long compilations;

    public MolangEngine() {
        this(256);
    }

    public MolangEngine(int cacheCapacity) {
        if (cacheCapacity < 0) {
            throw new IllegalArgumentException("Molang cache capacity cannot be negative");
        }
        this.cacheCapacity = cacheCapacity;
    }

    public MolangExpression compile(String source) {
        return compile(source, MolangCompileOptions.DEFAULT);
    }

    public MolangExpression compile(String source, MolangCompileOptions options) {
        return compileValidated(source, options, _ -> {});
    }

    /**
     * 域约束始终检查原始 AST，命中缓存也不会跳过合法性验证。
     */
    public MolangExpression compileValidated(String source, MolangCompileOptions options, Consumer<Program> validator) {
        return compileInternal(source, options, validator, null);
    }

    private synchronized CompiledExpression compileInternal(String source, MolangCompileOptions options, Consumer<Program> validator, @Nullable Program parsed) {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(options, "options");
        CacheKey key = new CacheKey(source, options);
        CompiledExpression cached = cache.get(key);
        if (cached != null) {
            validator.accept(cached.program);
            cacheHits++;
            return cached;
        }
        try {
            Program program = parsed != null ? parsed : new Parser(new Lexer(source).tokenize(), options.version()).parse();
            validator.accept(program);
            MolangExpression executable = new MolangCompiler(program, options).compile();
            CompiledExpression compiled = new CompiledExpression(source, program, options, executable);
            compilations++;
            if (cacheCapacity != 0) {
                if (cache.size() == cacheCapacity) {
                    cache.remove(cache.keySet().iterator().next());
                }
                cache.put(key, compiled);
            }
            return compiled;
        } catch (IllegalArgumentException exception) {
            throw new MolangCompileException("Invalid Molang expression", exception);
        }
    }

    public float evaluate(String source, MolangRuntime runtime) {
        return compile(source).evaluate(runtime);
    }

    public MolangBatch bindBatch(MolangRuntime runtime, List<MolangExpression> expressions) {
        return new MolangBatch(runtime, expressions);
    }

    public synchronized CacheStats cacheStats() {
        return new CacheStats(cacheHits, compilations, cache.size());
    }

    public synchronized void clearCache() {
        cache.clear();
    }

    public record CacheStats(long hits, long compilations, int size) {
    }

    private record CacheKey(String source, MolangCompileOptions options) {
    }

    private final class CompiledExpression implements MolangExpression {
        private final String source;
        private final Program program;
        private final MolangCompileOptions options;
        private final MolangExpression executable;
        private final boolean requiresRuntimeValidation;

        private CompiledExpression(String source, Program program, MolangCompileOptions options, MolangExpression executable) {
            this.source = source;
            this.program = program;
            this.options = options;
            this.executable = executable;
            this.requiresRuntimeValidation = options.stableVariableReads() || !options.querySchema().definitions().isEmpty();
        }

        @Override
        public float evaluate(MolangRuntime runtime) {
            if (requiresRuntimeValidation) {
                validateRuntime(runtime);
            }
            return executable.evaluate(runtime);
        }

        @Override
        @Nullable
        public Object evaluateValue(MolangRuntime runtime) {
            if (requiresRuntimeValidation) {
                validateRuntime(runtime);
            }
            return executable.evaluateValue(runtime);
        }

        @Override
        public MolangBindingPlan bindingPlan() {
            return executable.bindingPlan();
        }

        @Override
        public MolangBoundExpression bind(MolangRuntime runtime) {
            Map<String, MolangQueryDefinition> definitions = new HashMap<>();
            MolangBindingPlan plan = bindingPlan();
            MolangQuerySchema available = runtime.querySchema();
            for (int i = 0; i < plan.queryCount(); i++) {
                if (plan.query(i).foreignContext()) {
                    continue;
                }
                String name = plan.query(i).name();
                MolangQueryDefinition definition = available.get(name);
                if (definition != null) {
                    definitions.put(name, definition);
                }
            }
            MolangQuerySchema schema = definitions.isEmpty() ? MolangQuerySchema.EMPTY : new MolangQuerySchema(definitions);
            MolangCompileOptions specialized = new MolangCompileOptions(options.version(), schema, runtime.supportsStableVariableReads());
            CompiledExpression expression = compileInternal(source, specialized, _ -> {}, program);
            expression.validateRuntime(runtime);
            return new MolangBoundExpression(expression.executable, runtime.bind(expression.bindingPlan()));
        }

        private void validateRuntime(MolangRuntime runtime) {
            if (options.stableVariableReads() && !runtime.supportsStableVariableReads()) {
                throw new IllegalArgumentException("Runtime does not support the compiled variable stability contract");
            }
            if (!options.querySchema().definitions().isEmpty()) {
                MolangQuerySchema actual = runtime.querySchema();
                for (MolangQueryDefinition expected : options.querySchema().definitions().values()) {
                    if (!expected.equals(actual.get(expected.name()))) {
                        throw new IllegalArgumentException("Runtime query contract differs from compiled schema: " + expected.name());
                    }
                }
            }
        }
    }
}
