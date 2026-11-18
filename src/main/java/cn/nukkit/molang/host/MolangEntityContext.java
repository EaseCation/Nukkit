package cn.nukkit.molang.host;

import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityHumanType;
import cn.nukkit.entity.property.EntityPropertiesInstance;
import cn.nukkit.entity.property.EntityProperty;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemFullNames;
import cn.nukkit.math.Mth;
import cn.nukkit.molang.runtime.MolangActorContext;
import cn.nukkit.molang.runtime.MolangEntityReference;
import cn.nukkit.molang.runtime.MolangQueryDefinition;
import cn.nukkit.molang.runtime.MolangQueryDefinition.Effect;
import cn.nukkit.molang.runtime.MolangQueryDefinition.Stability;
import cn.nukkit.molang.runtime.MolangQueryDefinition.ValueType;
import cn.nukkit.molang.runtime.MolangRuntime;
import cn.nukkit.molang.runtime.MolangRuntimeBuilder;
import cn.nukkit.molang.runtime.MolangRuntimeTemplate;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 实体的显式 Molang 宿主。调用方提供动画时间与修正后的移动量。
 * 该适配层不会自动挂接服务器 tick；宿主负责采样帧、选择附近实体和发布公开变量。
 */
public final class MolangEntityContext {
    private static final MolangRuntimeTemplate TEMPLATE = createTemplate();

    private final Entity entity;
    private final MolangRuntime runtime;
    private final MolangActorContext actor;
    private List<MolangEntityContext> nearby = List.of();
    private double previousX;
    private double previousZ;
    private float animationTime;
    private float modifiedMoveSpeed;
    private float modifiedDistanceMoved;
    private float groundSpeed;

    public MolangEntityContext(Entity entity, Collection<String> publicVariables) {
        this.entity = Objects.requireNonNull(entity, "entity");
        previousX = entity.x;
        previousZ = entity.z;
        runtime = TEMPLATE.newRuntime(this);
        actor = new MolangActorContext(runtime, publicVariables, () -> !entity.isClosed());
    }

    private static MolangRuntimeTemplate createTemplate() {
        MolangRuntimeBuilder builder = new MolangRuntimeBuilder().strictQueries(true);
        builder.queryNumber(number("health", Stability.EVALUATION), (context, _) -> host(context).entity.getHealth());
        builder.queryNumber(number("max_health", Stability.EVALUATION), (context, _) -> (float) host(context).entity.getMaxHealth());
        builder.queryNumber(number("is_alive", Stability.EVALUATION), (context, _) -> host(context).entity.isAlive() ? 1 : 0);
        builder.queryNumber(number("is_on_ground", Stability.EVALUATION), (context, _) -> host(context).entity.isOnGround() ? 1 : 0);
        builder.queryNumber(number("life_time", Stability.EVALUATION), (context, _) -> host(context).entity.getAge() / (float) Server.TPS);
        builder.queryNumber(number("ground_speed", Stability.FRAME), (context, _) -> host(context).groundSpeed);
        builder.queryNumber(number("anim_time", Stability.FRAME), (context, _) -> host(context).animationTime);
        builder.queryNumber(number("modified_move_speed", Stability.FRAME), (context, _) -> host(context).modifiedMoveSpeed);
        builder.queryNumber(number("modified_distance_moved", Stability.FRAME), (context, _) -> host(context).modifiedDistanceMoved);
        builder.query(definition("property", ValueType.DYNAMIC, 1, 1).withArguments(ValueType.STRING), (context, args) -> host(context).property(string(args, 0)));
        builder.queryNumber(definition("has_property", ValueType.NUMBER, 1, 1).withArguments(ValueType.STRING).withNullable(false), (context, args) -> {
            EntityPropertiesInstance properties = host(context).entity.getProperties();
            return properties != null && properties.getProperty(string(args, 0)) != null ? 1 : 0;
        });
        builder.queryNumber(definition("is_item_name_any", ValueType.NUMBER, 2, Integer.MAX_VALUE)
                        .withArguments(ValueType.STRING, ValueType.DYNAMIC, ValueType.STRING).withNullable(false),
                (context, args) -> host(context).isItemNameAny(args));
        builder.query(definition("get_nearby_entities", ValueType.ARRAY, 1, 2)
                        .withArguments(ValueType.NUMBER, ValueType.STRING).withNullable(false),
                (context, args) -> host(context).nearbyEntities(args));
        return builder.template();
    }

    public Entity entity() {
        return entity;
    }

    public MolangRuntime runtime() {
        return runtime;
    }

    public MolangActorContext actor() {
        return actor;
    }

    public MolangEntityReference publicReference() {
        return actor.publicReference();
    }

    /**
     * 地面速度由实体已提交的水平位移计算；修正移动量由实际动画驱动器提供。
     */
    public void captureFrame(float animationTime, float modifiedMoveSpeed, float modifiedDistanceMoved, float elapsedSeconds) {
        if (!Float.isFinite(animationTime) || !Float.isFinite(modifiedMoveSpeed) || !Float.isFinite(modifiedDistanceMoved) || !Float.isFinite(elapsedSeconds) || elapsedSeconds <= 0) {
            throw new IllegalArgumentException("Invalid Molang entity animation frame");
        }
        groundSpeed = (float) (Mth.length(entity.x - previousX, entity.z - previousZ) / elapsedSeconds);
        previousX = entity.x;
        previousZ = entity.z;
        this.animationTime = animationTime;
        this.modifiedMoveSpeed = modifiedMoveSpeed;
        this.modifiedDistanceMoved = modifiedDistanceMoved;
        runtime.invalidateQueries();
    }

    public void nearby(Collection<MolangEntityContext> entities) {
        nearby = List.copyOf(entities);
        runtime.invalidateQueries();
    }

    @Nullable
    private Object property(String name) {
        EntityPropertiesInstance properties = entity.getProperties();
        EntityProperty property = properties != null ? properties.getProperty(name) : null;
        if (property == null) {
            return null;
        }
        return switch (property.getType()) {
            case INT -> properties.getInt(property);
            case FLOAT -> properties.getFloat(property);
            case BOOL -> properties.getBoolean(property) ? 1 : 0;
            case ENUM -> properties.getEnum(property);
        };
    }

    private float isItemNameAny(Object[] args) {
        String slot = string(args, 0);
        int firstName = 1;
        if (args.length > 1 && args[1] instanceof Number index) {
            if (index.floatValue() != 0) {
                return 0;
            }
            firstName = 2;
        }
        if (!(entity instanceof EntityHumanType human)) {
            return 0;
        }
        Item item;
        switch (slot) {
            case "slot.weapon.mainhand" -> {
                if (human.getInventory() == null) {
                    return 0;
                }
                // 读取真实主槽，避免交互过程的副手映射和只读查询中的物品克隆。
                item = human.getInventory().getContentsUnsafe().get(human.getInventory().getHeldItemIndex());
            }
            case "slot.weapon.offhand" -> {
                if (human.getOffhandInventory() == null) {
                    return 0;
                }
                item = human.getOffhandInventory().getContentsUnsafe().get(0);
            }
            default -> {
                return 0;
            }
        }
        String identifier = item == null || item.isNull() ? ItemFullNames.AIR : item.getFullName();
        for (int i = firstName; i < args.length; i++) {
            if (identifier.equals(string(args, i))) {
                return 1;
            }
        }
        return 0;
    }

    private Object[] nearbyEntities(Object[] args) {
        if (args.length < 1 || args.length > 2 || !(args[0] instanceof Number number)) {
            throw new IllegalArgumentException("Nearby entity radius must be numeric");
        }
        float radius = number.floatValue();
        if (!Float.isFinite(radius) || radius < 0) {
            throw new IllegalArgumentException("Nearby entity radius must be finite and non-negative");
        }
        String identifier = args.length > 1 ? string(args, 1) : null;
        double radiusSquared = (double) radius * radius;
        List<MolangEntityReference> result = new ArrayList<>();
        for (MolangEntityContext candidate : nearby) {
            Entity other = candidate.entity;
            if (other == entity || other.isClosed() || other.level != entity.level || other.distanceSquared(entity) > radiusSquared
                    || identifier != null && !identifier.equals(other.getIdentifier())) {
                continue;
            }
            result.add(candidate.publicReference());
        }
        return result.toArray();
    }

    private static MolangEntityContext host(MolangRuntime runtime) {
        if (runtime.getHost() instanceof MolangEntityContext context) {
            return context;
        }
        throw new IllegalArgumentException("Expected a Molang entity host");
    }

    private static String string(Object[] args, int index) {
        if (index >= args.length || !(args[index] instanceof String value)) {
            throw new IllegalArgumentException("Molang entity query requires a string argument");
        }
        return value;
    }

    private static MolangQueryDefinition number(String name, Stability stability) {
        return MolangQueryDefinition.number(name, 0, Effect.READ_ONLY, stability).withNullable(false);
    }

    private static MolangQueryDefinition definition(String name, ValueType type, int minimum, int maximum) {
        return new MolangQueryDefinition(name, type, minimum, maximum, Effect.READ_ONLY, Stability.EVALUATION, Set.of("host.*"), Set.of());
    }
}
