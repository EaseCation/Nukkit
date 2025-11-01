package cn.nukkit.loot.functions;

import cn.nukkit.item.Item;
import cn.nukkit.loot.LootTableContext;
import cn.nukkit.loot.predicates.LootItemCondition;
import cn.nukkit.math.RandomSource;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.ToString;

@ToString(callSuper = true)
public class SetStewEffectFunction extends LootItemFunction {
    private final EffectInfo[] effects;

    @JsonCreator
    public SetStewEffectFunction(
            @JsonProperty("conditions")
            LootItemCondition[] conditions,
            @JsonProperty("effects")
            EffectInfo... effects
    ) {
        super(conditions);
        this.effects = effects;
    }

    @Override
    public Item apply(Item item, RandomSource random, LootTableContext context) {
        if (effects != null && item.is(Item.SUSPICIOUS_STEW)) {
            item.setDamage(effects[random.nextInt(effects.length)].id);
        }
        return item;
    }

    public record EffectInfo(int id) {
    }
}
