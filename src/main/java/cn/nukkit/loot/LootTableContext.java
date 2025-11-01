package cn.nukkit.loot;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityDamageByChildEntityEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.utils.Utils;
import lombok.Builder;
import lombok.NonNull;
import tools.jackson.databind.EnumNamingStrategies;
import tools.jackson.databind.annotation.EnumNaming;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

@Builder(builderMethodName = "internalBuilder")
public class LootTableContext {
    private final float luck;
    @NonNull
    private final Level level;
    @Nullable
    private final Entity thisEntity;
    @Nullable
    private final Player killerPlayer;
    @Nullable
    private final Entity killedEntity;
    @Nullable
    private final EntityDamageEvent deathSource;
    private final float explosionRadius;
    @Nullable
    private final Item tool;
    private final Set<LootTable> visitedTables = new LinkedHashSet<>();

    private LootTableContext(float luck, Level level, Entity thisEntity, Player killerPlayer, Entity killedEntity, EntityDamageEvent deathSource, float explosionRadius, Item tool) {
        this.luck = luck;
        this.level = level;
        this.thisEntity = thisEntity;
        this.killerPlayer = killerPlayer;
        this.killedEntity = killedEntity;
        this.deathSource = deathSource;
        this.explosionRadius = explosionRadius;
        this.tool = tool;
    }

    public float getLuck() {
        return luck;
    }

    public Level getLevel() {
        return level;
    }

    @Nullable
    public Entity getThisEntity() {
        return thisEntity;
    }

    @Nullable
    public Player getKillerPlayer() {
        return killerPlayer;
    }

    @Nullable
    public Entity getKilledEntity() {
        return killedEntity;
    }

    @Nullable
    public Entity getKillerPet() {
/*
        if (!(deathSource instanceof EntityDamageByEntityEvent event)) {
            return null;
        }
        Entity entity = event.getDamager();
        if (entity.isTame() && entity.getPlayerOwner() != null) {
            return entity;
        }
*/
        return null;
    }

    @Nullable
    public Entity getKillerEntity() {
        if (!(deathSource instanceof EntityDamageByEntityEvent event)) {
            return null;
        }
        return event.getDamager();
    }

    @Nullable
    public Entity getDirectKillerEntity() {
        if (!(deathSource instanceof EntityDamageByEntityEvent event)) {
            return null;
        }
        if (deathSource instanceof EntityDamageByChildEntityEvent ev) {
            return ev.getChild();
        }
        return event.getDamager();
    }

    @Nullable
    public Entity getEntity(EntityTarget target) {
        return target.entityGetter.apply(this);
    }

    @Nullable
    public EntityDamageEvent getDeathSource() {
        return deathSource;
    }

    public float getExplosionRadius() {
        return explosionRadius;
    }

    @Nullable
    public Item getTool() {
        return tool;
    }

    public boolean addVisitedTable(LootTable table) {
        return visitedTables.add(table);
    }

    public void removeVisitedTable(LootTable table) {
        visitedTables.remove(table);
    }

    public static LootTableContext.LootTableContextBuilder builder(Level level) {
        return internalBuilder().level(level);
    }

    @EnumNaming(EnumNamingStrategies.SnakeCaseStrategy.class)
    public enum EntityTarget {
        THIS("this", LootTableContext::getThisEntity),
        KILLER("killer", LootTableContext::getKillerEntity),
        DIRECT_KILLER("direct_killer", LootTableContext::getDirectKillerEntity),
        KILLER_PLAYER("killer_player", LootTableContext::getKillerPlayer),
        ;

        private static final Map<String, EntityTarget> BY_NAME = Utils.make(new HashMap<>(), lookup -> {
            for (EntityTarget target : values()) {
                lookup.put(target.name, target);
            }
        });

        private final String name;
        private final Function<LootTableContext, Entity> entityGetter;

        EntityTarget(String name, Function<LootTableContext, Entity> entityGetter) {
            this.name = name;
            this.entityGetter = entityGetter;
        }

        @Override
        public String toString() {
            return name;
        }

        public Function<LootTableContext, Entity> getEntityGetter() {
            return entityGetter;
        }

        @Nullable
        public static EntityTarget getByName(String name) {
            return BY_NAME.get(name);
        }
    }
}
