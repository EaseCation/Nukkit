package cn.nukkit.entity;

import cn.nukkit.GameVersion;
import cn.nukkit.entity.item.*;
import cn.nukkit.entity.mob.*;
import cn.nukkit.entity.passive.*;
import cn.nukkit.entity.projectile.*;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

import javax.annotation.Nullable;

import static cn.nukkit.GameVersion.*;

public final class Entities {

    private static final String[] TYPE_TO_IDENTIFIER = new String[256];
    private static final Object2IntMap<String> IDENTIFIER_TO_TYPE = new Object2IntOpenHashMap<>();

    static {
        IDENTIFIER_TO_TYPE.defaultReturnValue(0);
    }

    public static void registerVanillaEntities() {
        registerEntity(EntityID.PLAYER, "player", "Human", EntityHuman.class, true);
        registerEntity(EntityID.ITEM, "item", "Item", EntityItem.class);
        registerEntity(EntityID.XP_ORB, "xp_orb", "XpOrb", EntityXPOrb.class);
        registerEntity(EntityID.PAINTING, "painting", "Painting", EntityPainting.class);
        registerEntity(EntityID.FALLING_BLOCK, "falling_block", "FallingSand", EntityFallingBlock.class);
        registerEntity(EntityID.TNT, "tnt", "PrimedTnt", EntityPrimedTNT.class);
        registerEntity(EntityID.ENDER_CRYSTAL, "ender_crystal", "EndCrystal", EntityEndCrystal.class);
        registerEntity(EntityID.FIREWORKS_ROCKET, "fireworks_rocket", "Firework", EntityFirework.class);
        //Projectile
        registerEntity(EntityID.ARROW, "arrow", "Arrow", EntityArrow.class);
        registerEntity(EntityID.EGG, "egg", "Egg", EntityEgg.class);
        registerEntity(EntityID.ENDER_PEARL, "ender_pearl", "EnderPearl", EntityEnderPearl.class);
        registerEntity(EntityID.FISHING_HOOK, "fishing_hook", "FishingHook", EntityFishingHook.class);
        registerEntity(EntityID.SNOWBALL, "snowball", "Snowball", EntitySnowball.class);
        registerEntity(EntityID.XP_BOTTLE, "xp_bottle", "ThrownExpBottle", EntityExpBottle.class);
        registerEntity(EntityID.SPLASH_POTION, "splash_potion", "ThrownPotion", EntityPotion.class);
        //Vehicle
        registerEntity(EntityID.BOAT, "boat", "Boat", EntityBoat.class);
        registerEntity(EntityID.CHEST_MINECART, "chest_minecart", "MinecartChest", EntityMinecartChest.class);
        registerEntity(EntityID.HOPPER_MINECART, "hopper_minecart", "MinecartHopper", EntityMinecartHopper.class);
        registerEntity(EntityID.MINECART, "minecart", "MinecartRideable", EntityMinecartEmpty.class);
        registerEntity(EntityID.TNT_MINECART, "tnt_minecart", "MinecartTnt", EntityMinecartTNT.class);
        //Monsters
        registerEntity(EntityID.BLAZE, "blaze", "Blaze", EntityBlaze.class);
        registerEntity(EntityID.CAVE_SPIDER, "cave_spider", "CaveSpider", EntityCaveSpider.class);
        registerEntity(EntityID.CREEPER, "creeper", "Creeper", EntityCreeper.class);
        registerEntity(EntityID.ELDER_GUARDIAN, "elder_guardian", "ElderGuardian", EntityElderGuardian.class);
        registerEntity(EntityID.ENDER_DRAGON, "ender_dragon", "EnderDragon", EntityEnderDragon.class);
        registerEntity(EntityID.ENDERMAN, "enderman", "Enderman", EntityEnderman.class);
        registerEntity(EntityID.ENDERMITE, "endermite", "Endermite", EntityEndermite.class);
        registerEntity(EntityID.EVOCATION_ILLAGER, "evocation_illager", "Evoker", EntityEvoker.class);
        registerEntity(EntityID.GHAST, "ghast", "Ghast", EntityGhast.class);
        registerEntity(EntityID.GUARDIAN, "guardian", "Guardian", EntityGuardian.class);
        registerEntity(EntityID.HUSK, "husk", "Husk", EntityHusk.class);
        registerEntity(EntityID.MAGMA_CUBE, "magma_cube", "MagmaCube", EntityMagmaCube.class);
        registerEntity(EntityID.SHULKER, "shulker", "Shulker", EntityShulker.class);
        registerEntity(EntityID.SILVERFISH, "silverfish", "Silverfish", EntitySilverfish.class);
        registerEntity(EntityID.SKELETON, "skeleton", "Skeleton", EntitySkeleton.class);
        registerEntity(EntityID.SLIME, "slime", "Slime", EntitySlime.class);
        registerEntity(EntityID.SPIDER, "spider", "Spider", EntitySpider.class);
        registerEntity(EntityID.STRAY, "stray", "Stray", EntityStray.class);
        registerEntity(EntityID.VEX, "vex", "Vex", EntityVex.class);
        registerEntity(EntityID.VINDICATOR, "vindicator", "Vindicator", EntityVindicator.class);
        registerEntity(EntityID.WITCH, "witch", "Witch", EntityWitch.class);
        registerEntity(EntityID.WITHER, "wither", "Wither", EntityWither.class);
        registerEntity(EntityID.WITHER_SKELETON, "wither_skeleton", "WitherSkeleton", EntityWitherSkeleton.class);
        registerEntity(EntityID.ZOMBIE, "zombie", "Zombie", EntityZombie.class);
        registerEntity(EntityID.ZOMBIE_PIGMAN, "zombie_pigman", "ZombiePigman", EntityZombiePigman.class);
        registerEntity(EntityID.ZOMBIE_VILLAGER, "zombie_villager", "ZombieVillager", EntityZombieVillagerV1.class);
        //Passive
        registerEntity(EntityID.BAT, "bat", "Bat", EntityBat.class);
        registerEntity(EntityID.CHICKEN, "chicken", "Chicken", EntityChicken.class);
        registerEntity(EntityID.COW, "cow", "Cow", EntityCow.class);
        registerEntity(EntityID.DONKEY, "donkey", "Donkey", EntityDonkey.class);
        registerEntity(EntityID.HORSE, "horse", "Horse", EntityHorse.class);
        registerEntity(EntityID.LLAMA, "llama", "Llama", EntityLlama.class);
        registerEntity(EntityID.MOOSHROOM, "mooshroom", "Mooshroom", EntityMooshroom.class);
        registerEntity(EntityID.MULE, "mule", "Mule", EntityMule.class);
        registerEntity(EntityID.OCELOT, "ocelot", "Ocelot", EntityOcelot.class);
        registerEntity(EntityID.PARROT, "parrot", "Parrot", EntityParrot.class);
        registerEntity(EntityID.PIG, "pig", "Pig", EntityPig.class);
        registerEntity(EntityID.POLAR_BEAR, "polar_bear", "PolarBear", EntityPolarBear.class);
        registerEntity(EntityID.RABBIT, "rabbit", "Rabbit", EntityRabbit.class);
        registerEntity(EntityID.SHEEP, "sheep", "Sheep", EntitySheep.class);
        registerEntity(EntityID.SKELETON_HORSE, "skeleton_horse", "SkeletonHorse", EntitySkeletonHorse.class);
        registerEntity(EntityID.SQUID, "squid", "Squid", EntitySquid.class);
        registerEntity(EntityID.VILLAGER, "villager", "Villager", EntityVillagerV1.class);
        registerEntity(EntityID.WOLF, "wolf", "Wolf", EntityWolf.class);
        registerEntity(EntityID.ZOMBIE_HORSE, "zombie_horse", "ZombieHorse", EntityZombieHorse.class);

        registerEntity(EntityID.DOLPHIN, "dolphin", "Dolphin", EntityDolphin.class, V1_4_0);
        registerEntity(EntityID.THROWN_TRIDENT, "thrown_trident", "ThrownTrident", EntityThrownTrident.class, V1_4_0);
        registerEntity(EntityID.PUFFERFISH, "pufferfish", "Pufferfish", EntityPufferfish.class, V1_4_0);
        registerEntity(EntityID.SALMON, "salmon", "Salmon", EntitySalmon.class, V1_4_0);
        registerEntity(EntityID.TROPICALFISH, "tropicalfish", "TropicalFish", EntityTropicalFish.class, V1_4_0);
        registerEntity(EntityID.COD, "cod", "Cod", EntityCod.class, V1_4_0);

        registerEntity(EntityID.TURTLE, "turtle", "Turtle", EntityTurtle.class, V1_5_0);
        registerEntity(EntityID.DROWNED, "drowned", "Drowned", EntityDrowned.class, V1_5_0);

        registerEntity(EntityID.PHANTOM, "phantom", "Phantom", EntityPhantom.class, V1_6_0);

        registerEntity(EntityID.CAT, "cat", "Cat", EntityCat.class, V1_8_0);
        registerEntity(EntityID.PANDA, "panda", "Panda", EntityPanda.class, V1_8_0);

        registerEntity(EntityID.PILLAGER, "pillager", "Pillager", EntityPillager.class, V1_10_0);

        registerEntity(EntityID.RAVAGER, "ravager", "Ravager", EntityRavager.class, V1_11_0);
        registerEntity(EntityID.VILLAGER_V2, "villager_v2", "VillagerV2", EntityVillager.class, V1_11_0);
        registerEntity(EntityID.ZOMBIE_VILLAGER_V2, "zombie_villager_v2", "ZombieVillagerV2", EntityZombieVillager.class, V1_11_0);
        registerEntity(EntityID.WANDERING_TRADER, "wandering_trader", "WanderingTrader", EntityWanderingTrader.class, V1_11_0);

        // 1.13.0
        // fox

        // 1.14.0
        // bee

        // 1.16.0
        // piglin
        // hoglin
        // strider
        // zoglin

        // 1.16.20
        // piglin_brute

        // 1.17.0
        // goat
        // glow_squid
        // axolotl

        // 1.19.0
        // warden
        // frog
        // tadpole
        // allay
        registerEntity(EntityID.CHEST_BOAT, "chest_boat", "BoatChest", EntityBoatChest.class, V1_19_0);

        // 1.19.10
        // trader_llama

    }

    private static Class<? extends Entity> registerEntity(int typeId, String identifier, String name, Class<? extends Entity> clazz) {
        return registerEntity(typeId, identifier, name, clazz, false);
    }

    private static Class<? extends Entity> registerEntity(int typeId, String identifier, String name, Class<? extends Entity> clazz, boolean force) {
        if (!Entity.registerEntity(identifier, name, clazz, force)) {
            return null;
        }
        TYPE_TO_IDENTIFIER[typeId] = identifier;
        IDENTIFIER_TO_TYPE.put(identifier, typeId);
        return clazz;
    }

    /**
     * @param version min required base game version
     */
    private static Class<? extends Entity> registerEntity(int typeId, String identifier, String name, Class<? extends Entity> clazz, GameVersion version) {
        if (!version.isAvailable()) {
            return null;
        }
        return registerEntity(typeId, identifier, name, clazz);
    }

    @Nullable
    public static String getIdentifierByType(int type) {
        if (type <= 0 || type >= TYPE_TO_IDENTIFIER.length) {
            return null;
        }
        return TYPE_TO_IDENTIFIER[type];
    }

    public static int getTypeByIdentifier(String identifier) {
        return IDENTIFIER_TO_TYPE.getInt(identifier);
    }

    private Entities() {
        throw new IllegalStateException();
    }
}
