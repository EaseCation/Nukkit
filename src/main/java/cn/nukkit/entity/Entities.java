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
        registerTodo(EntityID.LIGHTNING_BOLT, "lightning_bolt", "Lightning"/*, EntityLightning.class, EntityLightning::new*/); // runtime only
        registerEntity(EntityID.PLAYER, "player", "Human", EntityHuman.class, EntityHuman::new, true);
        registerEntity(EntityID.ITEM, "item", "Item", EntityItem.class, EntityItem::new);
        registerEntity(EntityID.XP_ORB, "xp_orb", "XpOrb", EntityXPOrb.class, EntityXPOrb::new);
        registerEntity(EntityID.PAINTING, "painting", "Painting", EntityPainting.class, EntityPainting::new);
        registerEntity(EntityID.FALLING_BLOCK, "falling_block", "FallingSand", EntityFallingBlock.class, EntityFallingBlock::new);
        registerEntity(EntityID.TNT, "tnt", "PrimedTnt", EntityPrimedTNT.class, EntityPrimedTNT::new);
        registerEntity(EntityID.ENDER_CRYSTAL, "ender_crystal", "EndCrystal", EntityEndCrystal.class, EntityEndCrystal::new);
        registerEntity(EntityID.FIREWORKS_ROCKET, "fireworks_rocket", "Firework", EntityFirework.class, EntityFirework::new);
        //Projectile
        registerEntity(EntityID.ARROW, "arrow", "Arrow", EntityArrow.class, EntityArrow::new);
        registerEntity(EntityID.EGG, "egg", "Egg", EntityEgg.class, EntityEgg::new);
        registerEntity(EntityID.ENDER_PEARL, "ender_pearl", "EnderPearl", EntityEnderPearl.class, EntityEnderPearl::new);
        registerEntity(EntityID.FISHING_HOOK, "fishing_hook", "FishingHook", EntityFishingHook.class, EntityFishingHook::new);
        registerEntity(EntityID.SNOWBALL, "snowball", "Snowball", EntitySnowball.class, EntitySnowball::new);
        registerEntity(EntityID.XP_BOTTLE, "xp_bottle", "ThrownExpBottle", EntityExpBottle.class, EntityExpBottle::new);
        registerEntity(EntityID.SPLASH_POTION, "splash_potion", "ThrownPotion", EntityPotion.class, EntityPotion::new);
        //Vehicle
        registerEntity(EntityID.BOAT, "boat", "Boat", EntityBoat.class, EntityBoat::new);
        registerEntity(EntityID.CHEST_MINECART, "chest_minecart", "MinecartChest", EntityMinecartChest.class, EntityMinecartChest::new);
        registerEntity(EntityID.HOPPER_MINECART, "hopper_minecart", "MinecartHopper", EntityMinecartHopper.class, EntityMinecartHopper::new);
        registerEntity(EntityID.MINECART, "minecart", "MinecartRideable", EntityMinecartEmpty.class, EntityMinecartEmpty::new);
        registerEntity(EntityID.TNT_MINECART, "tnt_minecart", "MinecartTnt", EntityMinecartTNT.class, EntityMinecartTNT::new);
        //Monsters
        registerEntity(EntityID.BLAZE, "blaze", "Blaze", EntityBlaze.class, EntityBlaze::new);
        registerEntity(EntityID.CAVE_SPIDER, "cave_spider", "CaveSpider", EntityCaveSpider.class, EntityCaveSpider::new);
        registerEntity(EntityID.CREEPER, "creeper", "Creeper", EntityCreeper.class, EntityCreeper::new);
        registerEntity(EntityID.ELDER_GUARDIAN, "elder_guardian", "ElderGuardian", EntityElderGuardian.class, EntityElderGuardian::new);
        registerEntity(EntityID.ENDER_DRAGON, "ender_dragon", "EnderDragon", EntityEnderDragon.class, EntityEnderDragon::new);
        registerEntity(EntityID.ENDERMAN, "enderman", "Enderman", EntityEnderman.class, EntityEnderman::new);
        registerEntity(EntityID.ENDERMITE, "endermite", "Endermite", EntityEndermite.class, EntityEndermite::new);
        registerEntity(EntityID.EVOCATION_ILLAGER, "evocation_illager", "Evoker", EntityEvoker.class, EntityEvoker::new);
        registerEntity(EntityID.GHAST, "ghast", "Ghast", EntityGhast.class, EntityGhast::new);
        registerEntity(EntityID.GUARDIAN, "guardian", "Guardian", EntityGuardian.class, EntityGuardian::new);
        registerEntity(EntityID.HUSK, "husk", "Husk", EntityHusk.class, EntityHusk::new);
        registerEntity(EntityID.MAGMA_CUBE, "magma_cube", "MagmaCube", EntityMagmaCube.class, EntityMagmaCube::new);
        registerEntity(EntityID.SHULKER, "shulker", "Shulker", EntityShulker.class, EntityShulker::new);
        registerEntity(EntityID.SILVERFISH, "silverfish", "Silverfish", EntitySilverfish.class, EntitySilverfish::new);
        registerEntity(EntityID.SKELETON, "skeleton", "Skeleton", EntitySkeleton.class, EntitySkeleton::new);
        registerEntity(EntityID.SLIME, "slime", "Slime", EntitySlime.class, EntitySlime::new);
        registerEntity(EntityID.SPIDER, "spider", "Spider", EntitySpider.class, EntitySpider::new);
        registerEntity(EntityID.STRAY, "stray", "Stray", EntityStray.class, EntityStray::new);
        registerEntity(EntityID.VEX, "vex", "Vex", EntityVex.class, EntityVex::new);
        registerEntity(EntityID.VINDICATOR, "vindicator", "Vindicator", EntityVindicator.class, EntityVindicator::new);
        registerEntity(EntityID.WITCH, "witch", "Witch", EntityWitch.class, EntityWitch::new);
        registerEntity(EntityID.WITHER, "wither", "Wither", EntityWither.class, EntityWither::new);
        registerEntity(EntityID.WITHER_SKELETON, "wither_skeleton", "WitherSkeleton", EntityWitherSkeleton.class, EntityWitherSkeleton::new);
        registerEntity(EntityID.ZOMBIE, "zombie", "Zombie", EntityZombie.class, EntityZombie::new);
        registerEntity(EntityID.ZOMBIE_PIGMAN, "zombie_pigman", "ZombiePigman", EntityZombiePigman.class, EntityZombiePigman::new);
        registerEntity(EntityID.ZOMBIE_VILLAGER, "zombie_villager", "ZombieVillager", EntityZombieVillagerV1.class, EntityZombieVillagerV1::new);
        //Passive
        registerEntity(EntityID.BAT, "bat", "Bat", EntityBat.class, EntityBat::new);
        registerEntity(EntityID.CHICKEN, "chicken", "Chicken", EntityChicken.class, EntityChicken::new);
        registerEntity(EntityID.COW, "cow", "Cow", EntityCow.class, EntityCow::new);
        registerEntity(EntityID.DONKEY, "donkey", "Donkey", EntityDonkey.class, EntityDonkey::new);
        registerEntity(EntityID.HORSE, "horse", "Horse", EntityHorse.class, EntityHorse::new);
        registerEntity(EntityID.IRON_GOLEM, "iron_golem", "IronGolem", EntityIronGolem.class, EntityIronGolem::new);
        registerEntity(EntityID.LLAMA, "llama", "Llama", EntityLlama.class, EntityLlama::new);
        registerEntity(EntityID.MOOSHROOM, "mooshroom", "Mooshroom", EntityMooshroom.class, EntityMooshroom::new);
        registerEntity(EntityID.MULE, "mule", "Mule", EntityMule.class, EntityMule::new);
        registerEntity(EntityID.OCELOT, "ocelot", "Ocelot", EntityOcelot.class, EntityOcelot::new);
        registerEntity(EntityID.PARROT, "parrot", "Parrot", EntityParrot.class, EntityParrot::new);
        registerEntity(EntityID.PIG, "pig", "Pig", EntityPig.class, EntityPig::new);
        registerEntity(EntityID.POLAR_BEAR, "polar_bear", "PolarBear", EntityPolarBear.class, EntityPolarBear::new);
        registerEntity(EntityID.RABBIT, "rabbit", "Rabbit", EntityRabbit.class, EntityRabbit::new);
        registerEntity(EntityID.SHEEP, "sheep", "Sheep", EntitySheep.class, EntitySheep::new);
        registerEntity(EntityID.SKELETON_HORSE, "skeleton_horse", "SkeletonHorse", EntitySkeletonHorse.class, EntitySkeletonHorse::new);
        registerEntity(EntityID.SNOW_GOLEM, "snow_golem", "SnowGolem", EntitySnowGolem.class, EntitySnowGolem::new);
        registerEntity(EntityID.SQUID, "squid", "Squid", EntitySquid.class, EntitySquid::new);
        registerEntity(EntityID.VILLAGER, "villager", "Villager", EntityVillagerV1.class, EntityVillagerV1::new);
        registerEntity(EntityID.WOLF, "wolf", "Wolf", EntityWolf.class, EntityWolf::new);
        registerEntity(EntityID.ZOMBIE_HORSE, "zombie_horse", "ZombieHorse", EntityZombieHorse.class, EntityZombieHorse::new);

        registerEntity(EntityID.ARMOR_STAND, "armor_stand", "ArmorStand", EntityArmorStand.class, EntityArmorStand::new);
        registerTodo(EntityID.EYE_OF_ENDER_SIGNAL, "eye_of_ender_signal", "EyeOfEnder");
        registerTodo(EntityID.SHULKER_BULLET, "shulker_bullet", "ShulkerBullet");
        registerTodo(EntityID.DRAGON_FIREBALL, "dragon_fireball", "DragonFireball");
        registerTodo(EntityID.FIREBALL, "fireball", "Fireball");
        registerTodo(EntityID.LEASH_KNOT, "leash_knot", "LeashKnot");
        registerTodo(EntityID.WITHER_SKULL, "wither_skull", "WitherSkull");
        registerTodo(EntityID.WITHER_SKULL_DANGEROUS, "wither_skull_dangerous", "WitherSkullDangerous");
        registerEntity(EntityID.SMALL_FIREBALL, "small_fireball", "SmallFireball", EntitySmallFireball.class, EntitySmallFireball::new);
        registerEntity(EntityID.AREA_EFFECT_CLOUD, "area_effect_cloud", "AreaEffectCloud", EntityAreaEffectCloud.class, EntityAreaEffectCloud::new);
        registerTodo(EntityID.COMMAND_BLOCK_MINECART, "command_block_minecart", "MinecartCommandBlock");
        registerEntity(EntityID.LINGERING_POTION, "lingering_potion", "LingeringPotion", EntityLingeringPotion.class, EntityLingeringPotion::new);
        registerTodo(EntityID.LLAMA_SPIT, "llama_spit", "LlamaSpit");
        registerTodo(EntityID.EVOCATION_FANG, "evocation_fang", "EvokerFang");

        registerTodo(EntityID.NPC, "npc", "NPC");
        registerTodo(EntityID.AGENT, "agent", "Agent");
        registerTodo(EntityID.TRIPOD_CAMERA, "tripod_camera", "Camera");
        registerTodo(EntityID.ICE_BOMB, "ice_bomb", "IceBomb");
        registerTodo(EntityID.BALLOON, "balloon", "Balloon");

        registerEntity(EntityID.DOLPHIN, "dolphin", "Dolphin", EntityDolphin.class, EntityDolphin::new, V1_4_0);
        registerEntity(EntityID.THROWN_TRIDENT, "thrown_trident", "ThrownTrident", EntityThrownTrident.class, EntityThrownTrident::new, V1_4_0);
        registerEntity(EntityID.PUFFERFISH, "pufferfish", "Pufferfish", EntityPufferfish.class, EntityPufferfish::new, V1_4_0);
        registerEntity(EntityID.SALMON, "salmon", "Salmon", EntitySalmon.class, EntitySalmon::new, V1_4_0);
        registerEntity(EntityID.TROPICALFISH, "tropicalfish", "TropicalFish", EntityTropicalFish.class, EntityTropicalFish::new, V1_4_0);
        registerEntity(EntityID.COD, "cod", "Cod", EntityCod.class, EntityCod::new, V1_4_0);

        registerEntity(EntityID.TURTLE, "turtle", "Turtle", EntityTurtle.class, EntityTurtle::new, V1_5_0);
        registerEntity(EntityID.DROWNED, "drowned", "Drowned", EntityDrowned.class, EntityDrowned::new, V1_5_0);

        registerEntity(EntityID.PHANTOM, "phantom", "Phantom", EntityPhantom.class, EntityPhantom::new, V1_6_0);

        registerEntity(EntityID.CAT, "cat", "Cat", EntityCat.class, EntityCat::new, V1_8_0);
        registerEntity(EntityID.PANDA, "panda", "Panda", EntityPanda.class, EntityPanda::new, V1_8_0);

        registerEntity(EntityID.PILLAGER, "pillager", "Pillager", EntityPillager.class, EntityPillager::new, V1_10_0);

        registerEntity(EntityID.RAVAGER, "ravager", "Ravager", EntityRavager.class, EntityRavager::new, V1_11_0);
        registerEntity(EntityID.VILLAGER_V2, "villager_v2", "VillagerV2", EntityVillager.class, EntityVillager::new, V1_11_0);
        registerEntity(EntityID.ZOMBIE_VILLAGER_V2, "zombie_villager_v2", "ZombieVillagerV2", EntityZombieVillager.class, EntityZombieVillager::new, V1_11_0);
        registerEntity(EntityID.WANDERING_TRADER, "wandering_trader", "WanderingTrader", EntityWanderingTrader.class, EntityWanderingTrader::new, V1_11_0);

        registerTodo(EntityID.ELDER_GUARDIAN_GHOST, "elder_guardian_ghost", "ElderGuardianGhost", V1_13_0); // internal
        registerEntity(EntityID.FOX, "fox", "Fox", EntityFox.class, EntityFox::new, V1_13_0);

        registerEntity(EntityID.BEE, "bee", "Bee", EntityBee.class, EntityBee::new, V1_14_0);

        registerEntity(EntityID.PIGLIN, "piglin", "Piglin", EntityPiglin.class, EntityPiglin::new, V1_16_0);
        registerEntity(EntityID.HOGLIN, "hoglin", "Hoglin", EntityHoglin.class, EntityHoglin::new, V1_16_0);
        registerEntity(EntityID.STRIDER, "strider", "Strider", EntityStrider.class, EntityStrider::new, V1_16_0);
        registerEntity(EntityID.ZOGLIN, "zoglin", "Zoglin", EntityZoglin.class, EntityZoglin::new, V1_16_0);

        registerEntity(EntityID.PIGLIN_BRUTE, "piglin_brute", "PiglinBrute", EntityPiglinBrute.class, EntityPiglinBrute::new, V1_16_20);

        registerEntity(EntityID.GOAT, "goat", "Goat", EntityGoat.class, EntityGoat::new, V1_17_0);
        registerEntity(EntityID.GLOW_SQUID, "glow_squid", "GlowSquid", EntityGlowSquid.class, EntityGlowSquid::new, V1_17_0);
        registerEntity(EntityID.AXOLOTL, "axolotl", "Axolotl", EntityAxolotl.class, EntityAxolotl::new, V1_17_0);

        registerEntity(EntityID.WARDEN, "warden", "Warden", EntityWarden.class, EntityWarden::new, V1_19_0);
        registerEntity(EntityID.FROG, "frog", "Frog", EntityFrog.class, EntityFrog::new, V1_19_0);
        registerEntity(EntityID.TADPOLE, "tadpole", "Tadpole", EntityTadpole.class, EntityTadpole::new, V1_19_0);
        registerEntity(EntityID.ALLAY, "allay", "Allay", EntityAllay.class, EntityAllay::new, V1_19_0);
        registerEntity(EntityID.CHEST_BOAT, "chest_boat", "BoatChest", EntityBoatChest.class, EntityBoatChest::new, V1_19_0);

        registerEntity(EntityID.TRADER_LLAMA, "trader_llama", "TraderLlama", EntityTraderLlama.class, EntityTraderLlama::new, V1_19_10);

        registerEntity(EntityID.CAMEL, "camel", "Camel", EntityCamel.class, EntityCamel::new, V1_20_0);
        registerEntity(EntityID.SNIFFER, "sniffer", "Sniffer", EntitySniffer.class, EntitySniffer::new, V1_20_0);

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
        if (!Entity.registerEntity(identifier, name, clazz, factory, force)) {
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
