package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.level.Position;
import lombok.extern.log4j.Log4j2;

import static cn.nukkit.network.protocol.types.UiContainerSlots.*;

@Log4j2
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
        who.resetCraftingGridType();

        for (int i = 0; i < SIZE; i++) {
            for (Item drop : who.getInventory().addItem(getItem(i))) {
                who.dropItem(drop);
            }
            clear(i);
        }
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
        if (dye.getId() != ItemID.DYE) {
            return false;
        }

        Item pattern = getPatternSlot();
        if (!pattern.isNull() && pattern.getId() != ItemID.BANNER_PATTERN) {
            return false;
        }

        //TODO: more check

        playerUI.setItem(CURSOR, result);
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
}
