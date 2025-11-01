package cn.nukkit.loot.functions;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemArrow;
import cn.nukkit.loot.LootTableContext;
import cn.nukkit.loot.predicates.LootItemCondition;
import cn.nukkit.math.RandomSource;
import cn.nukkit.potion.Potion;
import cn.nukkit.potion.Potions;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.ToString;

import java.util.Optional;

@ToString(callSuper = true)
public class SetPotionFunction extends LootItemFunction {
    private final String id;

    private final transient Optional<Potion> parsedPotion;

    @JsonCreator
    public SetPotionFunction(
            @JsonProperty("conditions")
            LootItemCondition[] conditions,
            @JsonProperty("id")
            String id
    ) {
        super(conditions);
        this.id = id;
        parsedPotion = Optional.ofNullable(Potions.getPotionByIdentifier(id));
    }

    @Override
    public Item apply(Item item, RandomSource random, LootTableContext context) {
        parsedPotion.ifPresent(potion -> {
            if (item.isPotion()) {
                item.setDamage(potion.getId());
            } else if (item.is(Item.ARROW)) {
                item.setDamage(potion.getId() + ItemArrow.TIPPED_ARROW);
            }
        });
        return item;
    }
}
