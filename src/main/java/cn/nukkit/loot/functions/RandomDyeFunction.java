package cn.nukkit.loot.functions;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemColorArmor;
import cn.nukkit.item.ItemHorseArmorLeather;
import cn.nukkit.loot.LootTableContext;
import cn.nukkit.loot.predicates.LootItemCondition;
import cn.nukkit.math.RandomSource;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.ToString;

@ToString(callSuper = true)
public class RandomDyeFunction extends LootItemFunction {
    @JsonCreator
    public RandomDyeFunction(
            @JsonProperty("conditions")
            LootItemCondition[] conditions
    ) {
        super(conditions);
    }

    @Override
    public Item apply(Item item, RandomSource random, LootTableContext context) {
        if (item instanceof ItemColorArmor dyeable) {
            int color = random.nextInt(0xffffff);
            dyeable.setColor(color >> 16, color >> 8 & 0xff, color & 0xff);
        } else if (item instanceof ItemHorseArmorLeather dyeable) {
            int color = random.nextInt(0xffffff);
            dyeable.setColor(color >> 16, color >> 8 & 0xff, color & 0xff);
        }
        return item;
    }
}
