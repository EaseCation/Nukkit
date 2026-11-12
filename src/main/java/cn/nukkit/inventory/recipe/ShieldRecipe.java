package cn.nukkit.inventory.recipe;

import cn.nukkit.inventory.MultiRecipe;
import cn.nukkit.inventory.RecipeTag;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBanner;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.ItemShield;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.utils.BannerPattern.Type;
import cn.nukkit.utils.DyeColor;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public final class ShieldRecipe extends MultiRecipe {
    public ShieldRecipe(UUID id) {
        super(id, RecipeTag.CRAFTING_TABLE, Item.get(ItemID.SHIELD));
    }

    @Override
    public int getGridIngredientCount() {
        return 2;
    }

    @Override
    @Nullable
    public Item getResult(List<Item> inputs, int repetitions) {
        if (repetitions < 1 || repetitions > 256 || inputs.size() != 2) {
            return null;
        }
        ItemShield shield = null;
        ItemBanner banner = null;
        for (Item input : inputs) {
            if (input.getCount() != repetitions) {
                return null;
            }
            if (input instanceof ItemShield value && shield == null) {
                shield = value;
            } else if (input instanceof ItemBanner value && banner == null) {
                banner = value;
            } else {
                return null;
            }
        }
        if (shield == null || banner == null || shield.hasCompoundTag() && shield.getNamedTag().contains("Base") || banner.getPatternsSize() > 6) {
            return null;
        }
        ItemShield result = (ItemShield) shield.clone();
        CompoundTag tag;
        if (banner.getType() == ItemBanner.TYPE_ILLAGER) {
            // drop all old tags :(
            tag = new CompoundTag().putList(new ListTag<CompoundTag>("Patterns").add(new CompoundTag()
                    .putString("Pattern", Type.PATTERN_ILLAGER.getName())
                    .putInt("Color", DyeColor.WHITE.getDyeData())));
            result.setDamage(0);
        } else {
            tag = result.getOrCreateNamedTag();
            if (banner.hasCompoundTag()) {
                tag.putAll(banner.getNamedTag().copy());
            }
        }
        result.setNamedTag(tag.putInt("Base", banner.getDamage()));
        return result;
    }
}
