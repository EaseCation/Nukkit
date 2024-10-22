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
        registerTodo(EntityID.LIGHTNING_BOLT, EntityNames.LIGHTNING_BOLT, "Lightning"/*, EntityLightning.class, EntityLightning::new*/); // runtime only
        registerEntity(EntityID.PLAYER, EntityNames.PLAYER, "Human", EntityHuman.class, EntityHuman::new, true);
        registerEntity(EntityID.ITEM, EntityNames.ITEM, "Item", EntityItem.class, EntityItem::new);
        registerEntity(EntityID.XP_ORB, EntityNames.XP_ORB, "XpOrb", EntityXPOrb.class, EntityXPOrb::new);
        registerEntity(EntityID.PAINTING, EntityNames.PAINTING, "Painting", EntityPainting.class, EntityPainting::new);
        registerEntity(EntityID.FALLING_BLOCK, EntityNames.FALLING_BLOCK, "FallingSand", EntityFallingBlock.class, EntityFallingBlock::new);
        registerEntity(EntityID.TNT, EntityNames.TNT, "PrimedTnt", EntityPrimedTNT.class, EntityPrimedTNT::new);
        registerEntity(EntityID.ENDER_CRYSTAL, EntityNames.ENDER_CRYSTAL, "EndCrystal", EntityEndCrystal.class, EntityEndCrystal::new);
        registerEntity(EntityID.FIREWORKS_ROCKET, EntityNames.FIREWORKS_ROCKET, "Firework", EntityFirework.class, EntityFirework::new);
        //Projectile
        registerEntity(EntityID.ARROW, EntityNames.ARROW, "Arrow", EntityArrow.class, EntityArrow::new);
        registerEntity(EntityID.EGG, EntityNames.EGG, "Egg", EntityEgg.class, EntityEgg::new);
        registerEntity(EntityID.ENDER_PEARL, EntityNames.ENDER_PEARL, "EnderPearl", EntityEnderPearl.class, EntityEnderPearl::new);
        registerEntity(EntityID.FISHING_HOOK, EntityNames.FISHING_HOOK, "FishingHook", EntityFishingHook.class, EntityFishingHook::new);
        registerEntity(EntityID.SNOWBALL, EntityNames.SNOWBALL, "Snowball", EntitySnowball.class, EntitySnowball::new);
        registerEntity(EntityID.XP_BOTTLE, EntityNames.XP_BOTTLE, "ThrownExpBottle", EntityExpBottle.class, EntityExpBottle::new);
        registerEntity(EntityID.SPLASH_POTION, EntityNames.SPLASH_POTION, "ThrownPotion", EntityPotion.class, EntityPotion::new);
        //Vehicle
        registerEntity(EntityID.BOAT, EntityNames.BOAT, "Boat", EntityBoat.class, EntityBoat::new);
        registerEntity(EntityID.CHEST_MINECART, EntityNames.CHEST_MINECART, "MinecartChest", EntityMinecartChest.class, EntityMinecartChest::new);
        registerEntity(EntityID.HOPPER_MINECART, EntityNames.HOPPER_MINECART, "MinecartHopper", EntityMinecartHopper.class, EntityMinecartHopper::new);
        registerEntity(EntityID.MINECART, EntityNames.MINECART, "MinecartRideable", EntityMinecartEmpty.class, EntityMinecartEmpty::new);
        registerEntity(EntityID.TNT_MINECART, EntityNames.TNT_MINECART, "MinecartTnt", EntityMinecartTNT.class, EntityMinecartTNT::new);
        //Monsters
        registerEntity(EntityID.BLAZE, EntityNames.BLAZE, "Blaze", EntityBlaze.class, EntityBlaze::new);
        registerEntity(EntityID.CAVE_SPIDER, EntityNames.CAVE_SPIDER, "CaveSpider", EntityCaveSpider.class, EntityCaveSpider::new);
        registerEntity(EntityID.CREEPER, EntityNames.CREEPER, "Creeper", EntityCreeper.class, EntityCreeper::new);
        registerEntity(EntityID.ELDER_GUARDIAN, EntityNames.ELDER_GUARDIAN, "ElderGuardian", EntityElderGuardian.class, EntityElderGuardian::new);
        registerEntity(EntityID.ENDER_DRAGON, EntityNames.ENDER_DRAGON, "EnderDragon", EntityEnderDragon.class, EntityEnderDragon::new);
        registerEntity(EntityID.ENDERMAN, EntityNames.ENDERMAN, "Enderman", EntityEnderman.class, EntityEnderman::new);
        registerEntity(EntityID.ENDERMITE, EntityNames.ENDERMITE, "Endermite", EntityEndermite.class, EntityEndermite::new);
        registerEntity(EntityID.EVOCATION_ILLAGER, EntityNames.EVOCATION_ILLAGER, "Evoker", EntityEvoker.class, EntityEvoker::new);
        registerEntity(EntityID.GHAST, EntityNames.GHAST, "Ghast", EntityGhast.class, EntityGhast::new);
        registerEntity(EntityID.GUARDIAN, EntityNames.GUARDIAN, "Guardian", EntityGuardian.class, EntityGuardian::new);
        registerEntity(EntityID.HUSK, EntityNames.HUSK, "Husk", EntityHusk.class, EntityHusk::new);
        registerEntity(EntityID.MAGMA_CUBE, EntityNames.MAGMA_CUBE, "MagmaCube", EntityMagmaCube.class, EntityMagmaCube::new);
        registerEntity(EntityID.SHULKER, EntityNames.SHULKER, "Shulker", EntityShulker.class, EntityShulker::new);
        registerEntity(EntityID.SILVERFISH, EntityNames.SILVERFISH, "Silverfish", EntitySilverfish.class, EntitySilverfish::new);
        registerEntity(EntityID.SKELETON, EntityNames.SKELETON, "Skeleton", EntitySkeleton.class, EntitySkeleton::new);
        registerEntity(EntityID.SLIME, EntityNames.SLIME, "Slime", EntitySlime.class, EntitySlime::new);
        registerEntity(EntityID.SPIDER, EntityNames.SPIDER, "Spider", EntitySpider.class, EntitySpider::new);
        registerEntity(EntityID.STRAY, EntityNames.STRAY, "Stray", EntityStray.class, EntityStray::new);
        registerEntity(EntityID.VEX, EntityNames.VEX, "Vex", EntityVex.class, EntityVex::new);
        registerEntity(EntityID.VINDICATOR, EntityNames.VINDICATOR, "Vindicator", EntityVindicator.class, EntityVindicator::new);
        registerEntity(EntityID.WITCH, EntityNames.WITCH, "Witch", EntityWitch.class, EntityWitch::new);
        registerEntity(EntityID.WITHER, EntityNames.WITHER, "Wither", EntityWither.class, EntityWither::new);
        registerEntity(EntityID.WITHER_SKELETON, EntityNames.WITHER_SKELETON, "WitherSkeleton", EntityWitherSkeleton.class, EntityWitherSkeleton::new);
        registerEntity(EntityID.ZOMBIE, EntityNames.ZOMBIE, "Zombie", EntityZombie.class, EntityZombie::new);
        registerEntity(EntityID.ZOMBIE_PIGMAN, EntityNames.ZOMBIE_PIGMAN, "ZombiePigman", EntityZombiePigman.class, EntityZombiePigman::new);
        registerEntity(EntityID.ZOMBIE_VILLAGER, EntityNames.ZOMBIE_VILLAGER, "ZombieVillager", EntityZombieVillagerV1.class, EntityZombieVillagerV1::new);
        //Passive
        registerEntity(EntityID.BAT, EntityNames.BAT, "Bat", EntityBat.class, EntityBat::new);
        registerEntity(EntityID.CHICKEN, EntityNames.CHICKEN, "Chicken", EntityChicken.class, EntityChicken::new);
        registerEntity(EntityID.COW, EntityNames.COW, "Cow", EntityCow.class, EntityCow::new);
        registerEntity(EntityID.DONKEY, EntityNames.DONKEY, "Donkey", EntityDonkey.class, EntityDonkey::new);
        registerEntity(EntityID.HORSE, EntityNames.HORSE, "Horse", EntityHorse.class, EntityHorse::new);
        registerEntity(EntityID.IRON_GOLEM, EntityNames.IRON_GOLEM, "IronGolem", EntityIronGolem.class, EntityIronGolem::new);
        registerEntity(EntityID.LLAMA, EntityNames.LLAMA, "Llama", EntityLlama.class, EntityLlama::new);
        registerEntity(EntityID.MOOSHROOM, EntityNames.MOOSHROOM, "Mooshroom", EntityMooshroom.class, EntityMooshroom::new);
        registerEntity(EntityID.MULE, EntityNames.MULE, "Mule", EntityMule.class, EntityMule::new);
        registerEntity(EntityID.OCELOT, EntityNames.OCELOT, "Ocelot", EntityOcelot.class, EntityOcelot::new);
        registerEntity(EntityID.PARROT, EntityNames.PARROT, "Parrot", EntityParrot.class, EntityParrot::new);
        registerEntity(EntityID.PIG, EntityNames.PIG, "Pig", EntityPig.class, EntityPig::new);
        registerEntity(EntityID.POLAR_BEAR, EntityNames.POLAR_BEAR, "PolarBear", EntityPolarBear.class, EntityPolarBear::new);
        registerEntity(EntityID.RABBIT, EntityNames.RABBIT, "Rabbit", EntityRabbit.class, EntityRabbit::new);
        registerEntity(EntityID.SHEEP, EntityNames.SHEEP, "Sheep", EntitySheep.class, EntitySheep::new);
        registerEntity(EntityID.SKELETON_HORSE, EntityNames.SKELETON_HORSE, "SkeletonHorse", EntitySkeletonHorse.class, EntitySkeletonHorse::new);
        registerEntity(EntityID.SNOW_GOLEM, EntityNames.SNOW_GOLEM, "SnowGolem", EntitySnowGolem.class, EntitySnowGolem::new);
        registerEntity(EntityID.SQUID, EntityNames.SQUID, "Squid", EntitySquid.class, EntitySquid::new);
        registerEntity(EntityID.VILLAGER, EntityNames.VILLAGER, "Villager", EntityVillagerV1.class, EntityVillagerV1::new);
        registerEntity(EntityID.WOLF, EntityNames.WOLF, "Wolf", EntityWolf.class, EntityWolf::new);
        registerEntity(EntityID.ZOMBIE_HORSE, EntityNames.ZOMBIE_HORSE, "ZombieHorse", EntityZombieHorse.class, EntityZombieHorse::new);

        registerEntity(EntityID.ARMOR_STAND, EntityNames.ARMOR_STAND, "ArmorStand", EntityArmorStand.class, EntityArmorStand::new);
        registerTodo(EntityID.EYE_OF_ENDER_SIGNAL, EntityNames.EYE_OF_ENDER_SIGNAL, "EyeOfEnder");
        registerTodo(EntityID.SHULKER_BULLET, EntityNames.SHULKER_BULLET, "ShulkerBullet");
        registerTodo(EntityID.DRAGON_FIREBALL, EntityNames.DRAGON_FIREBALL, "DragonFireball");
        registerTodo(EntityID.FIREBALL, EntityNames.FIREBALL, "Fireball");
        registerTodo(EntityID.LEASH_KNOT, EntityNames.LEASH_KNOT, "LeashKnot");
        registerTodo(EntityID.WITHER_SKULL, EntityNames.WITHER_SKULL, "WitherSkull");
        registerTodo(EntityID.WITHER_SKULL_DANGEROUS, EntityNames.WITHER_SKULL_DANGEROUS, "WitherSkullDangerous");
        registerEntity(EntityID.SMALL_FIREBALL, EntityNames.SMALL_FIREBALL, "SmallFireball", EntitySmallFireball.class, EntitySmallFireball::new);
        registerEntity(EntityID.AREA_EFFECT_CLOUD, EntityNames.AREA_EFFECT_CLOUD, "AreaEffectCloud", EntityAreaEffectCloud.class, EntityAreaEffectCloud::new);
        registerTodo(EntityID.COMMAND_BLOCK_MINECART, EntityNames.COMMAND_BLOCK_MINECART, "MinecartCommandBlock");
        registerEntity(EntityID.LINGERING_POTION, EntityNames.LINGERING_POTION, "LingeringPotion", EntityLingeringPotion.class, EntityLingeringPotion::new);
        registerTodo(EntityID.LLAMA_SPIT, EntityNames.LLAMA_SPIT, "LlamaSpit");
        registerTodo(EntityID.EVOCATION_FANG, EntityNames.EVOCATION_FANG, "EvokerFang");

        registerTodo(EntityID.NPC, EntityNames.NPC, "NPC");
        registerTodo(EntityID.AGENT, EntityNames.AGENT, "Agent");
        registerTodo(EntityID.TRIPOD_CAMERA, EntityNames.TRIPOD_CAMERA, "Camera");
        registerTodo(EntityID.ICE_BOMB, EntityNames.ICE_BOMB, "IceBomb");
        registerTodo(EntityID.BALLOON, EntityNames.BALLOON, "Balloon");

        registerEntity(EntityID.DOLPHIN, EntityNames.DOLPHIN, "Dolphin", EntityDolphin.class, EntityDolphin::new, V1_4_0);
        registerEntity(EntityID.THROWN_TRIDENT, EntityNames.THROWN_TRIDENT, "ThrownTrident", EntityThrownTrident.class, EntityThrownTrident::new, V1_4_0);
        registerEntity(EntityID.PUFFERFISH, EntityNames.PUFFERFISH, "Pufferfish", EntityPufferfish.class, EntityPufferfish::new, V1_4_0);
        registerEntity(EntityID.SALMON, EntityNames.SALMON, "Salmon", EntitySalmon.class, EntitySalmon::new, V1_4_0);
        registerEntity(EntityID.TROPICALFISH, EntityNames.TROPICALFISH, "TropicalFish", EntityTropicalFish.class, EntityTropicalFish::new, V1_4_0);
        registerEntity(EntityID.COD, EntityNames.COD, "Cod", EntityCod.class, EntityCod::new, V1_4_0);

        registerEntity(EntityID.TURTLE, EntityNames.TURTLE, "Turtle", EntityTurtle.class, EntityTurtle::new, V1_5_0);
        registerEntity(EntityID.DROWNED, EntityNames.DROWNED, "Drowned", EntityDrowned.class, EntityDrowned::new, V1_5_0);

        registerEntity(EntityID.PHANTOM, EntityNames.PHANTOM, "Phantom", EntityPhantom.class, EntityPhantom::new, V1_6_0);

        registerEntity(EntityID.CAT, EntityNames.CAT, "Cat", EntityCat.class, EntityCat::new, V1_8_0);
        registerEntity(EntityID.PANDA, EntityNames.PANDA, "Panda", EntityPanda.class, EntityPanda::new, V1_8_0);

        registerEntity(EntityID.PILLAGER, EntityNames.PILLAGER, "Pillager", EntityPillager.class, EntityPillager::new, V1_10_0);

        registerEntity(EntityID.RAVAGER, EntityNames.RAVAGER, "Ravager", EntityRavager.class, EntityRavager::new, V1_11_0);
        registerEntity(EntityID.VILLAGER_V2, EntityNames.VILLAGER_V2, "VillagerV2", EntityVillager.class, EntityVillager::new, V1_11_0);
        registerEntity(EntityID.ZOMBIE_VILLAGER_V2, EntityNames.ZOMBIE_VILLAGER_V2, "ZombieVillagerV2", EntityZombieVillager.class, EntityZombieVillager::new, V1_11_0);
        registerEntity(EntityID.WANDERING_TRADER, EntityNames.WANDERING_TRADER, "WanderingTrader", EntityWanderingTrader.class, EntityWanderingTrader::new, V1_11_0);

        registerTodo(EntityID.ELDER_GUARDIAN_GHOST, EntityNames.ELDER_GUARDIAN_GHOST, "ElderGuardianGhost", V1_13_0); // internal
        registerEntity(EntityID.FOX, EntityNames.FOX, "Fox", EntityFox.class, EntityFox::new, V1_13_0);

        registerEntity(EntityID.BEE, EntityNames.BEE, "Bee", EntityBee.class, EntityBee::new, V1_14_0);

        registerEntity(EntityID.PIGLIN, EntityNames.PIGLIN, "Piglin", EntityPiglin.class, EntityPiglin::new, V1_16_0);
        registerEntity(EntityID.HOGLIN, EntityNames.HOGLIN, "Hoglin", EntityHoglin.class, EntityHoglin::new, V1_16_0);
        registerEntity(EntityID.STRIDER, EntityNames.STRIDER, "Strider", EntityStrider.class, EntityStrider::new, V1_16_0);
        registerEntity(EntityID.ZOGLIN, EntityNames.ZOGLIN, "Zoglin", EntityZoglin.class, EntityZoglin::new, V1_16_0);

        registerEntity(EntityID.PIGLIN_BRUTE, EntityNames.PIGLIN_BRUTE, "PiglinBrute", EntityPiglinBrute.class, EntityPiglinBrute::new, V1_16_20);

        registerEntity(EntityID.GOAT, EntityNames.GOAT, "Goat", EntityGoat.class, EntityGoat::new, V1_17_0);
        registerEntity(EntityID.GLOW_SQUID, EntityNames.GLOW_SQUID, "GlowSquid", EntityGlowSquid.class, EntityGlowSquid::new, V1_17_0);
        registerEntity(EntityID.AXOLOTL, EntityNames.AXOLOTL, "Axolotl", EntityAxolotl.class, EntityAxolotl::new, V1_17_0);

        registerEntity(EntityID.WARDEN, EntityNames.WARDEN, "Warden", EntityWarden.class, EntityWarden::new, V1_19_0);
        registerEntity(EntityID.FROG, EntityNames.FROG, "Frog", EntityFrog.class, EntityFrog::new, V1_19_0);
        registerEntity(EntityID.TADPOLE, EntityNames.TADPOLE, "Tadpole", EntityTadpole.class, EntityTadpole::new, V1_19_0);
        registerEntity(EntityID.ALLAY, EntityNames.ALLAY, "Allay", EntityAllay.class, EntityAllay::new, V1_19_0);
        registerEntity(EntityID.CHEST_BOAT, EntityNames.CHEST_BOAT, "BoatChest", EntityBoatChest.class, EntityBoatChest::new, V1_19_0);

        registerEntity(EntityID.TRADER_LLAMA, EntityNames.TRADER_LLAMA, "TraderLlama", EntityTraderLlama.class, EntityTraderLlama::new, V1_19_10);

        registerEntity(EntityID.CAMEL, EntityNames.CAMEL, "Camel", EntityCamel.class, EntityCamel::new, V1_20_0);
        registerEntity(EntityID.SNIFFER, EntityNames.SNIFFER, "Sniffer", EntitySniffer.class, EntitySniffer::new, V1_20_0);

        registerEntity(EntityID.ARMADILLO, EntityNames.ARMADILLO, "Armadillo", EntityArmadillo.class, EntityArmadillo::new, V1_20_80);

        registerEntity(EntityID.BREEZE, EntityNames.BREEZE, "Breeze", EntityBreeze.class, EntityBreeze::new, V1_21_0);
        registerTodo(EntityID.BREEZE_WIND_CHARGE_PROJECTILE, EntityNames.BREEZE_WIND_CHARGE_PROJECTILE, "BreezeWindCharge");
//        registerEntity(EntityID.BREEZE_WIND_CHARGE_PROJECTILE, EntityNames.BREEZE_WIND_CHARGE_PROJECTILE, "BreezeWindCharge", EntityWindChargeBreeze.class, EntityWindChargeBreeze::new, V1_21_0);
        registerTodo(EntityID.WIND_CHARGE_PROJECTILE, EntityNames.WIND_CHARGE_PROJECTILE, "WindCharge");
//        registerEntity(EntityID.WIND_CHARGE_PROJECTILE, EntityNames.WIND_CHARGE_PROJECTILE, "WindCharge", EntityWindCharge.class, EntityWindCharge::new, V1_21_0);
        registerEntity(EntityID.BOGGED, EntityNames.BOGGED, "Bogged", EntityBogged.class, EntityBogged::new, V1_21_0);
        registerTodo(EntityID.OMINOUS_ITEM_SPAWNER, EntityNames.OMINOUS_ITEM_SPAWNER, "OminousItemSpawner");
//        registerEntity(EntityID.OMINOUS_ITEM_SPAWNER, EntityNames.OMINOUS_ITEM_SPAWNER, "OminousItemSpawner", EntityOminousItemSpawner.class, EntityOminousItemSpawner::new, V1_21_0);

        registerTodo(EntityID.CREAKING, EntityNames.CREAKING, "Creaking");
//        registerEntity(EntityID.CREAKING, EntityNames.CREAKING, "Creaking", EntityCreaking.class, EntityCreaking::new, V1_21_50);
    }

    private static boolean registerTodo(int typeId, String identifier, String name) {
        TYPE_TO_IDENTIFIER[typeId] = identifier;
        IDENTIFIER_TO_TYPE.put(identifier, typeId);
        return true;
    }

    private static boolean registerTodo(int typeId, String identifier, String name, GameVersion version) {
        if (!version.isAvailable()) {
            return false;
        }
        return registerTodo(typeId, identifier, name);
    }

    private static Class<? extends Entity> registerEntity(int typeId, String identifier, String name, Class<? extends Entity> clazz, EntityFactory factory) {
        return registerEntity(typeId, identifier, name, clazz, factory, false);
    }

    private static Class<? extends Entity> registerEntity(int typeId, String identifier, String name, Class<? extends Entity> clazz, EntityFactory factory, boolean force) {
        if (!Entity.registerEntity("minecraft:" + identifier, identifier, name, clazz, factory, force)) {
            return null;
        }
        TYPE_TO_IDENTIFIER[typeId] = identifier;
        IDENTIFIER_TO_TYPE.put(identifier, typeId);
        return clazz;
    }

    /**
     * @param version min required base game version
     */
    private static Class<? extends Entity> registerEntity(int typeId, String identifier, String name, Class<? extends Entity> clazz, EntityFactory factory, GameVersion version) {
        if (!version.isAvailable()) {
            return null;
        }
        return registerEntity(typeId, identifier, name, clazz, factory);
    }

    @Nullable
    public static String getIdentifierByType(int type) {
        return getIdentifierByType(type, true);
    }

    @Nullable
    public static String getIdentifierByType(int type, boolean withNamespace) {
        if (type <= 0 || type >= TYPE_TO_IDENTIFIER.length) {
            return null;
        }

        String identifier = TYPE_TO_IDENTIFIER[type];
        if (identifier == null) {
            return null;
        }

        if (!withNamespace) {
            return identifier;
        }

        return "minecraft:" + identifier;
    }

    public static int getTypeByIdentifier(String identifier) {
        return getTypeByIdentifier(identifier, true);
    }

    public static int getTypeByIdentifier(String identifier, boolean namespaced) {
        if (namespaced && identifier.startsWith("minecraft:")) {
            identifier = identifier.substring(10);
        }
        return IDENTIFIER_TO_TYPE.getInt(identifier);
    }

    private Entities() {
        throw new IllegalStateException();
    }
}
