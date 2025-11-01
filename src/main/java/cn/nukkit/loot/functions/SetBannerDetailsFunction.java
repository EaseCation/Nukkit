package cn.nukkit.loot.functions;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBanner;
import cn.nukkit.loot.LootTableContext;
import cn.nukkit.loot.predicates.LootItemCondition;
import cn.nukkit.loot.providers.NumberProvider;
import cn.nukkit.math.RandomSource;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.ToString;

@ToString(callSuper = true)
public class SetBannerDetailsFunction extends LootItemFunction {
    private final NumberProvider type;

    @JsonCreator
    public SetBannerDetailsFunction(
            @JsonProperty("conditions")
            LootItemCondition[] conditions,
            @JsonProperty("type")
            NumberProvider type
    ) {
        super(conditions);
        this.type = type;
    }

    @Override
    public Item apply(Item item, RandomSource random, LootTableContext context) {
        if (type != null && item instanceof ItemBanner banner) {
            banner.setType(type.getInt(random));
        }
        return item;
    }
}
