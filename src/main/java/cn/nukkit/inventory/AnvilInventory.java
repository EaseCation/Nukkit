package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;

import javax.annotation.Nullable;

import static cn.nukkit.network.protocol.types.UiContainerSlots.*;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class AnvilInventory extends FakeBlockUIComponent {

    public static final int INPUT_SLOT = ANVIL_INPUT - ANVIL_INPUT_OFFSET;
    public static final int INGREDIENT_SLOT = ANVIL_INGREDIENT - ANVIL_INPUT_OFFSET;

    public static final int SIZE = 1 + ANVIL_INPUT_LAST - ANVIL_INPUT_OFFSET;

    private int cost;

    public AnvilInventory(PlayerUIInventory playerUI, Position position) {
        super(playerUI, InventoryType.ANVIL, ANVIL_INPUT_OFFSET, SIZE, position);
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
            this.clear(i);
        }
    }

    @Override
    public void onOpen(Player who) {
        super.onOpen(who);
        who.craftingType = Player.CRAFTING_ANVIL;
        who.recipeTag = RecipeTag.CRAFTING_TABLE;
    }

    public Item getInputSlot() {
        return this.getItem(INPUT_SLOT);
    }

    public Item getMaterialSlot() {
        return this.getItem(INGREDIENT_SLOT);
    }

    /**
     * 铁砧产出结果后由服务端权威消耗输入与材料。
     * 客户端改名会卡出新物品，但 RepairItemAction 是空操作，输入槽必须在这里清掉，否则关窗会把原物退回造成复制。
     * 材料剩余量按交易前快照计算，避免 SlotChangeAction 已扣过一次后再扣。
     *
     * @param consumedMaterial 本次交易声明消耗的材料；纯改名时为 {@code null} 或空物品
     * @param materialBefore   执行客户端槽位变更前的材料槽快照
     */
    public void consumeResultIngredients(@Nullable Item consumedMaterial, @Nullable Item materialBefore) {
        this.clear(INPUT_SLOT);
        if (materialBefore == null) {
            materialBefore = Item.get(Item.AIR);
        }
        if (consumedMaterial == null || consumedMaterial.isNull()) {
            // 纯改名不消耗材料，保持交易前状态
            this.setItem(INGREDIENT_SLOT, materialBefore);
            return;
        }

        int leftoverCount = Math.max(0, materialBefore.getCount() - consumedMaterial.getCount());
        if (leftoverCount <= 0) {
            this.clear(INGREDIENT_SLOT);
            return;
        }
        Item leftover = materialBefore.clone();
        leftover.setCount(leftoverCount);
        this.setItem(INGREDIENT_SLOT, leftover);
    }

    public int getCost() {
        return this.cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }
}
