package cn.nukkit.molang.compiler;

import cn.nukkit.molang.runtime.MolangMath;
import cn.nukkit.utils.Utils;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 常量折叠和字节码分派共用的数学函数表；调用目标直接引用 Java 方法。
 */
final class MolangFunctions {
    private static final Map<String, MathFunction> MATH = Utils.make(() -> {
        Map<String, MathFunction> functions = new HashMap<>();
        functions.put("abs", new MathFunction(1, true, (a, _, _) -> MolangMath.abs(a)));
        functions.put("acos", new MathFunction(1, true, (a, _, _) -> MolangMath.acos(a)));
        functions.put("asin", new MathFunction(1, true, (a, _, _) -> MolangMath.asin(a)));
        functions.put("atan", new MathFunction(1, true, (a, _, _) -> MolangMath.atan(a)));
        functions.put("atan2", new MathFunction(2, true, (a, b, _) -> MolangMath.atan2(a, b)));
        functions.put("ceil", new MathFunction(1, true, (a, _, _) -> MolangMath.ceil(a)));
        functions.put("clamp", new MathFunction(3, true, MolangMath::clamp));
        functions.put("cos", new MathFunction(1, true, (a, _, _) -> MolangMath.cos(a)));
        functions.put("copy_sign", new MathFunction(2, true, (a, b, _) -> MolangMath.copy_sign(a, b)));
        functions.put("die_roll", new MathFunction(3, false, MolangMath::die_roll));
        functions.put("die_roll_integer", new MathFunction(3, false, MolangMath::die_roll_integer));
        functions.put("exp", new MathFunction(1, true, (a, _, _) -> MolangMath.exp(a)));
        functions.put("floor", new MathFunction(1, true, (a, _, _) -> MolangMath.floor(a)));
        functions.put("hermite_blend", new MathFunction(1, true, (a, _, _) -> MolangMath.hermite_blend(a)));
        functions.put("inverse_lerp", new MathFunction(3, true, MolangMath::inverse_lerp));
        functions.put("lerp", new MathFunction(3, true, MolangMath::lerp));
        functions.put("lerprotate", new MathFunction(3, true, MolangMath::lerprotate));
        functions.put("ln", new MathFunction(1, true, (a, _, _) -> MolangMath.ln(a)));
        functions.put("max", new MathFunction(2, true, (a, b, _) -> MolangMath.max(a, b)));
        functions.put("min", new MathFunction(2, true, (a, b, _) -> MolangMath.min(a, b)));
        functions.put("min_angle", new MathFunction(1, true, (a, _, _) -> MolangMath.min_angle(a)));
        functions.put("mod", new MathFunction(2, true, (a, b, _) -> MolangMath.mod(a, b)));
        functions.put("pi", new MathFunction(0, true, (_, _, _) -> MolangMath.pi()));
        functions.put("pow", new MathFunction(2, true, (a, b, _) -> MolangMath.pow(a, b)));
        functions.put("random", new MathFunction(2, false, (a, b, _) -> MolangMath.random(a, b)));
        functions.put("random_integer", new MathFunction(2, false, (a, b, _) -> MolangMath.random_integer(a, b)));
        functions.put("round", new MathFunction(1, true, (a, _, _) -> MolangMath.round(a)));
        functions.put("sign", new MathFunction(1, true, (a, _, _) -> MolangMath.sign(a)));
        functions.put("sin", new MathFunction(1, true, (a, _, _) -> MolangMath.sin(a)));
        functions.put("sqrt", new MathFunction(1, true, (a, _, _) -> MolangMath.sqrt(a)));
        functions.put("trunc", new MathFunction(1, true, (a, _, _) -> MolangMath.trunc(a)));
        functions.put("ease_in_sine", new MathFunction(3, true, MolangMath::ease_in_sine));
        functions.put("ease_out_sine", new MathFunction(3, true, MolangMath::ease_out_sine));
        functions.put("ease_in_out_sine", new MathFunction(3, true, MolangMath::ease_in_out_sine));
        functions.put("ease_in_quad", new MathFunction(3, true, MolangMath::ease_in_quad));
        functions.put("ease_out_quad", new MathFunction(3, true, MolangMath::ease_out_quad));
        functions.put("ease_in_out_quad", new MathFunction(3, true, MolangMath::ease_in_out_quad));
        functions.put("ease_in_cubic", new MathFunction(3, true, MolangMath::ease_in_cubic));
        functions.put("ease_out_cubic", new MathFunction(3, true, MolangMath::ease_out_cubic));
        functions.put("ease_in_out_cubic", new MathFunction(3, true, MolangMath::ease_in_out_cubic));
        functions.put("ease_in_quart", new MathFunction(3, true, MolangMath::ease_in_quart));
        functions.put("ease_out_quart", new MathFunction(3, true, MolangMath::ease_out_quart));
        functions.put("ease_in_out_quart", new MathFunction(3, true, MolangMath::ease_in_out_quart));
        functions.put("ease_in_quint", new MathFunction(3, true, MolangMath::ease_in_quint));
        functions.put("ease_out_quint", new MathFunction(3, true, MolangMath::ease_out_quint));
        functions.put("ease_in_out_quint", new MathFunction(3, true, MolangMath::ease_in_out_quint));
        functions.put("ease_in_expo", new MathFunction(3, true, MolangMath::ease_in_expo));
        functions.put("ease_out_expo", new MathFunction(3, true, MolangMath::ease_out_expo));
        functions.put("ease_in_out_expo", new MathFunction(3, true, MolangMath::ease_in_out_expo));
        functions.put("ease_in_circ", new MathFunction(3, true, MolangMath::ease_in_circ));
        functions.put("ease_out_circ", new MathFunction(3, true, MolangMath::ease_out_circ));
        functions.put("ease_in_out_circ", new MathFunction(3, true, MolangMath::ease_in_out_circ));
        functions.put("ease_in_back", new MathFunction(3, true, MolangMath::ease_in_back));
        functions.put("ease_out_back", new MathFunction(3, true, MolangMath::ease_out_back));
        functions.put("ease_in_out_back", new MathFunction(3, true, MolangMath::ease_in_out_back));
        functions.put("ease_in_bounce", new MathFunction(3, true, MolangMath::ease_in_bounce));
        functions.put("ease_out_bounce", new MathFunction(3, true, MolangMath::ease_out_bounce));
        functions.put("ease_in_out_bounce", new MathFunction(3, true, MolangMath::ease_in_out_bounce));
        functions.put("ease_in_elastic", new MathFunction(3, true, MolangMath::ease_in_elastic));
        functions.put("ease_out_elastic", new MathFunction(3, true, MolangMath::ease_out_elastic));
        functions.put("ease_in_out_elastic", new MathFunction(3, true, MolangMath::ease_in_out_elastic));
        // netease only:
        functions.put("and", new MathFunction(2, true, (a, b, _) -> MolangMath.and(a, b)));
        functions.put("or", new MathFunction(2, true, (a, b, _) -> MolangMath.or(a, b)));
        return Map.copyOf(functions);
    });

    static final Set<String> PURE_QUERIES = Set.of("all", "any", "approx_eq", "in_range", "count");
    static final Set<String> EFFECTFUL_QUERIES = Set.of("log", "debug_output");

    static boolean builtinQuery(String name) {
        return PURE_QUERIES.contains(name) || effectfulQuery(name);
    }

    static boolean effectfulQuery(String name) {
        return EFFECTFUL_QUERIES.contains(name);
    }

    @Nullable
    static MathFunction math(String name) {
        return MATH.get(name);
    }

    @SuppressWarnings("unused")
    static float smallIntegerPower(float base, int exponent) {
        double square = (double) base * base;
        double power = exponent == 3 ? square * base : square * square;
        // float 的平方在 double 中精确。覆盖最后一次乘法及 Math.pow 的舍入误差后，
        // 两端落入同一 float 才返回快速结果；分界附近和非有限输入仍使用原函数。
        double margin = Math.abs(power) * 0x1p-50;
        float lower = (float) (power - margin);
        if (lower == (float) (power + margin)) {
            return lower;
        }
        return MolangMath.pow(base, exponent);
    }

    private MolangFunctions() {
    }

    @FunctionalInterface
    interface FloatFunction {
        float apply(float a, float b, float c);
    }

    record MathFunction(int arity, boolean pure, FloatFunction function) {
        float evaluate(float[] arguments) {
            return function.apply(arguments.length > 0 ? arguments[0] : 0, arguments.length > 1 ? arguments[1] : 0, arguments.length > 2 ? arguments[2] : 0);
        }
    }
}
