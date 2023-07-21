package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.level.Position;

import java.util.Arrays;

import static cn.nukkit.network.protocol.types.UiContainerSlots.*;

//TODO: Depends on ItemStackNetManager :(
public class SmithingTableInventory extends FakeBlockUIComponent {

    public static final int INPUT_SLOT = SMITHING_TABLE_INPUT - SMITHING_TABLE_INPUT_OFFSET;
    public static final int INGREDIENT_SLOT = SMITHING_TABLE_INGREDIENT - SMITHING_TABLE_INPUT_OFFSET;
    public static final int TEMPLATE_SLOT = SMITHING_TABLE_TEMPLATE - SMITHING_TABLE_INPUT_OFFSET;

    public static final int SIZE = 1 + SMITHING_TABLE_INPUT_LAST - SMITHING_TABLE_INPUT_OFFSET;

    public SmithingTableInventory(PlayerUIInventory playerUI, Position position) {
        super(playerUI, InventoryType.SMITHING_TABLE, SMITHING_TABLE_INPUT_OFFSET, SIZE, position);
    }

    @Override
    public void onClose(Player who) {
        super.onClose(who);
        who.craftingType = Player.CRAFTING_SMALL;
        who.recipeTag = RecipeTag.CRAFTING_TABLE;
        who.resetCraftingGridType();

        for (int i = 0; i < SIZE; ++i) {
            for (Item drop : who.getInventory().addItem(getItem(i))) {
                who.dropItem(drop);
            }
            clear(i);
        }
    }

    @Override
    public void onOpen(Player who) {
        super.onOpen(who);
        who.craftingType = Player.CRAFTING_SMITHING_TABLE;
        who.recipeTag = RecipeTag.CRAFTING_TABLE;
    }

    @Override
    public boolean onTakeResult(Player player, Item result) {
        //TODO: trim
        Item input = getInputSlot();
        Item material = getMaterialSlot();

        int expectedMaterial = Integer.MIN_VALUE;
        switch (input.getId()) {
            case ItemID.DIAMOND_SWORD:
                if (result.getId() == ItemID.NETHERITE_SWORD) {
                    expectedMaterial = ItemID.NETHERITE_INGOT;
                }
                break;
            case ItemID.DIAMOND_SHOVEL:
                if (result.getId() == ItemID.NETHERITE_SHOVEL) {
                    expectedMaterial = ItemID.NETHERITE_INGOT;
                }
                break;
            case ItemID.DIAMOND_PICKAXE:
                if (result.getId() == ItemID.NETHERITE_PICKAXE) {
                    expectedMaterial = ItemID.NETHERITE_INGOT;
                }
                break;
            case ItemID.DIAMOND_AXE:
                if (result.getId() == ItemID.NETHERITE_AXE) {
                    expectedMaterial = ItemID.NETHERITE_INGOT;
                }
                break;
            case ItemID.DIAMOND_HOE:
                if (result.getId() == ItemID.NETHERITE_HOE) {
                    expectedMaterial = ItemID.NETHERITE_INGOT;
                }
                break;
            case ItemID.DIAMOND_HELMET:
                if (result.getId() == ItemID.NETHERITE_HELMET) {
                    expectedMaterial = ItemID.NETHERITE_INGOT;
                }
                break;
            case ItemID.DIAMOND_CHESTPLATE:
                if (result.getId() == ItemID.NETHERITE_CHESTPLATE) {
                    expectedMaterial = ItemID.NETHERITE_INGOT;
                }
                break;
            case ItemID.DIAMOND_LEGGINGS:
                if (result.getId() == ItemID.NETHERITE_LEGGINGS) {
                    expectedMaterial = ItemID.NETHERITE_INGOT;
                }
                break;
            case ItemID.DIAMOND_BOOTS:
                if (result.getId() == ItemID.NETHERITE_BOOTS) {
                    expectedMaterial = ItemID.NETHERITE_INGOT;
                }
                break;
        }
        if (expectedMaterial == Integer.MIN_VALUE) {
            return false;
        }

        return expectedMaterial == material.getId() && input.getDamage() == result.getDamage()
                && Arrays.equals(input.getCompoundTag(), result.getCompoundTag());
    }

    @Override
    public void postTakeResultResolve(Player player) {
        pop(INPUT_SLOT);
        pop(INGREDIENT_SLOT);
        pop(TEMPLATE_SLOT); //TODO
    }

    public Item getInputSlot() {
        return getItem(INPUT_SLOT);
    }

    public Item getMaterialSlot() {
        return getItem(INGREDIENT_SLOT);
    }

    public Item getTemplateSlot() {
        return getItem(TEMPLATE_SLOT);
    }
}
