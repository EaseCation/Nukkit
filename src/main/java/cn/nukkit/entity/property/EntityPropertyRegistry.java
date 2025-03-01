package cn.nukkit.entity.property;

import cn.nukkit.GameVersion;
import cn.nukkit.entity.Entities;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityFullNames;
import cn.nukkit.entity.EntityID;

import java.util.HashMap;
import java.util.Map;

import static cn.nukkit.GameVersion.*;
import static cn.nukkit.entity.property.EntityPropertyIntValues.*;
import static cn.nukkit.entity.property.EntityPropertyNames.*;
import static cn.nukkit.entity.property.EntityPropertyStringValues.*;

public final class EntityPropertyRegistry {
    private static final Map<String, EntityProperties> REGISTRY = new HashMap<>();
    private static final EntityProperties[] BY_TYPE = new EntityProperties[256];

    public static void registerVanillaProperties() {
        registerProperties(V1_19_70, EntityID.BEE, EntityFullNames.BEE,
                new BooleanEntityProperty(HAS_NECTAR));

        registerProperties(V1_20_80, EntityID.ARMADILLO, EntityFullNames.ARMADILLO,
                new EnumEntityProperty(ARMADILLO_STATE,
                        ARMADILLO_STATE_UNROLLED,
                        ARMADILLO_STATE_ROLLED_UP,
                        ARMADILLO_STATE_ROLLED_UP_PEEKING,
                        ARMADILLO_STATE_ROLLED_UP_RELAXING,
                        ARMADILLO_STATE_ROLLED_UP_UNROLLING));

        registerProperties(V1_21_50, EntityID.CREAKING, EntityFullNames.CREAKING,
                new EnumEntityProperty(CREAKING_STATE,
                        CREAKING_STATE_NEUTRAL,
                        CREAKING_STATE_HOSTILE_OBSERVED,
                        CREAKING_STATE_HOSTILE_UNOBSERVED,
                        CREAKING_STATE_TWITCHING,
                        CREAKING_STATE_CRUMBLING
                ),
                new IntEntityProperty(CREAKING_SWAYING_TICKS, CREAKING_SWAYING_TICKS_MIN, CREAKING_SWAYING_TICKS_MAX));

        registerProperties(V1_21_70, EntityID.CHICKEN, EntityFullNames.CHICKEN,
                new EnumEntityProperty(CLIMATE_VARIANT,
                        CLIMATE_VARIANT_TEMPERATE,
                        CLIMATE_VARIANT_WARM,
                        CLIMATE_VARIANT_COLD));
        registerProperties(V1_21_70, EntityID.COW, EntityFullNames.COW,
                new EnumEntityProperty(CLIMATE_VARIANT,
                        CLIMATE_VARIANT_TEMPERATE,
                        CLIMATE_VARIANT_WARM,
                        CLIMATE_VARIANT_COLD));
        registerProperties(V1_21_70, EntityID.EGG, EntityFullNames.EGG,
                new EnumEntityProperty(CLIMATE_VARIANT,
                        CLIMATE_VARIANT_TEMPERATE,
                        CLIMATE_VARIANT_WARM,
                        CLIMATE_VARIANT_COLD));
        registerProperties(V1_21_70, EntityID.PIG, EntityFullNames.PIG,
                new EnumEntityProperty(CLIMATE_VARIANT,
                        CLIMATE_VARIANT_TEMPERATE,
                        CLIMATE_VARIANT_WARM,
                        CLIMATE_VARIANT_COLD));
        registerProperties(V1_21_70, EntityID.WOLF, EntityFullNames.WOLF,
                new EnumEntityProperty(SOUND_VARIANT,
                        SOUND_VARIANT_DEFAULT,
                        SOUND_VARIANT_BIG,
                        SOUND_VARIANT_CUTE,
                        SOUND_VARIANT_GRUMPY,
                        SOUND_VARIANT_MAD,
                        SOUND_VARIANT_PUGLIN,
                        SOUND_VARIANT_SAD));
    }

    private static void registerProperties(int entityType, String entityIdentifier, EntityProperty... properties) {
        EntityProperties collection = REGISTRY.computeIfAbsent(entityIdentifier, EntityProperties::new);
        if (entityType > 0) {
            EntityProperties current = BY_TYPE[entityType];
            if (current == null) {
                BY_TYPE[entityType] = collection;
            } else if (current != collection) {
                throw new IllegalArgumentException("Entity " + entityIdentifier + " is already registered with type " + entityType);
            }
        }
        collection.registerProperties(properties);
    }

    /**
     * @param version min required base game version
     */
    private static void registerProperties(GameVersion version, int entityType, String entityIdentifier, EntityProperty... properties) {
        if (!version.isAvailable()) {
//            return;
        }
        registerProperties(entityType, entityIdentifier, properties);
    }

    public static void registerCustomProperties(String entityIdentifier, EntityProperty... properties) {
        for (EntityProperty property : properties) {
            String name = property.getName();
            if (name.split(":").length != 2) {
                throw new IllegalArgumentException("Invalid namespaced custom entity property name: " + name);
            }
            if (name.startsWith("minecraft:")) {
                throw new IllegalArgumentException("Invalid custom entity property name: " + property.getName());
            }
        }
        registerProperties(Entities.getTypeByIdentifier(entityIdentifier), entityIdentifier, properties);
    }

    public static EntityProperties getProperties(Entity entity) {
        int type = entity.getNetworkId();
        if (type == 0) {
            return getProperties(entity.getIdentifier());
        }
        if (type == -1) {
            type = EntityID.PLAYER;
        }
        return getProperties(type);
    }

    public static EntityProperties getProperties(int entityType) {
        if (entityType < 0 || entityType >= BY_TYPE.length) {
            return EntityProperties.EMPTY;
        }
        EntityProperties properties = BY_TYPE[entityType];
        if (properties == null) {
            return EntityProperties.EMPTY;
        }
        return properties;
    }

    public static EntityProperties getProperties(String entityIdentifier) {
        return REGISTRY.getOrDefault(entityIdentifier, EntityProperties.EMPTY);
    }

    public static Map<String, EntityProperties> getRegistry() {
        return REGISTRY;
    }

    private EntityPropertyRegistry() {
        throw new IllegalStateException();
    }
}
