package cn.nukkit.item.food;

import cn.nukkit.GameVersion;
import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.player.PlayerEatFoodEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemSuspiciousStew;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.potion.Effect;

import java.util.*;

import static cn.nukkit.GameVersion.*;

/**
 * Created by Snake1999 on 2016/1/13.
 * Package cn.nukkit.item.food in project nukkit.
 */
public abstract class Food {

    private static final Map<NodeIDMetaPlugin, Food> registryCustom = new LinkedHashMap<>();
    private static final Map<NodeIDMeta, Food> registryDefault = new LinkedHashMap<>();

    public static final Food apple = registerDefaultFood(new FoodNormal(4, 2.4F).addRelative(Item.APPLE));
    public static final Food apple_golden = registerDefaultFood(new FoodEffective(4, 9.6F)
            .addEffect(Effect.getEffect(Effect.REGENERATION).setAmplifier(1).setDuration(5 * 20))
            .addEffect(Effect.getEffect(Effect.ABSORPTION).setDuration(2 * 60 * 20))
            .addRelative(Item.GOLDEN_APPLE));
    public static final Food apple_golden_enchanted = registerDefaultFood(new FoodEffective(4, 9.6F)
            .addEffect(Effect.getEffect(Effect.REGENERATION).setAmplifier(4).setDuration(30 * 20))
            .addEffect(Effect.getEffect(Effect.ABSORPTION).setDuration(2 * 60 * 20).setAmplifier(3))
            .addEffect(Effect.getEffect(Effect.RESISTANCE).setDuration(5 * 60 * 20))
            .addEffect(Effect.getEffect(Effect.FIRE_RESISTANCE).setDuration(5 * 60 * 20))
            .addRelative(Item.ENCHANTED_GOLDEN_APPLE));
    public static final Food beef_raw = registerDefaultFood(new FoodNormal(3, 1.8F).addRelative(Item.BEEF));
    public static final Food beetroot = registerDefaultFood(new FoodNormal(1, 1.2F).addRelative(Item.BEETROOT));
    public static final Food beetroot_soup = registerDefaultFood(new FoodInBowl(6, 7.2F).addRelative(Item.BEETROOT_SOUP));
    public static final Food bread = registerDefaultFood(new FoodNormal(5, 6F).addRelative(Item.BREAD));
    public static final Food cake_slice = registerDefaultFood(new FoodNormal(2, 0.4F)
            .addRelative(Block.BLOCK_CAKE, 0)
            .addRelative(Block.BLOCK_CAKE, 1)
            .addRelative(Block.BLOCK_CAKE, 2)
            .addRelative(Block.BLOCK_CAKE, 3)
            .addRelative(Block.BLOCK_CAKE, 4)
            .addRelative(Block.BLOCK_CAKE, 5)
            .addRelative(Block.BLOCK_CAKE, 6)
            .addRelative(Block.CANDLE_CAKE)
            .addRelative(Block.WHITE_CANDLE_CAKE)
            .addRelative(Block.ORANGE_CANDLE_CAKE)
            .addRelative(Block.MAGENTA_CANDLE_CAKE)
            .addRelative(Block.LIGHT_BLUE_CANDLE_CAKE)
            .addRelative(Block.YELLOW_CANDLE_CAKE)
            .addRelative(Block.LIME_CANDLE_CAKE)
            .addRelative(Block.PINK_CANDLE_CAKE)
            .addRelative(Block.GRAY_CANDLE_CAKE)
            .addRelative(Block.LIGHT_GRAY_CANDLE_CAKE)
            .addRelative(Block.CYAN_CANDLE_CAKE)
            .addRelative(Block.PURPLE_CANDLE_CAKE)
            .addRelative(Block.BLUE_CANDLE_CAKE)
            .addRelative(Block.BROWN_CANDLE_CAKE)
            .addRelative(Block.GREEN_CANDLE_CAKE)
            .addRelative(Block.RED_CANDLE_CAKE)
            .addRelative(Block.BLACK_CANDLE_CAKE));
    public static final Food carrot = registerDefaultFood(new FoodNormal(3, 4.8F).addRelative(Item.CARROT));
    public static final Food carrot_golden = registerDefaultFood(new FoodNormal(6, 14.4F).addRelative(Item.GOLDEN_CARROT));
    public static final Food chicken_raw = registerDefaultFood(new FoodEffective(2, 1.2F)
            .addChanceEffect(0.3F, Effect.getEffect(Effect.HUNGER).setDuration(30 * 20))
            .addRelative(Item.CHICKEN));
    public static final Food chicken_cooked = registerDefaultFood(new FoodNormal(6, 7.2F).addRelative(Item.COOKED_CHICKEN));
    public static final Food chorus_fruit = registerDefaultFood(new FoodChorusFruit());
    public static final Food cookie = registerDefaultFood(new FoodNormal(2, 0.4F).addRelative(Item.COOKIE));
    public static final Food melon_slice = registerDefaultFood(new FoodNormal(2, 1.2F).addRelative(Item.MELON_SLICE));
    public static final Food milk = registerDefaultFood(new FoodMilk().addRelative(Item.BUCKET, 1));
    public static final Food mushroom_stew = registerDefaultFood(new FoodInBowl(6, 7.2F).addRelative(Item.MUSHROOM_STEW));
    public static final Food mutton_cooked = registerDefaultFood(new FoodNormal(6, 9.6F).addRelative(Item.COOKED_MUTTON));
    public static final Food mutton_raw = registerDefaultFood(new FoodNormal(2, 1.2F).addRelative(Item.MUTTON));
    public static final Food porkchop_cooked = registerDefaultFood(new FoodNormal(8, 12.8F).addRelative(Item.COOKED_PORKCHOP));
    public static final Food porkchop_raw = registerDefaultFood(new FoodNormal(3, 1.8F).addRelative(Item.PORKCHOP));
    public static final Food potato_raw = registerDefaultFood(new FoodNormal(1, 0.6F).addRelative(Item.POTATO));
    public static final Food potato_baked = registerDefaultFood(new FoodNormal(5, 7.2F).addRelative(Item.BAKED_POTATO));
    public static final Food potato_poisonous = registerDefaultFood(new FoodEffective(2, 1.2F)
            .addChanceEffect(0.6F, Effect.getEffect(Effect.POISON).setDuration(4 * 20))
            .addRelative(Item.POISONOUS_POTATO));
    public static final Food pumpkin_pie = registerDefaultFood(new FoodNormal(8, 4.8F).addRelative(Item.PUMPKIN_PIE));
    public static final Food rabbit_cooked = registerDefaultFood(new FoodNormal(5, 6F).addRelative(Item.COOKED_RABBIT));
    public static final Food rabbit_raw = registerDefaultFood(new FoodNormal(3, 1.8F).addRelative(Item.RABBIT));
    public static final Food rabbit_stew = registerDefaultFood(new FoodInBowl(10, 12F).addRelative(Item.RABBIT_STEW));
    public static final Food rotten_flesh = registerDefaultFood(new FoodEffective(4, 0.8F)
            .addChanceEffect(0.8F, Effect.getEffect(Effect.HUNGER).setDuration(30 * 20))
            .addRelative(Item.ROTTEN_FLESH));
    public static final Food spider_eye = registerDefaultFood(new FoodEffective(2, 3.2F)
            .addEffect(Effect.getEffect(Effect.POISON).setDuration(4 * 20))
            .addRelative(Item.SPIDER_EYE));
    public static final Food steak = registerDefaultFood(new FoodNormal(8, 12.8F).addRelative(Item.COOKED_BEEF));
    //different kinds of fishes
    public static final Food clownfish = registerDefaultFood(new FoodNormal(1, 0.2F).addRelative(Item.TROPICAL_FISH));
    public static final Food fish_cooked = registerDefaultFood(new FoodNormal(5, 6F).addRelative(Item.COOKED_COD));
    public static final Food fish_raw = registerDefaultFood(new FoodNormal(2, 0.4F).addRelative(Item.COD));
    public static final Food salmon_cooked = registerDefaultFood(new FoodNormal(6, 9.6F).addRelative(Item.COOKED_SALMON));
    public static final Food salmon_raw = registerDefaultFood(new FoodNormal(2, 0.4F).addRelative(Item.SALMON));
    public static final Food pufferfish = registerDefaultFood(new FoodEffective(1, 0.2F)
            .addEffect(Effect.getEffect(Effect.HUNGER).setAmplifier(2).setDuration(15 * 20))
            .addEffect(Effect.getEffect(Effect.NAUSEA).setAmplifier(1).setDuration(15 * 20))
            .addEffect(Effect.getEffect(Effect.POISON).setAmplifier(3).setDuration(60 * 20))
            .addRelative(Item.PUFFERFISH));
    public static final Food dried_kelp = registerDefaultFood(new FoodNormal(1, 0.6F).addRelative(Item.DRIED_KELP), V1_4_0);
    public static final Food sweet_berries = registerDefaultFood(new FoodNormal(2, 1.2F).addRelative(Item.SWEET_BERRIES), V1_11_0);
    public static final Food poppy_stew = registerDefaultFood(new FoodEffectiveInBowl(6, 7.2f)
            .addEffect(Effect.getEffect(Effect.NIGHT_VISION).setDuration(5 * 20))
            .addRelative(Item.SUSPICIOUS_STEW, ItemSuspiciousStew.POPPY_STEW), V1_13_0);
    public static final Food cornflower_stew = registerDefaultFood(new FoodEffectiveInBowl(6, 7.2f)
            .addEffect(Effect.getEffect(Effect.JUMP_BOOST).setDuration(5 * 20))
            .addRelative(Item.SUSPICIOUS_STEW, ItemSuspiciousStew.CORNFLOWER_STEW), V1_13_0);
    public static final Food tulip_stew = registerDefaultFood(new FoodEffectiveInBowl(6, 7.2f)
            .addEffect(Effect.getEffect(Effect.WEAKNESS).setDuration(8 * 20))
            .addRelative(Item.SUSPICIOUS_STEW, ItemSuspiciousStew.TULIP_STEW), V1_13_0);
    public static final Food azure_bluet_stew = registerDefaultFood(new FoodEffectiveInBowl(6, 7.2f)
            .addEffect(Effect.getEffect(Effect.BLINDNESS).setDuration(7 * 20))
            .addRelative(Item.SUSPICIOUS_STEW, ItemSuspiciousStew.AZURE_BLUET_STEW), V1_13_0);
    public static final Food lily_of_the_valley_stew = registerDefaultFood(new FoodEffectiveInBowl(6, 7.2f)
            .addEffect(Effect.getEffect(Effect.POISON).setDuration(11 * 20))
            .addRelative(Item.SUSPICIOUS_STEW, ItemSuspiciousStew.LILY_OF_THE_VALLEY_STEW), V1_13_0);
    public static final Food dandelion_stew = registerDefaultFood(new FoodEffectiveInBowl(6, 7.2f)
            .addEffect(Effect.getEffect(Effect.SATURATION).setDuration(6))
            .addRelative(Item.SUSPICIOUS_STEW, ItemSuspiciousStew.DANDELION_STEW)
            .addRelative(Item.SUSPICIOUS_STEW, ItemSuspiciousStew.BLUE_ORCHID_STEW), V1_13_0);
    public static final Food allium_stew = registerDefaultFood(new FoodEffectiveInBowl(6, 7.2f)
            .addEffect(Effect.getEffect(Effect.FIRE_RESISTANCE).setDuration(3 * 20))
            .addRelative(Item.SUSPICIOUS_STEW, ItemSuspiciousStew.ALLIUM_STEW), V1_13_0);
    public static final Food oxeye_daisy_stew = registerDefaultFood(new FoodEffectiveInBowl(6, 7.2f)
            .addEffect(Effect.getEffect(Effect.REGENERATION).setDuration(7 * 20))
            .addRelative(Item.SUSPICIOUS_STEW, ItemSuspiciousStew.OXEYE_DAISY_STEW), V1_13_0);
    public static final Food wither_rose_stew = registerDefaultFood(new FoodEffectiveInBowl(6, 7.2f)
            .addEffect(Effect.getEffect(Effect.WITHER).setDuration(7 * 20))
            .addRelative(Item.SUSPICIOUS_STEW, ItemSuspiciousStew.WITHER_ROSE_STEW), V1_13_0);
    public static final Food honey_bottle = registerDefaultFood(new FoodInBottle(6, 1.2F) {
        @Override
        protected boolean onEatenBy(Player player) {
            player.removeEffect(Effect.POISON);
            return super.onEatenBy(player);
        }
    }.addRelative(Item.HONEY_BOTTLE), V1_14_0);

    //Opened API for plugins
    public static Food registerFood(Food food, Plugin plugin) {
        Objects.requireNonNull(food);
        Objects.requireNonNull(plugin);
        food.relativeIDs.forEach(n -> registryCustom.put(new NodeIDMetaPlugin(n.id, n.meta, plugin), food));
        return food;
    }

    private static Food registerDefaultFood(Food food) {
        food.relativeIDs.forEach(n -> registryDefault.put(n, food));
        return food;
    }

    private static Food registerDefaultFood(Food food, GameVersion versions) {
        if (!versions.isAvailable()) {
            return null;
        }
        return registerDefaultFood(food);
    }

    public static Food getByRelative(Item item) {
        Objects.requireNonNull(item);
        return getByRelative(item.getId(), item.getDamage());
    }

    public static Food getByRelative(Block block) {
        Objects.requireNonNull(block);
        return getByRelative(block.getId(), block.getDamage());
    }

    public static Food getByRelative(int relativeID, int meta) {
        final Food[] result = {null};
        registryCustom.forEach((n, f) -> {
            if (n.id == relativeID && n.meta == meta && n.plugin.isEnabled()) result[0] = f;
        });
        if (result[0] == null) {
            registryDefault.forEach((n, f) -> {
                if (n.id == relativeID && n.meta == meta) result[0] = f;
            });
        }
        return result[0];
    }

    protected int restoreFood = 0;
    protected float restoreSaturation = 0;
    protected final List<NodeIDMeta> relativeIDs = new ArrayList<>();

    public final boolean eatenBy(Player player) {
        PlayerEatFoodEvent event = new PlayerEatFoodEvent(player, this);
        player.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) return false;
        return event.getFood().onEatenBy(player);
    }

    protected boolean onEatenBy(Player player) {
        player.getFoodData().addFoodLevel(this);
        return true;
    }

    public Food addRelative(int relativeID) {
        return addRelative(relativeID, 0);
    }

    public Food addRelative(int relativeID, int meta) {
        NodeIDMeta node = new NodeIDMeta(relativeID, meta);
        return addRelative(node);
    }

    private Food addRelative(NodeIDMeta node) {
        if (!relativeIDs.contains(node)) relativeIDs.add(node);
        return this;
    }

    public int getRestoreFood() {
        return restoreFood;
    }

    public Food setRestoreFood(int restoreFood) {
        this.restoreFood = restoreFood;
        return this;
    }

    public float getRestoreSaturation() {
        return restoreSaturation;
    }

    public Food setRestoreSaturation(float restoreSaturation) {
        this.restoreSaturation = restoreSaturation;
        return this;
    }

    static class NodeIDMeta {
        final int id;
        final int meta;

        NodeIDMeta(int id, int meta) {
            this.id = id;
            this.meta = meta;
        }
    }

    static class NodeIDMetaPlugin extends NodeIDMeta {
        final Plugin plugin;

        NodeIDMetaPlugin(int id, int meta, Plugin plugin) {
            super(id, meta);
            this.plugin = plugin;
        }
    }

}
