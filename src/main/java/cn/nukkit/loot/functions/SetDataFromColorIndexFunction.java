package cn.nukkit.loot.functions;

import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;
import cn.nukkit.loot.LootTableContext;
import cn.nukkit.loot.predicates.LootItemCondition;
import cn.nukkit.math.RandomSource;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.ToString;

@ToString(callSuper = true)
public class SetDataFromColorIndexFunction extends LootItemFunction {
    @JsonCreator
    public SetDataFromColorIndexFunction(
            @JsonProperty("conditions")
            LootItemCondition[] conditions
    ) {
        super(conditions);
    }

    @Override
    public Item apply(Item item, RandomSource random, LootTableContext context) {
        int data = 0;
        Entity thisEntity = context.getThisEntity();
        if (thisEntity != null) {
            int colorIndex = thisEntity.getDataPropertyByte(Entity.DATA_COLOR);
            if (colorIndex >= 0) {
                data = colorIndex & 0xf;
            }
        }
        item.setDamage(data);
        return item;
    }
}
