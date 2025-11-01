package cn.nukkit.loot.functions;

import cn.nukkit.item.Item;
import cn.nukkit.loot.LootTableContext;
import cn.nukkit.loot.predicates.LootItemCondition;
import cn.nukkit.loot.providers.NumberProvider;
import cn.nukkit.math.RandomSource;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.ToString;

@ToString(callSuper = true)
public class SetOminousBottleFunction extends LootItemFunction {
    private final NumberProvider amplifier;

    @JsonCreator
    public SetOminousBottleFunction(
            @JsonProperty("conditions")
            LootItemCondition[] conditions,
            @JsonProperty("amplifier")
            NumberProvider amplifier
    ) {
        super(conditions);
        this.amplifier = amplifier;
    }

    @Override
    public Item apply(Item item, RandomSource random, LootTableContext context) {
        if (amplifier != null && item.is(Item.OMINOUS_BOTTLE)) {
            item.setDamage(amplifier.getInt(random));
        }
        return item;
    }
}
