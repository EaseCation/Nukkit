package cn.nukkit.loot.functions;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemArmor;
import cn.nukkit.loot.LootTableContext;
import cn.nukkit.loot.predicates.LootItemCondition;
import cn.nukkit.math.RandomSource;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.ToString;

@ToString(callSuper = true)
public class SetArmorTrimFunction extends LootItemFunction {
    private final String pattern;
    private final String material;

    @JsonCreator
    public SetArmorTrimFunction(
            @JsonProperty("conditions")
            LootItemCondition[] conditions,
            @JsonProperty("pattern")
            String pattern,
            @JsonProperty("material")
            String material
    ) {
        super(conditions);
        this.pattern = pattern;
        this.material = material;
    }

    @Override
    public Item apply(Item item, RandomSource random, LootTableContext context) {
        if (item instanceof ItemArmor armor && pattern != null && material != null) {
            armor.setTrim(pattern, material);
        }
        return item;
    }
}
