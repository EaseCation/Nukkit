package cn.nukkit.inventory.recipe;

import cn.nukkit.inventory.MultiRecipe;
import cn.nukkit.inventory.RecipeTag;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemDye;
import cn.nukkit.item.ItemFirework;
import cn.nukkit.item.ItemFirework.FireworkExplosion.ExplosionType;
import cn.nukkit.item.ItemFireworkStar;
import cn.nukkit.item.ItemID;
import cn.nukkit.nbt.tag.ByteArrayTag;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.bytes.ByteList;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class FireworksRecipe extends MultiRecipe {
    private static final int MAX_INGREDIENTS = 9;
    private static final int MAX_COLORS = 8;
    private static final int BLACK_FIREWORK_COLOR = 0x1d1d21;

    public FireworksRecipe(UUID id) {
        super(id, RecipeTag.CRAFTING_TABLE, Item.get(ItemID.FIREWORK_ROCKET));
    }

    @Nullable
    @Override
    public Item getResult(List<Item> inputs, int repetitions) {
        if (repetitions < 1 || repetitions > 256) {
            return null;
        }
        int gunpowder = 0;
        int paper = 0;
        int shapes = 0;
        int ingredients = 0;
        boolean trail = false;
        boolean flicker = false;
        ExplosionType shape = ExplosionType.SMALL_BALL;
        ByteList colors = new ByteArrayList();
        List<Item> stars = new ArrayList<>();
        for (Item input : inputs) {
            if (input == null || input.getCount() < 0) {
                return null;
            }
            if (input.isNull()) {
                continue;
            }
            if (input.getCount() % repetitions != 0) {
                return null;
            }
            int count = input.getCount() / repetitions;
            if (count > MAX_INGREDIENTS - ingredients) {
                return null;
            }
            ingredients += count;
            DyeColor color = getDyeColor(input);
            if (color != null) {
                for (int index = 0; index < count; index++) {
                    colors.add((byte) color.getDyeData());
                }
            } else if (input.is(ItemID.GUNPOWDER)) {
                gunpowder += count;
            } else if (input.is(ItemID.PAPER)) {
                paper += count;
            } else if (input.is(ItemID.FIREWORK_STAR)) {
                for (int index = 0; index < count; index++) {
                    stars.add(input);
                }
            } else if (input.is(ItemID.DIAMOND)) {
                trail = true;
            } else if (input.is(ItemID.GLOWSTONE_DUST)) {
                flicker = true;
            } else {
                ExplosionType modifier;
                if (input.is(ItemID.FIRE_CHARGE)) {
                    modifier = ExplosionType.LARGE_BALL;
                } else if (input.is(ItemID.GOLD_NUGGET)) {
                    modifier = ExplosionType.STAR_SHAPED;
                } else if (input.is(ItemID.FEATHER)) {
                    modifier = ExplosionType.BURST;
                } else if (input.isSkull()) {
                    modifier = ExplosionType.CREEPER_SHAPED;
                } else {
                    return null;
                }
                shapes += count;
                shape = modifier;
            }
        }
        if (ingredients < 2 || gunpowder > 3 || paper > 1 || shapes > 1 || colors.size() > MAX_COLORS) {
            return null;
        }
        if (paper == 1 && gunpowder > 0 && colors.isEmpty() && !trail && !flicker && shapes == 0) {
            return createRocket(stars, gunpowder, repetitions);
        }
        if (paper == 0 && gunpowder == 1 && stars.isEmpty() && !colors.isEmpty()) {
            return createStar(shape, colors.toByteArray(), new byte[0], trail, flicker, repetitions);
        }
        if (paper == 0 && gunpowder == 0 && stars.size() == 1 && !colors.isEmpty()
                && !trail && !flicker && shapes == 0) {
            CompoundTag explosion = getExplosion(stars.getFirst());
            if (explosion == null) {
                return null;
            }
            return createStar(ExplosionType.getValues()[explosion.getByte("FireworkType")],
                    explosion.getByteArray("FireworkColor"), colors.toByteArray(),
                    explosion.getBoolean("FireworkTrail"), explosion.getBoolean("FireworkFlicker"), repetitions);
        }
        return null;
    }

    private static Item createRocket(List<Item> stars, int flight, int repetitions) {
        ListTag<CompoundTag> explosions = new ListTag<>("Explosions");
        for (Item star : stars) {
            CompoundTag data = getExplosion(star);
            if (data != null) {
                explosions.add(data.clone());
            }
        }
        ItemFirework rocket = new ItemFirework(0, 3 * repetitions);
        rocket.setNamedTag(new CompoundTag().putCompound("Fireworks", new CompoundTag()
                .putByte("Flight", flight).putList(explosions)));
        return rocket;
    }

    private static Item createStar(ExplosionType shape, byte[] colors, byte[] fades, boolean trail, boolean flicker, int repetitions) {
        CompoundTag explosion = new CompoundTag()
                .putByte("FireworkType", shape.ordinal())
                .putByteArray("FireworkColor", colors.clone())
                .putByteArray("FireworkFade", fades.clone())
                .putBoolean("FireworkTrail", trail)
                .putBoolean("FireworkFlicker", flicker);
        ItemFireworkStar star = new ItemFireworkStar(colors[0] & 0xff, repetitions);
        star.setNamedTag(new CompoundTag().putCompound("FireworksItem", explosion)
                .putInt("customColor", getCustomColor(colors)));
        return star;
    }

    @Nullable
    private static CompoundTag getExplosion(Item star) {
        CompoundTag tag = star.getNamedTag();
        if (tag == null || !(tag.get("FireworksItem") instanceof CompoundTag explosion)
                || !(explosion.get("FireworkColor") instanceof ByteArrayTag colors)
                || colors.data == null || colors.data.length < 1 || colors.data.length > MAX_COLORS
                || explosion.getByte("FireworkType") < 0
                || explosion.getByte("FireworkType") >= ExplosionType.getValues().length) {
            return null;
        }
        for (byte color : colors.data) {
            if ((color & 0xff) >= 16) {
                return null;
            }
        }
        return explosion;
    }

    @Nullable
    private static DyeColor getDyeColor(Item item) {
        if (item instanceof ItemDye dye) {
            return dye.getDyeColor();
        }
        return switch (item.getId()) {
            case ItemID.INK_SAC -> DyeColor.BLACK;
            case ItemID.COCOA_BEANS -> DyeColor.BROWN;
            case ItemID.LAPIS_LAZULI -> DyeColor.BLUE;
            case ItemID.BONE_MEAL -> DyeColor.WHITE;
            default -> null;
        };
    }

    private static int getCustomColor(byte[] colors) {
        float red = 0;
        float green = 0;
        float blue = 0;
        for (byte value : colors) {
            DyeColor dye = DyeColor.getByDyeData(value & 0xff);
            int rgb = dye == DyeColor.BLACK ? BLACK_FIREWORK_COLOR : dye.getSignColor().getRGB();
            red += (rgb >> 16 & 0xff) * (1.0f / 255);
            green += (rgb >> 8 & 0xff) * (1.0f / 255);
            blue += (rgb & 0xff) * (1.0f / 255);
        }
        float scale = 1.0f / colors.length;
        return BlockColor.getARGB((int) (red * scale * 255), (int) (green * scale * 255), (int) (blue * scale * 255));
    }
}
