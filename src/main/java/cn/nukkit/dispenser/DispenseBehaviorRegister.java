package cn.nukkit.dispenser;

import cn.nukkit.entity.EntityID;
import cn.nukkit.entity.item.*;
import cn.nukkit.entity.passive.*;
import cn.nukkit.entity.projectile.*;
import cn.nukkit.entity.property.EntityPropertyNames;
import cn.nukkit.entity.property.EntityPropertyStringValues;
import cn.nukkit.item.*;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.potion.Effect;
import cn.nukkit.potion.Potion;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

/**
 * @author CreeperFace
 */
public final class DispenseBehaviorRegister {

    private static final Int2ObjectMap<DispenseBehavior> behaviors = new Int2ObjectOpenHashMap<>();
    private static final DispenseBehavior defaultBehavior = new DefaultDispenseBehavior();

    static {
        behaviors.defaultReturnValue(defaultBehavior);
    }

    public static void registerBehavior(int itemId, DispenseBehavior behavior) {
        behaviors.put(itemId, behavior);
    }

    public static DispenseBehavior getBehavior(int id) {
        return behaviors.get(id);
    }

    public static void removeDispenseBehavior(int id) {
        behaviors.remove(id);
    }

    public static void init() {
        registerBehavior(ItemID.OAK_BOAT, new BoatDispenseBehavior(ItemBoat.OAK, EntityBoat::new));
        registerBehavior(ItemID.SPRUCE_BOAT, new BoatDispenseBehavior(ItemBoat.SPRUCE, EntityBoat::new));
        registerBehavior(ItemID.BIRCH_BOAT, new BoatDispenseBehavior(ItemBoat.BIRCH, EntityBoat::new));
        registerBehavior(ItemID.JUNGLE_BOAT, new BoatDispenseBehavior(ItemBoat.JUNGLE, EntityBoat::new));
        registerBehavior(ItemID.ACACIA_BOAT, new BoatDispenseBehavior(ItemBoat.ACACIA, EntityBoat::new));
        registerBehavior(ItemID.DARK_OAK_BOAT, new BoatDispenseBehavior(ItemBoat.DARK_OAK, EntityBoat::new));
        registerBehavior(ItemID.MANGROVE_BOAT, new BoatDispenseBehavior(ItemBoat.MANGROVE, EntityBoat::new));
        registerBehavior(ItemID.BAMBOO_RAFT, new BoatDispenseBehavior(ItemBoat.RAFT, EntityBoat::new));
        registerBehavior(ItemID.CHERRY_BOAT, new BoatDispenseBehavior(ItemBoat.CHERRY, EntityBoat::new));
        registerBehavior(ItemID.PALE_OAK_BOAT, new BoatDispenseBehavior(ItemBoat.PALE_OAK, EntityBoat::new));
        registerBehavior(ItemID.OAK_CHEST_BOAT, new BoatDispenseBehavior(ItemBoatChest.OAK, EntityBoatChest::new));
        registerBehavior(ItemID.SPRUCE_CHEST_BOAT, new BoatDispenseBehavior(ItemBoatChest.SPRUCE, EntityBoatChest::new));
        registerBehavior(ItemID.BIRCH_CHEST_BOAT, new BoatDispenseBehavior(ItemBoatChest.BIRCH, EntityBoatChest::new));
        registerBehavior(ItemID.JUNGLE_CHEST_BOAT, new BoatDispenseBehavior(ItemBoatChest.JUNGLE, EntityBoatChest::new));
        registerBehavior(ItemID.ACACIA_CHEST_BOAT, new BoatDispenseBehavior(ItemBoatChest.ACACIA, EntityBoatChest::new));
        registerBehavior(ItemID.DARK_OAK_CHEST_BOAT, new BoatDispenseBehavior(ItemBoatChest.DARK_OAK, EntityBoatChest::new));
        registerBehavior(ItemID.MANGROVE_CHEST_BOAT, new BoatDispenseBehavior(ItemBoatChest.MANGROVE, EntityBoatChest::new));
        registerBehavior(ItemID.BAMBOO_CHEST_RAFT, new BoatDispenseBehavior(ItemBoatChest.RAFT, EntityBoatChest::new));
        registerBehavior(ItemID.CHERRY_CHEST_BOAT, new BoatDispenseBehavior(ItemBoatChest.CHERRY, EntityBoatChest::new));
        registerBehavior(ItemID.PALE_OAK_CHEST_BOAT, new BoatDispenseBehavior(ItemBoatChest.PALE_OAK, EntityBoatChest::new));
        registerBehavior(ItemID.BUCKET, new BucketDispenseBehavior());
        registerBehavior(ItemID.WATER_BUCKET, new WaterBucketDispenseBehavior());
        registerBehavior(ItemID.COD_BUCKET, new WaterBucketDispenseBehavior(EntityCod::new));
        registerBehavior(ItemID.SALMON_BUCKET, new WaterBucketDispenseBehavior(EntitySalmon::new));
        registerBehavior(ItemID.TROPICAL_FISH_BUCKET, new WaterBucketDispenseBehavior(EntityTropicalFish::new));
        registerBehavior(ItemID.PUFFERFISH_BUCKET, new WaterBucketDispenseBehavior(EntityPufferfish::new));
        registerBehavior(ItemID.AXOLOTL_BUCKET, new WaterBucketDispenseBehavior(EntityAxolotl::new));
        registerBehavior(ItemID.TADPOLE_BUCKET, new WaterBucketDispenseBehavior(EntityTadpole::new));
        registerBehavior(ItemID.LAVA_BUCKET, new LavaBucketDispenseBehavior());
        registerBehavior(ItemID.POWDER_SNOW_BUCKET, new PowderSnowBucketDispenseBehavior());
        registerBehavior(ItemID.BONE_MEAL, new FertilizerDispenseBehavior());
        registerBehavior(ItemID.RAPID_FERTILIZER, new FertilizerDispenseBehavior());
        registerBehavior(ItemID.FIREWORK_ROCKET, new FireworksDispenseBehavior());
        registerBehavior(ItemID.FLINT_AND_STEEL, new FlintAndSteelDispenseBehavior());
        registerBehavior(ItemBlockID.UNDYED_SHULKER_BOX, new ShulkerBoxDispenseBehavior());
        registerBehavior(ItemBlockID.WHITE_SHULKER_BOX, new ShulkerBoxDispenseBehavior());
        registerBehavior(ItemBlockID.ORANGE_SHULKER_BOX, new ShulkerBoxDispenseBehavior());
        registerBehavior(ItemBlockID.MAGENTA_SHULKER_BOX, new ShulkerBoxDispenseBehavior());
        registerBehavior(ItemBlockID.LIGHT_BLUE_SHULKER_BOX, new ShulkerBoxDispenseBehavior());
        registerBehavior(ItemBlockID.YELLOW_SHULKER_BOX, new ShulkerBoxDispenseBehavior());
        registerBehavior(ItemBlockID.LIME_SHULKER_BOX, new ShulkerBoxDispenseBehavior());
        registerBehavior(ItemBlockID.PINK_SHULKER_BOX, new ShulkerBoxDispenseBehavior());
        registerBehavior(ItemBlockID.GRAY_SHULKER_BOX, new ShulkerBoxDispenseBehavior());
        registerBehavior(ItemBlockID.LIGHT_GRAY_SHULKER_BOX, new ShulkerBoxDispenseBehavior());
        registerBehavior(ItemBlockID.CYAN_SHULKER_BOX, new ShulkerBoxDispenseBehavior());
        registerBehavior(ItemBlockID.PURPLE_SHULKER_BOX, new ShulkerBoxDispenseBehavior());
        registerBehavior(ItemBlockID.BLUE_SHULKER_BOX, new ShulkerBoxDispenseBehavior());
        registerBehavior(ItemBlockID.BROWN_SHULKER_BOX, new ShulkerBoxDispenseBehavior());
        registerBehavior(ItemBlockID.GREEN_SHULKER_BOX, new ShulkerBoxDispenseBehavior());
        registerBehavior(ItemBlockID.RED_SHULKER_BOX, new ShulkerBoxDispenseBehavior());
        registerBehavior(ItemBlockID.BLACK_SHULKER_BOX, new ShulkerBoxDispenseBehavior());
        registerBehavior(ItemID.CHICKEN_SPAWN_EGG, new SpawnEggDispenseBehavior(EntityID.CHICKEN));
        registerBehavior(ItemID.COW_SPAWN_EGG, new SpawnEggDispenseBehavior(EntityID.COW));
        registerBehavior(ItemID.PIG_SPAWN_EGG, new SpawnEggDispenseBehavior(EntityID.PIG));
        registerBehavior(ItemID.SHEEP_SPAWN_EGG, new SpawnEggDispenseBehavior(EntityID.SHEEP));
        registerBehavior(ItemID.WOLF_SPAWN_EGG, new SpawnEggDispenseBehavior(EntityID.WOLF));
        registerBehavior(ItemID.VILLAGER_SPAWN_EGG, new SpawnEggDispenseBehavior(EntityID.VILLAGER_V2));
        registerBehavior(ItemID.MOOSHROOM_SPAWN_EGG, new SpawnEggDispenseBehavior(EntityID.MOOSHROOM));
        registerBehavior(ItemID.SQUID_SPAWN_EGG, new SpawnEggDispenseBehavior(EntityID.SQUID));
        registerBehavior(ItemID.RABBIT_SPAWN_EGG, new SpawnEggDispenseBehavior(EntityID.RABBIT));
        registerBehavior(ItemID.BAT_SPAWN_EGG, new SpawnEggDispenseBehavior(EntityID.BAT));
        registerBehavior(ItemID.IRON_GOLEM_SPAWN_EGG, new SpawnEggDispenseBehavior(EntityID.IRON_GOLEM));
        registerBehavior(ItemID.SNOW_GOLEM_SPAWN_EGG, new SpawnEggDispenseBehavior(EntityID.SNOW_GOLEM));
        registerBehavior(ItemID.OCELOT_SPAWN_EGG, new SpawnEggDispenseBehavior(EntityID.OCELOT));
        registerBehavior(ItemID.HORSE_SPAWN_EGG, new SpawnEggDispenseBehavior(EntityID.HORSE));
        registerBehavior(ItemID.DONKEY_SPAWN_EGG, new SpawnEggDispenseBehavior(EntityID.DONKEY));
        registerBehavior(ItemID.MULE_SPAWN_EGG, new SpawnEggDispenseBehavior(EntityID.MULE));
        registerBehavior(ItemID.SKELETON_HORSE_SPAWN_EGG, new SpawnEggDispenseBehavior(EntityID.SKELETON_HORSE));
        registerBehavior(ItemID.ZOMBIE_HORSE_SPAWN_EGG, new SpawnEggDispenseBehavior(EntityID.ZOMBIE_HORSE));
        registerBehavior(ItemID.POLAR_BEAR_SPAWN_EGG, new SpawnEggDispenseBehavior(EntityID.POLAR_BEAR));
        registerBehavior(ItemID.LLAMA_SPAWN_EGG, new SpawnEggDispenseBehavior(EntityID.LLAMA));
        registerBehavior(ItemID.PARROT_SPAWN_EGG, new SpawnEggDispenseBehavior(EntityID.PARROT));
        registerBehavior(ItemID.DOLPHIN_SPAWN_EGG, new SpawnEggDispenseBehavior(EntityID.DOLPHIN));
        registerBehavior(ItemID.ZOMBIE_SPAWN_EGG, new SpawnEggDispenseBehavior(EntityID.ZOMBIE));
        registerBehavior(ItemID.CREEPER_SPAWN_EGG, new SpawnEggDispenseBehavior(EntityID.CREEPER));
        registerBehavior(ItemID.SKELETON_SPAWN_EGG, new SpawnEggDispenseBehavior(EntityID.SKELETON));
        registerBehavior(ItemID.SPIDER_SPAWN_EGG, new SpawnEggDispenseBehavior(EntityID.SPIDER));
        registerBehavior(ItemID.ZOMBIE_PIGMAN_SPAWN_EGG, new SpawnEggDispenseBehavior(EntityID.ZOMBIE_PIGMAN));
        registerBehavior(ItemID.SLIME_SPAWN_EGG, new SpawnEggDispenseBehavior(EntityID.SLIME));
        registerBehavior(ItemID.ENDERMAN_SPAWN_EGG, new SpawnEggDispenseBehavior(EntityID.ENDERMAN));
        registerBehavior(ItemID.SILVERFISH_SPAWN_EGG, new SpawnEggDispenseBehavior(EntityID.SILVERFISH));
        registerBehavior(ItemID.CAVE_SPIDER_SPAWN_EGG, new SpawnEggDispenseBehavior(EntityID.CAVE_SPIDER));
        registerBehavior(ItemID.GHAST_SPAWN_EGG, new SpawnEggDispenseBehavior(EntityID.GHAST));
        registerBehavior(ItemID.MAGMA_CUBE_SPAWN_EGG, new SpawnEggDispenseBehavior(EntityID.MAGMA_CUBE));
        registerBehavior(ItemID.BLAZE_SPAWN_EGG, new SpawnEggDispenseBehavior(EntityID.BLAZE));
        registerBehavior(ItemID.ZOMBIE_VILLAGER_SPAWN_EGG, new SpawnEggDispenseBehavior(EntityID.ZOMBIE_VILLAGER_V2));
        registerBehavior(ItemID.WITCH_SPAWN_EGG, new SpawnEggDispenseBehavior(EntityID.WITCH));
        registerBehavior(ItemID.STRAY_SPAWN_EGG, new SpawnEggDispenseBehavior(EntityID.STRAY));
        registerBehavior(ItemID.HUSK_SPAWN_EGG, new SpawnEggDispenseBehavior(EntityID.HUSK));
        registerBehavior(ItemID.WITHER_SKELETON_SPAWN_EGG, new SpawnEggDispenseBehavior(EntityID.WITHER_SKELETON));
        registerBehavior(ItemID.GUARDIAN_SPAWN_EGG, new SpawnEggDispenseBehavior(EntityID.GUARDIAN));
        registerBehavior(ItemID.ELDER_GUARDIAN_SPAWN_EGG, new SpawnEggDispenseBehavior(EntityID.ELDER_GUARDIAN));
        registerBehavior(ItemID.NPC_SPAWN_EGG, new SpawnEggDispenseBehavior(EntityID.NPC));
        registerBehavior(ItemID.WITHER_SPAWN_EGG, new SpawnEggDispenseBehavior(EntityID.WITHER));
        registerBehavior(ItemID.ENDER_DRAGON_SPAWN_EGG, new SpawnEggDispenseBehavior(EntityID.ENDER_DRAGON));
        registerBehavior(ItemID.SHULKER_SPAWN_EGG, new SpawnEggDispenseBehavior(EntityID.SHULKER));
        registerBehavior(ItemID.ENDERMITE_SPAWN_EGG, new SpawnEggDispenseBehavior(EntityID.ENDERMITE));
        registerBehavior(ItemID.AGENT_SPAWN_EGG, new SpawnEggDispenseBehavior(EntityID.AGENT));
        registerBehavior(ItemID.VINDICATOR_SPAWN_EGG, new SpawnEggDispenseBehavior(EntityID.VINDICATOR));
        registerBehavior(ItemID.PHANTOM_SPAWN_EGG, new SpawnEggDispenseBehavior(EntityID.PHANTOM));
        registerBehavior(ItemID.RAVAGER_SPAWN_EGG, new SpawnEggDispenseBehavior(EntityID.RAVAGER));
        registerBehavior(ItemID.TURTLE_SPAWN_EGG, new SpawnEggDispenseBehavior(EntityID.TURTLE));
        registerBehavior(ItemID.CAT_SPAWN_EGG, new SpawnEggDispenseBehavior(EntityID.CAT));
        registerBehavior(ItemID.EVOKER_SPAWN_EGG, new SpawnEggDispenseBehavior(EntityID.EVOCATION_ILLAGER));
        registerBehavior(ItemID.VEX_SPAWN_EGG, new SpawnEggDispenseBehavior(EntityID.VEX));
        registerBehavior(ItemID.PUFFERFISH_SPAWN_EGG, new SpawnEggDispenseBehavior(EntityID.PUFFERFISH));
        registerBehavior(ItemID.SALMON_SPAWN_EGG, new SpawnEggDispenseBehavior(EntityID.SALMON));
        registerBehavior(ItemID.DROWNED_SPAWN_EGG, new SpawnEggDispenseBehavior(EntityID.DROWNED));
        registerBehavior(ItemID.TROPICAL_FISH_SPAWN_EGG, new SpawnEggDispenseBehavior(EntityID.TROPICALFISH));
        registerBehavior(ItemID.COD_SPAWN_EGG, new SpawnEggDispenseBehavior(EntityID.COD));
        registerBehavior(ItemID.PANDA_SPAWN_EGG, new SpawnEggDispenseBehavior(EntityID.PANDA));
        registerBehavior(ItemID.PILLAGER_SPAWN_EGG, new SpawnEggDispenseBehavior(EntityID.PILLAGER));
        registerBehavior(ItemID.WANDERING_TRADER_SPAWN_EGG, new SpawnEggDispenseBehavior(EntityID.WANDERING_TRADER));
        registerBehavior(ItemID.FOX_SPAWN_EGG, new SpawnEggDispenseBehavior(EntityID.FOX));
        registerBehavior(ItemID.BEE_SPAWN_EGG, new SpawnEggDispenseBehavior(EntityID.BEE));
        registerBehavior(ItemID.PIGLIN_SPAWN_EGG, new SpawnEggDispenseBehavior(EntityID.PIGLIN));
        registerBehavior(ItemID.HOGLIN_SPAWN_EGG, new SpawnEggDispenseBehavior(EntityID.HOGLIN));
        registerBehavior(ItemID.STRIDER_SPAWN_EGG, new SpawnEggDispenseBehavior(EntityID.STRIDER));
        registerBehavior(ItemID.ZOGLIN_SPAWN_EGG, new SpawnEggDispenseBehavior(EntityID.ZOGLIN));
        registerBehavior(ItemID.PIGLIN_BRUTE_SPAWN_EGG, new SpawnEggDispenseBehavior(EntityID.PIGLIN_BRUTE));
        registerBehavior(ItemID.GOAT_SPAWN_EGG, new SpawnEggDispenseBehavior(EntityID.GOAT));
        registerBehavior(ItemID.GLOW_SQUID_SPAWN_EGG, new SpawnEggDispenseBehavior(EntityID.GLOW_SQUID));
        registerBehavior(ItemID.AXOLOTL_SPAWN_EGG, new SpawnEggDispenseBehavior(EntityID.AXOLOTL));
        registerBehavior(ItemID.WARDEN_SPAWN_EGG, new SpawnEggDispenseBehavior(EntityID.WARDEN));
        registerBehavior(ItemID.FROG_SPAWN_EGG, new SpawnEggDispenseBehavior(EntityID.FROG));
        registerBehavior(ItemID.TADPOLE_SPAWN_EGG, new SpawnEggDispenseBehavior(EntityID.TADPOLE));
        registerBehavior(ItemID.ALLAY_SPAWN_EGG, new SpawnEggDispenseBehavior(EntityID.ALLAY));
        registerBehavior(ItemID.CAMEL_SPAWN_EGG, new SpawnEggDispenseBehavior(EntityID.CAMEL));
        registerBehavior(ItemID.SNIFFER_SPAWN_EGG, new SpawnEggDispenseBehavior(EntityID.SNIFFER));
        registerBehavior(ItemID.BREEZE_SPAWN_EGG, new SpawnEggDispenseBehavior(EntityID.BREEZE));
        registerBehavior(ItemID.BOGGED_SPAWN_EGG, new SpawnEggDispenseBehavior(EntityID.BOGGED));
        registerBehavior(ItemID.CREAKING_SPAWN_EGG, new SpawnEggDispenseBehavior(EntityID.CREAKING));
        registerBehavior(ItemID.HAPPY_GHAST_SPAWN_EGG, new SpawnEggDispenseBehavior(EntityID.HAPPY_GHAST));
        registerBehavior(ItemID.COPPER_GOLEM_SPAWN_EGG, new SpawnEggDispenseBehavior(EntityID.COPPER_GOLEM));
        registerBehavior(ItemID.NAUTILUS_SPAWN_EGG, new SpawnEggDispenseBehavior(EntityID.NAUTILUS));
        registerBehavior(ItemID.ZOMBIE_NAUTILUS_SPAWN_EGG, new SpawnEggDispenseBehavior(EntityID.ZOMBIE_NAUTILUS));
        registerBehavior(ItemID.PARCHED_SPAWN_EGG, new SpawnEggDispenseBehavior(EntityID.PARCHED));
        registerBehavior(ItemID.CAMEL_HUSK_SPAWN_EGG, new SpawnEggDispenseBehavior(EntityID.CAMEL_HUSK));
        registerBehavior(ItemID.TRADER_LLAMA_SPAWN_EGG, new SpawnEggDispenseBehavior(EntityID.TRADER_LLAMA));
//        registerBehavior(ItemBlockID.CARVED_PUMPKIN, new PumpkinDispenseBehavior());
//        registerBehavior(ItemBlockID.LIT_PUMPKIN, new PumpkinDispenseBehavior());
//        registerBehavior(ItemBlockID.WITHER_SKELETON_SKULL, new WitherSkullDispenseBehavior());
        registerBehavior(ItemBlockID.TNT, new TNTDispenseBehavior(false));
        registerBehavior(ItemBlockID.UNDERWATER_TNT, new TNTDispenseBehavior(true));
        registerBehavior(ItemID.ARROW, new ProjectileDispenseBehavior(EntityArrow::new) {
            @Override
            protected double getMotion() {
                return super.getMotion() * 1.5;
            }

            @Override
            protected void correctNBT(CompoundTag nbt, Item item) {
                nbt.putByte("auxValue", item.getDamage());

                if (item.getDamage() != ItemArrow.NORMAL_ARROW) {
                    Potion potion = Potion.getPotion(item.getDamage() - ItemArrow.TIPPED_ARROW);
                    if (potion != null) {
                        Effect[] effects = potion.getEffects();
                        ListTag<CompoundTag> mobEffects = new ListTag<>("mobEffects");
                        for (Effect effect : effects) {
                            mobEffects.add(effect.save());
                        }
                        nbt.putList(mobEffects);
                    }
                }
            }
        });
        registerBehavior(ItemID.EGG, new ProjectileDispenseBehavior(EntityEgg::new) {
            @Override
            protected void correctNBT(CompoundTag nbt, Item item) {
                nbt.putCompound("properties", new CompoundTag()
                        .putString(EntityPropertyNames.CLIMATE_VARIANT, EntityPropertyStringValues.CLIMATE_VARIANT_TEMPERATE));
            }
        });
        registerBehavior(ItemID.BLUE_EGG, new ProjectileDispenseBehavior(EntityEgg::new) {
            @Override
            protected void correctNBT(CompoundTag nbt, Item item) {
                nbt.putCompound("properties", new CompoundTag()
                        .putString(EntityPropertyNames.CLIMATE_VARIANT, EntityPropertyStringValues.CLIMATE_VARIANT_COLD));
            }
        });
        registerBehavior(ItemID.BROWN_EGG, new ProjectileDispenseBehavior(EntityEgg::new) {
            @Override
            protected void correctNBT(CompoundTag nbt, Item item) {
                nbt.putCompound("properties", new CompoundTag()
                        .putString(EntityPropertyNames.CLIMATE_VARIANT, EntityPropertyStringValues.CLIMATE_VARIANT_WARM));
            }
        });
        registerBehavior(ItemID.SNOWBALL, new ProjectileDispenseBehavior(EntitySnowball::new));
        registerBehavior(ItemID.EXPERIENCE_BOTTLE, new ProjectileDispenseBehavior(EntityExpBottle::new) {
            @Override
            protected float getAccuracy() {
                return super.getAccuracy() * 0.5f;
            }

            @Override
            protected double getMotion() {
                return super.getMotion() * 1.25;
            }
        });
        registerBehavior(ItemID.SPLASH_POTION, new ProjectileDispenseBehavior(EntityPotion::new) {
            @Override
            protected float getAccuracy() {
                return super.getAccuracy() * 0.5f;
            }

            @Override
            protected double getMotion() {
                return super.getMotion() * 1.25;
            }

            @Override
            protected void correctNBT(CompoundTag nbt, Item item) {
                nbt.putShort("PotionId", item.getDamage());
                nbt.putCompound("Item", NBTIO.putItemHelper(item));
            }
        });
        registerBehavior(ItemID.LINGERING_POTION, new ProjectileDispenseBehavior(EntityLingeringPotion::new) {
            @Override
            protected float getAccuracy() {
                return super.getAccuracy() * 0.5f;
            }

            @Override
            protected double getMotion() {
                return super.getMotion() * 1.25;
            }

            @Override
            protected void correctNBT(CompoundTag nbt, Item item) {
                nbt.putShort("PotionId", item.getDamage());
                nbt.putCompound("Item", NBTIO.putItemHelper(item));
            }
        });
        registerBehavior(ItemID.TRIDENT, new ProjectileDispenseBehavior(EntityThrownTrident::new) {
            @Override
            protected float getAccuracy() {
                return super.getAccuracy() * 0.5f;
            }

            @Override
            protected double getMotion() {
                return super.getMotion() * 1.25;
            }

            @Override
            protected void correctNBT(CompoundTag nbt, Item item) {
                nbt.putCompound("Trident", NBTIO.putItemHelper(item));
            }
        });
        registerBehavior(ItemID.ICE_BOMB, new ProjectileDispenseBehavior(EntityIceBomb::new));
        registerBehavior(ItemID.WIND_CHARGE, new ProjectileDispenseBehavior(EntityWindCharge::new));
        registerBehavior(ItemID.FIRE_CHARGE, new FireChargeDispenseBehavior());
        registerBehavior(ItemID.GLASS_BOTTLE, new GlassBottleDispenseBehavior());
        registerBehavior(ItemID.MINECART, new MinecartDispenseBehavior(EntityMinecartEmpty::new));
        registerBehavior(ItemID.CHEST_MINECART, new MinecartDispenseBehavior(EntityMinecartChest::new));
        registerBehavior(ItemID.HOPPER_MINECART, new MinecartDispenseBehavior(EntityMinecartHopper::new));
        registerBehavior(ItemID.TNT_MINECART, new MinecartDispenseBehavior(EntityMinecartTNT::new));
//        registerBehavior(ItemID.COMMAND_BLOCK_MINECART, new MinecartDispenseBehavior(EntityMinecartCommandBlock::new));
        registerBehavior(ItemID.SHEARS, new ShearsDispenseBehaviour());
        registerBehavior(ItemBlockID.GLOWSTONE, new GlowstoneDispenseBehaviour());
        registerBehavior(ItemID.HONEYCOMB, new HoneycombDispenseBehaviour());
        registerBehavior(ItemID.POTION, new WaterBottleDispenseBehaviour());
        registerBehavior(ItemID.BRUSH, new BrushDispenseBehaviour());
    }
}
