package cn.nukkit.loot.functions;

import cn.nukkit.item.Item;
import cn.nukkit.loot.LootTableContext;
import cn.nukkit.loot.predicates.LootItemCondition;
import cn.nukkit.math.RandomSource;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.ToString;

@ToString(callSuper = true)
public class ExplosionDecayFunction extends LootItemFunction {
    @JsonCreator
    public ExplosionDecayFunction(
            @JsonProperty("conditions")
            LootItemCondition[] conditions
    ) {
        super(conditions);
    }

    @Override
    public Item apply(Item item, RandomSource random, LootTableContext context) {
        float erad = context.getExplosionRadius();
        if (erad > 0) {
            float prob = 1 / erad;
            int count = item.getCount();
            int result = 0;
            for (int i = 0; i < count; i++) {
                if (prob >= random.nextFloat()) {
                    result++;
                }
            }
            item.setCount(result);
        } else {
            item.setCount(0);
        }
        return item;
    }
}
