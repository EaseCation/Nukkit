package cn.nukkit.loot.functions;

import cn.nukkit.Server;
import cn.nukkit.inventory.FurnaceRecipe;
import cn.nukkit.inventory.RecipeTag;
import cn.nukkit.item.Item;
import cn.nukkit.loot.LootTableContext;
import cn.nukkit.loot.predicates.LootItemCondition;
import cn.nukkit.math.RandomSource;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.ToString;

@ToString(callSuper = true)
public class SmeltItemFunction extends LootItemFunction {
    @JsonCreator
    public SmeltItemFunction(
            @JsonProperty("conditions")
            LootItemCondition[] conditions
    ) {
        super(conditions);
    }

    @Override
    public Item apply(Item item, RandomSource random, LootTableContext context) {
        FurnaceRecipe recipe = Server.getInstance().getCraftingManager().matchFurnaceRecipe(item, RecipeTag.FURNACE);
        if (recipe != null) {
            item = recipe.getResult();
        }
        return item;
    }
}
