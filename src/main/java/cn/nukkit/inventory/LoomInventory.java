package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBannerPattern;
import cn.nukkit.item.ItemDye;
import cn.nukkit.item.ItemID;
import cn.nukkit.level.Position;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.utils.BannerPattern.Type;

import javax.annotation.Nullable;

import static cn.nukkit.network.protocol.types.UiContainerSlots.*;

public class LoomInventory extends FakeBlockUIComponent {

    public static final int BANNER_SLOT = LOOM_BANNER - LOOM_INPUT_OFFSET;
    public static final int DYE_SLOT = LOOM_DYE - LOOM_INPUT_OFFSET;
    public static final int PATTERN_SLOT = LOOM_PATTERN - LOOM_INPUT_OFFSET;

    public static final int SIZE = 1 + LOOM_INPUT_LAST - LOOM_INPUT_OFFSET;

    public LoomInventory(PlayerUIInventory playerUI, Position position) {
        super(playerUI, InventoryType.LOOM, LOOM_INPUT_OFFSET, SIZE, position);
    }

    @Override
    public void onOpen(Player who) {
        super.onOpen(who);
        who.craftingType = Player.CRAFTING_LOOM;
    }

    @Override
    public void onClose(Player who) {
        super.onClose(who);
        who.craftingType = Player.CRAFTING_SMALL;
        who.recipeTag = RecipeTag.CRAFTING_TABLE;

        if (who.isServerAuthoritativeInventoryEnabled()) {
            return;
        }

        for (int i = 0; i < SIZE; i++) {
            for (Item drop : who.getInventory().addItem(getItem(i))) {
                who.dropItem(drop);
            }
            clear(i);
        }

        who.resetCraftingGridType();
    }

    @Override
    public boolean onTakeResult(Player player, Item result) {
        if (result.getId() != ItemID.BANNER) {
            return false;
        }

        Item banner = getBannerSlot();
        if (banner.getId() != ItemID.BANNER) {
            return false;
        }

        Item dye = getDyeSlot();
        if (!dye.isDye()) {
            return false;
        }

        Item pattern = getPatternSlot();
        if (!pattern.isNull() && !pattern.isBannerPattern()) {
            return false;
        }

        //TODO: more check

        return true;
    }

    @Override
    public void postTakeResultResolve(Player player) {
        pop(BANNER_SLOT);
        pop(DYE_SLOT);
    }

    public Item getBannerSlot() {
        return getItem(BANNER_SLOT);
    }

    public Item getDyeSlot() {
        return getItem(DYE_SLOT);
    }

    public Item getPatternSlot() {
        return getItem(PATTERN_SLOT);
    }

    @Nullable
    public Item getOutput(String patternId) {
        return getOutput(patternId, getBannerSlot(), getDyeSlot(), getPatternSlot());
    }

    @Nullable
    public static Item getOutput(String patternId, Item bannerItem, Item dyeItem, Item patternItem) {
        if (!bannerItem.is(ItemID.BANNER) || bannerItem.isNull()
                || !(dyeItem instanceof ItemDye dye) || dyeItem.isNull()) {
            return null;
        }
        Type type = Type.getByName(patternId);
        if (type == null || type == Type.PATTERN_ILLAGER) {
            return null;
        }
        if (type.getId() != -1) {
            if (!(patternItem instanceof ItemBannerPattern pattern) || patternItem.isNull() || pattern.getPattern() != type) {
                return null;
            }
        } else if (!patternItem.isNull()) {
            return null;
        }
        Item result = bannerItem.clone();
        CompoundTag nbt = result.getOrCreateNamedTag();
        ListTag<CompoundTag> patterns = nbt.getList("Patterns", CompoundTag.class);
        if (patterns.size() >= 6) {
            return null;
        }
        patterns.addCompound(new CompoundTag()
                .putInt("Color", dye.getDyeColor().getDyeData())
                .putString("Pattern", patternId));
        result.setNamedTag(nbt.putList("Patterns", patterns));
        result.setCount(1);
        return result;
    }
}
