package cn.nukkit.loot.functions;

import cn.nukkit.item.Item;
import cn.nukkit.loot.LootTableContext;
import cn.nukkit.loot.predicates.LootItemCondition;
import cn.nukkit.math.RandomSource;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.ToString;

import javax.annotation.Nullable;

@ToString(callSuper = true)
public class SetItemLoreFunction extends LootItemFunction {
    @Nullable
    private final String[] lore;

    @JsonCreator
    public SetItemLoreFunction(
            @JsonProperty("conditions")
            LootItemCondition[] conditions,
            @JsonProperty("lore")
            String... lore
    ) {
        super(conditions);
        this.lore = lore;
    }

    @Override
    public Item apply(Item item, RandomSource random, LootTableContext context) {
        item.setLore(lore);
        return item;
    }
}
