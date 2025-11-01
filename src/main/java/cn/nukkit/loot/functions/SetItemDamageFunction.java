package cn.nukkit.loot.functions;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemDurable;
import cn.nukkit.loot.LootTableContext;
import cn.nukkit.loot.predicates.LootItemCondition;
import cn.nukkit.loot.providers.NumberProvider;
import cn.nukkit.math.Mth;
import cn.nukkit.math.RandomSource;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.ToString;

@ToString(callSuper = true)
public class SetItemDamageFunction extends LootItemFunction {
    private final NumberProvider damage;

    @JsonCreator
    public SetItemDamageFunction(
            @JsonProperty("conditions")
            LootItemCondition[] conditions,
            @JsonProperty("damage")
            NumberProvider damage
    ) {
        super(conditions);
        this.damage = damage;
    }

    @Override
    public Item apply(Item item, RandomSource random, LootTableContext context) {
        if (damage != null && item instanceof ItemDurable) {
            item.setDamage(Mth.floor((1 - damage.getFloat(random)) * item.getMaxDurability()));
        }
        return item;
    }
}
