package cn.nukkit.loot.functions;

import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.EnchantmentHelper;
import cn.nukkit.loot.LootTableContext;
import cn.nukkit.loot.predicates.LootItemCondition;
import cn.nukkit.math.RandomSource;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.ToString;

@ToString(callSuper = true)
public class EnchantRandomEquipmentFunction extends LootItemFunction {
    private final float chance;

    @JsonCreator
    public EnchantRandomEquipmentFunction(
            @JsonProperty("conditions")
            LootItemCondition[] conditions,
            @JsonProperty("chance")
            float chance
    ) {
        super(conditions);
        this.chance = chance;
    }

    @Override
    public Item apply(Item item, RandomSource random, LootTableContext context) {
        Entity entity = context.getThisEntity();
        if (entity != null) {
            float modifier = entity.level.getSpecialMultiplier();
            if (chance * modifier > random.nextFloat()) {
                EnchantmentHelper.randomlyEnchant(random, item, (int) (modifier * random.nextInt(18)) + 5, false);
            }
        }
        return item;
    }
}
