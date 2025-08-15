package cn.nukkit.dispenser;

import cn.nukkit.entity.item.*;
import cn.nukkit.entity.projectile.*;
import cn.nukkit.entity.property.EntityPropertyNames;
import cn.nukkit.entity.property.EntityPropertyStringValues;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemArrow;
import cn.nukkit.item.ItemBlockID;
import cn.nukkit.item.ItemID;
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
        registerBehavior(ItemID.BOAT, new BoatDispenseBehavior(EntityBoat::new));
        registerBehavior(ItemID.CHEST_BOAT, new BoatDispenseBehavior(EntityBoatChest::new));
        registerBehavior(ItemID.BUCKET, new BucketDispenseBehavior());
        registerBehavior(ItemID.DYE, new FertilizerDispenseBehavior());
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
        registerBehavior(ItemID.SPAWN_EGG, new SpawnEggDispenseBehavior());
//        registerBehavior(ItemBlockID.CARVED_PUMPKIN, new PumpkinDispenseBehavior());
//        registerBehavior(ItemBlockID.LIT_PUMPKIN, new PumpkinDispenseBehavior());
//        registerBehavior(ItemID.SKULL, new SkullDispenseBehavior());
        registerBehavior(ItemBlockID.TNT, new TNTDispenseBehavior());
        registerBehavior(ItemBlockID.UNDERWATER_TNT, new TNTDispenseBehavior());
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
//        registerBehavior(ItemID.WIND_CHARGE, new ProjectileDispenseBehavior(EntityWindCharge::new));
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
