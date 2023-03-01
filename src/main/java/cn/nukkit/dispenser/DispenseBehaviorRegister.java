package cn.nukkit.dispenser;

import cn.nukkit.block.BlockID;
import cn.nukkit.entity.item.*;
import cn.nukkit.entity.projectile.*;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemArrow;
import cn.nukkit.item.ItemID;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.potion.Effect;
import cn.nukkit.potion.Potion;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.Map;

/**
 * @author CreeperFace
 */
public final class DispenseBehaviorRegister {

    private static final Map<Integer, DispenseBehavior> behaviors = new Object2ObjectOpenHashMap<>();
    private static final DispenseBehavior defaultBehavior = new DefaultDispenseBehavior();

    public static void registerBehavior(int itemId, DispenseBehavior behavior) {
        behaviors.put(itemId, behavior);
    }

    public static DispenseBehavior getBehavior(int id) {
        return behaviors.getOrDefault(id, defaultBehavior);
    }

    public static void removeDispenseBehavior(int id) {
        behaviors.remove(id);
    }

    public static void init() {
        registerBehavior(ItemID.BOAT, new BoatDispenseBehavior());
        registerBehavior(ItemID.BUCKET, new BucketDispenseBehavior());
        registerBehavior(ItemID.DYE, new DyeDispenseBehavior());
        registerBehavior(ItemID.FIREWORK_ROCKET, new FireworksDispenseBehavior());
        registerBehavior(ItemID.FLINT_AND_STEEL, new FlintAndSteelDispenseBehavior());
        registerBehavior(BlockID.SHULKER_BOX, new ShulkerBoxDispenseBehavior());
        registerBehavior(BlockID.UNDYED_SHULKER_BOX, new ShulkerBoxDispenseBehavior());
        registerBehavior(ItemID.SPAWN_EGG, new SpawnEggDispenseBehavior());
        registerBehavior(BlockID.TNT, new TNTDispenseBehavior());
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
        //TODO: tipped arrow
        registerBehavior(ItemID.EGG, new ProjectileDispenseBehavior(EntityEgg::new));
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
        });
//        registerBehavior(ItemID.LINGERING_POTION, new ProjectileDispenseBehavior(EntityLingeringPotion::new)); //TODO
        registerBehavior(ItemID.TRIDENT, new ProjectileDispenseBehavior(EntityThrownTrident::new) {
            @Override
            protected float getAccuracy() {
                return super.getAccuracy() * 0.5f;
            }

            @Override
            protected double getMotion() {
                return super.getMotion() * 1.25;
            }
        });
        registerBehavior(ItemID.GLASS_BOTTLE, new GlassBottleDispenseBehavior());
        registerBehavior(ItemID.MINECART, new MinecartDispenseBehavior(EntityMinecartEmpty::new));
        registerBehavior(ItemID.CHEST_MINECART, new MinecartDispenseBehavior(EntityMinecartChest::new));
        registerBehavior(ItemID.HOPPER_MINECART, new MinecartDispenseBehavior(EntityMinecartHopper::new));
        registerBehavior(ItemID.TNT_MINECART, new MinecartDispenseBehavior(EntityMinecartTNT::new));
//        registerBehavior(ItemID.COMMAND_BLOCK_MINECART, new MinecartDispenseBehavior(EntityMinecartCommandBlock::new)); //TODO
    }
}
